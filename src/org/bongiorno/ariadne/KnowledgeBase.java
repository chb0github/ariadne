package org.bongiorno.ariadne;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bongiorno.ariadne.implementations.JdbcKnowledgeBase;
import org.bongiorno.ariadne.implementations.xml.jaxb.XmlKnowledgeBase;
import org.bongiorno.ariadne.interfaces.Equation;
import org.bongiorno.ariadne.interfaces.Factory;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Operation;
import org.bongiorno.ariadne.interfaces.Storable;
import org.bongiorno.ariadne.misc.InstanceFactory;
import org.bongiorno.ariadne.misc.ObservableFactory;
import org.bongiorno.ariadne.operandowners.OperandOwnerFactory;
import org.bongiorno.ariadne.operations.OperationFactory;
import org.bongiorno.ariadne.operators.OperatorFactory;
import org.bongiorno.ariadne.operators.arithmetic.ArithmeticOperator;
import org.bongiorno.ariadne.operators.logical.LogicalOperator;
import org.bongiorno.ariadne.implementations.EmptyKnowledgeBase;
import org.bongiorno.ariadne.implementations.ReferenceKnowledgeBase;
import org.bongiorno.ariadne.interfaces.Action;
import org.bongiorno.ariadne.interfaces.Loadable;
import org.bongiorno.ariadne.interfaces.Operator;
import org.bongiorno.ariadne.interfaces.Predicate;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Nov 9, 2007
 * Time: 1:43:41 PM
 * <p/>
 * A KnowledgeBase is at the core of what Ariadne does. A KnowledgeBase represents all the knowledge
 * that has so far been accumulated. This includes, but is not limited to: Predicates, Equations and
 * Operations.
 * <p/>
 * There are 3 clutch components to a KnowledgeBase.
 * 1) Operators {@see Operators}
 * 2) OperandOwners {@see OperandOwner}
 * 3) Operations {@see Operations}
 * <p/>
 * The preferred way to load/store data for your KnowledgeBase is to override load()/store()
 * and implement them.
 * <p/>
 * As part of that process you implement the following 3 methods:
 * getOperationFactory()
 * getOperatorFactory()
 * getOperandOwnerFactory()
 * 3 factory classes
 * in each one you return a respective subclass capable of retrieving/parsing/loading their
 * respective parts of a KnowledgeBase. Inside of those factories are protected methods that allow
 * implementors to circumvent client APIs for creating parts of a KnowledgeBase (for example,
 * setting the ID of an OperandOwner, where as a client would have that generated).
 * <p/>
 * Only the Above 3 methods need to be implemented. Everything else is taken care of
 */
public abstract class KnowledgeBase extends Observable implements Loadable, Storable {

    private static final Factory<String, KnowledgeBase> engines = new EngineFactory();
    private OperatorFactory opFact;
    private OperandOwnerFactory ooFact;
    private OperationFactory operationFact;
    private KnowledgeNotifier notifier = new KnowledgeNotifier();

    private boolean sideEffectChecking = false;

    protected KnowledgeBase() {
    }

    /**
     * Returns the PredicateFactory pair with the OperandOwnerFactory and OperatorFactory for this engine
     *
     * @return the PredicateFactory pair with the OperandOwnerFactory and OperatorFactory for this engine
     */
    protected abstract OperationFactory getOperationFactory();

    /**
     * Returns the OperandOwnerFactory pair with the PredicateFactory and OperatorFactory for this engine
     *
     * @return the OperandOwnerFactory pair with the PredicateFactory and OperatorFactory for this engine
     */
    protected abstract OperandOwnerFactory getOperandOwnerFactory();


    /**
     * Returns the OperatorFactory pair with the OperandOwnerFactory and PredicateFactory for this engine
     *
     * @return the OperatorFactory pair with the OperandOwnerFactory and PredicateFactory for this engine
     */
    protected abstract OperatorFactory getOperatorFactory();

    /**
     * Checks to see if all predicates could be constructed (runtime evaluation is not done) and no cycles were found.
     *
     * @return true if all is well.
     */
    public boolean isValid() {
        boolean retVal = false;
        try {
            validate();
            retVal = true;
        }
        catch (AriadneException e) {
            // intentionally ignored
        }
        return retVal;
    }

    @Override
    public void addObserver(Observer o) {
        notifier.addObserver(o);
    }

    /**
     * Loads, in this order: 1) OperatorFactory 2) OperandOwnerFactory 3) OperationFactory
     *
     * @throws AriadneException See specific subtypes for specific errors
     */
    public void load() throws AriadneException {
        opFact = (OperatorFactory) loadFactory(getOperatorFactory());
        ooFact = (OperandOwnerFactory) loadFactory(getOperandOwnerFactory());
        operationFact = (OperationFactory) loadFactory(getOperationFactory());
    }

    private Factory loadFactory(ObservableFactory f) throws AriadneException {
        f.addObserver(notifier);
        f.load();
        return f;
    }

    /**
     * Stores, in this order: 1) OperatoryFactory 2) OperandOwnerFactory 3) OperationFactory
     *
     * @throws AriadneException See specific subtypes for specific errors
     */
    public void store() throws AriadneException {
        opFact.store();
        ooFact.store();
        operationFact.store();
    }

    /**
     * This method is the same as {@link #isValid()} but throws an exception when not valid
     *
     * @throws AriadneException If a predicate couldn't be constructed due to look up failure
     *                          or there was a cycle in any of the predicate trees.
     */
    public void validate() throws AriadneException {
        for (Operation operation : operationFact.getOperations())
            operation.validate();
    }

    public static KnowledgeBase getInstance() throws AriadneException {
        return engines.get("engines.default");
    }

    public static KnowledgeBase getInstance(String source) throws AriadneException {
        return engines.get("engines." + source);
    }

    public Operation getOperation(OperandOwner lho, String op, OperandOwner rho) throws AriadneException {
        return operationFact.getOperation(lho, op, rho);
    }

    public Operation getOperation(OperandOwner lho, Operator op, OperandOwner rho) throws AriadneException {
        return operationFact.getOperation(lho, op, rho);
    }

    public Operation getOperation(Integer id) {
        return operationFact.getOperation(id);
    }

    public Predicate getPredicate(OperandOwner lho, String op, Equation rho) throws AriadneException {
        OperandOwner r = getOperandOwner(rho);
        return operationFact.getPredicate(lho, op, r);
    }

    public Predicate getPredicate(Equation lho, String op, OperandOwner rho) throws AriadneException {
        OperandOwner l = getOperandOwner(lho);
        return operationFact.getPredicate(l, op, rho);
    }

    public Predicate getPredicate(Equation lho, String op, Equation rho) throws AriadneException {
        OperandOwner l = getOperandOwner(lho);
        OperandOwner r = getOperandOwner(rho);
        return operationFact.getPredicate(l, op, r);
    }

    public Predicate getPredicate(Equation lho, Operator op, Equation rho) throws AriadneException {
        return operationFact.getPredicate(lho, op, rho);
    }

    public Predicate getPredicate(Boolean lho, String op, Boolean rho) throws AriadneException {
        return operationFact.getPredicate(lho, op, rho);
    }

    public Equation getEquation(Equation lho, String op, double rho) throws AriadneException {
        return getEquation(lho, op, getOperandOwner(rho));
    }

    public OperandOwner getOperandOwner(Class<? extends OperandOwner> clazz) throws AriadneException {
        return getOperandOwner(clazz, null);
    }

    public boolean isSideEffectChecking() {
        return sideEffectChecking;
    }

    public void setSideEffectChecking(boolean sideEffectChecking) {
        this.sideEffectChecking = sideEffectChecking;
    }

    /**
     * Applies the rules and actions of this knowledgebase. Rules will be applied in interative order
     * as returned by {#getApplicablePredicates()}. If you wish to manipulate the number and order of rules to
     * be applied you must override it there.
     * @param context the runtime context that will be used to apply all operations and actions.
     * @param <T> the run-time conext type
     * @return the set of all predicates applied.
     * @throws AriadneException
     */
    public <T> Set<Predicate<?,T>> apply(T context) throws AriadneException {
        Set<Predicate<?,T>> rulesApplied = new HashSet<Predicate<?,T>>();

        // this should turn into some kinda of "applicable rule iterator"
        for (Predicate<?, T> p : this.getApplicablePredicates()) {

            if (p.evaluate(context)) {
                List<Action<T>> actions = p.getActions();
                for (Action<T> action : actions) {
                    action.perform(context);
                    if (sideEffectChecking && !p.evaluate(context))
                        throw new SideEffectDetectedException(p,action);
                }
                rulesApplied.add(p);
            }
        }
        return rulesApplied;
    }

    /**
     * Override this method to determine which rules/predicates to use when applying this knowledge base to
     * a given context on {#apply}. By default all predicates will be applied
     * @return the set of predicates to apply
     */
    public Set<Predicate> getApplicablePredicates() {
        return this.getPredicates();
    }

    private static class EngineFactory extends InstanceFactory<KnowledgeBase> {
        private static final Map<Object, Object> defaults = new HashMap<Object, Object>();

        static {
            // unless overriden, these defaults apply
            defaults.put("engines.xml", XmlKnowledgeBase.class.getName());
            defaults.put("engines.jdbc", JdbcKnowledgeBase.class.getName());
            defaults.put("engines.default", EmptyKnowledgeBase.class.getName());
            defaults.put("engines.empty", EmptyKnowledgeBase.class.getName());
            defaults.put("engines.reference", ReferenceKnowledgeBase.class.getName());
        }

        private EngineFactory() {
            super(defaults);
        }

        @Override
        public KnowledgeBase get(String type) throws AriadneException {
            // every get performs a refresh on SystemProperties. This will cause override
            load();
            return super.get(type);
        }

        @Override
        public void load() throws AriadneException {
            SortedMap<Object, Object> sysProps = new TreeMap<Object, Object>(System.getProperties());
            SortedMap<Object, Object> overides = sysProps.subMap("engines", "enginest");

            super.objs.putAll(overides);
        }
    }

    public OperandOwner getOperandOwner(String fqcn, java.lang.Object val) throws java.lang.Exception {
        return ooFact.getOperandOwner(fqcn, val);
    }

    public OperandOwner getOperandOwner(Number id) {
        return ooFact.getOperandOwner(id);
    }

    public OperandOwner getOperandOwner(Class<? extends OperandOwner> ooClass, Object val) throws AriadneException {
        return ooFact.getOperandOwner(ooClass, val);
    }

    public OperandOwner getOperandOwner(Operation operation) throws AriadneException {
        return ooFact.getOperandOwner(operation);
    }

    public OperandOwner getOperandOwner(String s) throws AriadneException {
        return ooFact.getOperandOwner(s);
    }

    public OperandOwner getOperandOwner(Date d) throws AriadneException {
        return ooFact.getOperandOwner(d);
    }

    public OperandOwner getOperandOwner(Boolean b) throws AriadneException {
        return ooFact.getOperandOwner(b);
    }

    public OperandOwner getOperandOwner(Double d) throws AriadneException {
        return ooFact.getOperandOwner(d);
    }

    public Set<OperandOwner> getOperandOwners() {
        return ooFact.getOperandOwners();
    }

    public Set<OperandOwner> getOperandOwners(java.lang.Class<? extends OperandOwner> filterClass) {
        return ooFact.getOperandOwners(filterClass);
    }


    public LogicalOperator getLogicalOperator(String op) {
        return (LogicalOperator) opFact.getOperator(op);
    }

    public ArithmeticOperator getArithmeticOperator(String op) {
        return (ArithmeticOperator) opFact.getOperator(op);
    }

    public Operator getOperator(String op) {
        return opFact.getOperator(op);
    }

    public Operator getOperator(Number key) {
        return opFact.getOperator(key);
    }

    Operator getOperator(Class<Operator> type) throws AriadneException {
        return opFact.getOperator(type);
    }

    public Set<Operator> getOperators() {
        return opFact.getOperators();
    }


    public Equation getEquation(Number id) {
        return operationFact.getEquation(id);
    }


    public Equation getEquation(Class opOwnerclass, Object lho, String op, Object rho) throws AriadneException {
        return operationFact.getEquation(opOwnerclass, lho, op, rho);
    }

    public Equation getPredicate(OperandOwner lho, ArithmeticOperator op, OperandOwner rho) throws AriadneException {
        return operationFact.getEquation(lho, op, rho);
    }

    public Equation getEquation(OperandOwner lho, String op, OperandOwner rho) throws AriadneException {
        return operationFact.getEquation(lho, op, rho);
    }

    public Equation getEquation(OperandOwner lho, String op, Equation rho) throws AriadneException {
        OperandOwner r = getOperandOwner(rho);
        return operationFact.getEquation(lho, op, r);
    }

    public Equation getEquation(Equation lho, String op, OperandOwner rho) throws AriadneException {
        OperandOwner l = getOperandOwner(lho);
        return operationFact.getEquation(l, op, rho);
    }

    /**
     * This method takes lho and rho as input and passes them to a NumericOperandOwner
     *
     * @param lho
     * @param op
     * @param rho
     * @return
     * @throws AriadneException
     */
    public Equation getEquation(String lho, String op, String rho) throws AriadneException {
        return operationFact.getEquation(lho, op, rho);
    }

    /**
     * This method takes lho and rho as input and passes them to a NumericOperandOwner
     *
     * @param lho the constant double of the LH OO
     * @param op  the String for the operator to use, as registered by the Operator, for this equation
     * @param rho the constant double for the Right Hand Operand Owner
     * @return an Equation representing the input
     * @throws AriadneException if there was a problem, such as specifying an operator that
     *                          is not suitable for equations
     * @see org.bongiorno.ariadne.interfaces.Operator#getRegistrations()
     */
    public Equation getEquation(Double lho, String op, Double rho) throws AriadneException {
        return operationFact.getEquation(lho, op, rho);
    }

    /**
     * This method takes lho and rho as input and passes them to a NumericOperandOwner
     *
     * @param lho the constant double of the LH OO
     * @param op  the Operator for this equation
     * @param rho the constant double for the Right Hand Operand Owner
     * @return an Equation representing the input
     * @throws AriadneException if there was a problem, such as specifying an operator that
     *                          is not suitable for equations
     */
    public Equation getEquation(Double lho, Operator op, Double rho) throws AriadneException {
        return operationFact.getEquation(lho, op, rho);
    }

    public Equation getEquation(Equation lho, String op, Equation rho) throws AriadneException {
        return operationFact.getEquation(lho, op, rho);
    }

    public Equation getEquation(Equation lho, Operator op, Equation rho) throws AriadneException {
        return operationFact.getEquation(lho, op, rho);
    }

    /**
     * Gets an existing Predicate by it's ID. Null if there is none
     *
     * @param id the id of the pre-existing predicate
     * @return the Predicate if it exists, otherwise null
     */
    public Predicate getPredicate(Integer id) {
        return operationFact.getPredicate(id);
    }

    /**
     * Returns a predicate constructed of operandowners of class 'opOwnClass' who's inputs are, respectively lho and rho
     * Example:
     * getPredicate(NumericOperandOwner.class,10.0d,">",11.1d);
     * the 10.0d and the 11.1d would be feed into seperate instances of NumericOperandOwner
     *
     * @param opOwnClass the operandowner for both left and right hand sides of the expressions
     * @param lho        the input for the left operandowner
     * @param op         the operator to apply (must be logical)
     * @param rho        the input for the right operandowner
     * @return a Predicate representation of the above
     * @throws AriadneException if there was a problem constructing the class
     */
    public Predicate getPredicate(Class opOwnClass, Object lho, String op, Object rho) throws AriadneException {
        return operationFact.getPredicate(opOwnClass, lho, op, rho);
    }

    /**
     * Returns a predicate composed of the 2 operand owners respectively and the operator to be applied
     *
     * @param lho the OperandOwner that will generate the value used for the left side of the expression
     * @param op  the operator to be applied on the results from the operand owners
     * @param rho the OperandOwner that will generate the value used for the left side of the expression
     * @return a Predicate presenting this expression
     * @throws AriadneException if there was a problem returning this predicate
     */
    public Predicate getPredicate(OperandOwner lho, String op, OperandOwner rho) throws AriadneException {
        return operationFact.getPredicate(lho, op, rho);
    }

    /**
     * Returns a predicate composed of the 2 operand owners respectively and the operator to be applied.
     * This is just an alternative method for constructing predicates where the operator is an actual type
     * other than a String
     *
     * @param lho the OperandOwner that will generate the value used for the left side of the expression
     * @param op  the operator to be applied on the results from the operand owners
     * @param rho the OperandOwner that will generate the value used for the left side of the expression
     * @return a Predicate presenting this expression
     * @throws AriadneException if there was a problem returning this predicate
     */
    public Predicate getPredicate(OperandOwner lho, LogicalOperator op, OperandOwner rho) throws AriadneException {
        return operationFact.getPredicate(lho, op, rho);
    }

    /**
     * Returns a predicate composed of the 2 other predicates (thus resulting in a compound predicate)
     *
     * @param lho the Predicate that will generate the value used for the left side of the expression
     * @param op  the operator to be applied on the results from the operand owners
     * @param rho the Predicate that will generate the value used for the left side of the expression
     * @return a Predicate presenting this compound expression
     * @throws AriadneException if there was a problem returning this predicate
     */
    public Predicate getPredicate(Predicate lho, String op, Predicate rho) throws AriadneException {
        return operationFact.getPredicate(lho, op, rho);
    }




    /**
     * returns a set of all operations in this knowledgebase. This includes, but is not limited to
     * Predicates, Equations and set operations. It also returns user-custom types
     *
     * @return all operations defined within this knowledgebase
     */
    public Set<Operation> getOperations() {
        return operationFact.getOperations();
    }

    /**
     * returns all predicates in this Knowledge base (AKA 'Rules').
     *
     * @return a set of all predicates in this knowledge base
     */
    public Set<Predicate> getPredicates() {
        return operationFact.getPredicates();
    }

    public Set<Equation> getEquations() {
        return operationFact.getEquations();
    }

    /**
     * two knowledgebases are equal if they have the set of Operators, OperandOwners and Operations are 'equal'
     * See the respective equals method for those classes
     *
     * @param kb the other Knowledgebase
     * @return
     */
    public boolean equals(Object kb) {
        boolean eq = (kb == this);

        if (!eq) { // if they don't pass a shallow test, do a deep check
            eq = (kb instanceof KnowledgeBase);
            if (eq) {
                KnowledgeBase other = (KnowledgeBase) kb;
                boolean operEq = getOperations().equals(other.getOperations());
                boolean ooEq = getOperandOwners().equals(other.getOperandOwners());

                boolean opEq = getOperators().equals(other.getOperators());

                eq = operEq && ooEq && opEq;
            }
        }
        return eq;
    }

    @Override
    /**
     * Subclasses are encouraged to override.
     * @return a newline delimited string expressing all the operations in this knowledgebase.
     */
    public String toString() {
        StringBuffer buff = new StringBuffer();
        String newLine = System.getProperty("line.separator");

        for (Operation operation : operationFact.getOperations())
            buff.append(operation).append(newLine);

        return buff.toString();
    }

    /**
     * The idea behind this class is that it will do the observing and be the observed. Essentially delegating the
     * observation change to the underlying factories. This allows the internal details of the knowledge base and it's
     * factories to be hidden from the outside world but still tell you what was added.
     */
    private class KnowledgeNotifier extends Observable implements Observer {
        public void update(Observable o, Object arg) {
            setChanged();
            notifyObservers(arg);
        }
    }
}
