package org.bongiorno.ariadne.operations;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Operation;
import org.bongiorno.ariadne.interfaces.Storable;
import org.bongiorno.ariadne.misc.ExceptionOnDuplicateKeySortedMap;
import org.bongiorno.ariadne.misc.NumberKeyedTreeMap;
import org.bongiorno.ariadne.misc.ObservableFactory;
import org.bongiorno.ariadne.operandowners.BooleanOperandOwner;
import org.bongiorno.ariadne.operandowners.CompoundOperandOwner;
import org.bongiorno.ariadne.operandowners.NumericOperandOwner;
import org.bongiorno.ariadne.operators.arithmetic.ArithmeticOperator;
import org.bongiorno.ariadne.operators.logical.LogicalOperator;
import org.bongiorno.ariadne.interfaces.Action;
import org.bongiorno.ariadne.interfaces.Equation;
import org.bongiorno.ariadne.interfaces.Operator;
import org.bongiorno.ariadne.interfaces.Predicate;


/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 27, 2007
 * Time: 4:40:06 PM
 * Pass in an "id" and get a predicate. This class could drastically change
 */
public abstract class OperationFactory extends ObservableFactory<Integer, Operation> implements Storable {


    private final SortedMap<Number, Operation> OPERATIONS =
            new ExceptionOnDuplicateKeySortedMap<Number, Operation>(new NumberKeyedTreeMap<Operation>());

    private final SortedMap<Number, Predicate> PREDICATES =
            new ExceptionOnDuplicateKeySortedMap<Number, Predicate>(new NumberKeyedTreeMap<Predicate>());

    private final SortedMap<Number, Equation> EQUATIONS =
            new ExceptionOnDuplicateKeySortedMap<Number, Equation>(new NumberKeyedTreeMap<Equation>());

    private final Map<Operation, Integer> OPERATIONS_TO_ID = new HashMap<Operation, Integer>(10000);
    protected KnowledgeBase engine;


    protected OperationFactory(KnowledgeBase engine) {
        this.engine = engine;

    }

    public Operation get(Integer key) throws AriadneException {
        return getPredicate(key);
    }

    public Equation getEquation(Number id) {
        Operation operation = OPERATIONS.get(id.intValue());
        return (Equation) operation;
    }


    protected Equation getEquation(Integer id, OperandOwner lho, Operator op, OperandOwner rho) throws AriadneException {
        return (Equation) getOperation(new FactoryEquation(id, lho, op, rho));
    }

    public Equation getEquation(Class<? extends OperandOwner> opOwnerclass, Object lhoInput, String op, Object rhoInput) throws AriadneException {
        ArithmeticOperator someOp = (ArithmeticOperator) engine.getOperator(op);
        if (someOp == null)
            throw new IllegalArgumentException("No such operator " + op);

        return getEquation(opOwnerclass, lhoInput, someOp, rhoInput);
    }

    public Equation getEquation(Class<? extends OperandOwner> opOwnerclass, Object lhoInput, Operator op, Object rhoInput) throws AriadneException {
        OperandOwner someLHO = engine.getOperandOwner(opOwnerclass, lhoInput);
        OperandOwner someRHO = engine.getOperandOwner(opOwnerclass, rhoInput);

        return getEquation(getId(), someLHO, op, someRHO);
    }

    public Equation getEquation(OperandOwner lho, ArithmeticOperator op, OperandOwner rho) throws AriadneException {
        return getEquation(getId(), lho, op, rho);
    }

    public Equation getEquation(OperandOwner lho, String op, OperandOwner rho) throws AriadneException {
        Operator oper = engine.getOperator(op);
        if(oper == null)
            throw new IllegalArgumentException("No such operator " + op);
        return getEquation(getId(), lho, oper, rho);
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
    public Equation getEquation(Double lho, String op, Double rho) throws AriadneException {
        return getEquation(NumericOperandOwner.class, lho, op, rho);
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
     */
    public Equation getEquation(Double lho, Operator op, Double rho) throws AriadneException {
        return getEquation(NumericOperandOwner.class, lho, op, rho);
    }

    public Equation getEquation(Equation lho, String op, Equation rho) throws AriadneException {
        return getEquation(CompoundOperandOwner.class, lho.getId(), op, rho.getId());
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
    public Equation getEquation(String lho, String op, String rho) throws AriadneException {
        return getEquation(NumericOperandOwner.class, lho, op, rho);
    }

    public Equation getEquation(Equation lho, Operator op, Equation rho) throws AriadneException {
        OperandOwner someLHO = engine.getOperandOwner(CompoundOperandOwner.class, lho.getId());
        OperandOwner someRHO = engine.getOperandOwner(CompoundOperandOwner.class, rho.getId());

        return getEquation(getId(), someLHO, op, someRHO);

    }

    protected Equation getEquation(Integer id, Integer lhoId, Integer opId, Integer rhoId) throws AriadneException {
        Operator op = engine.getOperator(opId);
        if (op == null)
            throw new IllegalArgumentException("No such operator " + op);

        OperandOwner lho = engine.getOperandOwner(lhoId);
        OperandOwner rho = engine.getOperandOwner(rhoId);

        return getEquation(id, lho, op, rho);
    }


    public Predicate getPredicate(Class opOwnClass, Object lho, String op, Object rho) throws AriadneException {
        Operator someOp = engine.getOperator(op);
        if (someOp == null)
            throw new IllegalArgumentException("No such operator " + op);

        return getPredicate(opOwnClass, lho, someOp, rho);
    }

    public Predicate getPredicate(Class opOwnClass, Object lho, Operator op, Object rho) throws AriadneException {

        OperandOwner someLHO = engine.getOperandOwner(opOwnClass, lho);
        OperandOwner someRHO = engine.getOperandOwner(opOwnClass, rho);

        return getPredicate(getId(), someLHO, op, someRHO);
    }

    public Predicate getPredicate(OperandOwner lho, String op, OperandOwner rho) throws AriadneException {
        return getPredicate(getId(), lho, engine.getOperator(op), rho);
    }

    public Predicate getPredicate(OperandOwner lho, Operator op, OperandOwner rho) throws AriadneException {
        return getPredicate(getId(), lho, op, rho);
    }


    public Predicate getPredicate(Predicate lho, String op, Predicate rho) throws AriadneException {
        return getPredicate(CompoundOperandOwner.class, lho.getId(), op, rho.getId());

    }

    private Operation getOperation(Operation operation) throws AriadneException {
        // create a test predicate
        // look to see if it has an id
        Number previous = OPERATIONS_TO_ID.get(operation);
        // if it doesn't, add it
        return (previous == null ? addOperation(operation) : OPERATIONS.get(previous));
    }

    public Predicate getPredicate(Integer id) {
        return (Predicate) OPERATIONS.get(id);
    }

    protected Predicate getPredicate(Integer id, OperandOwner lho, Operator op, OperandOwner rho) throws AriadneException {
        return (Predicate) getOperation(new FactoryPredicate(id, lho, op, rho));
    }

    protected Predicate getPredicate(Integer id, Integer lhoId, Integer opId, Integer rhoId) throws AriadneException {
        OperandOwner lho = engine.getOperandOwner(lhoId);
        LogicalOperator op = (LogicalOperator) engine.getOperator(opId);
        OperandOwner rho = engine.getOperandOwner(rhoId);

        return getPredicate(id, lho, op, rho);
    }


    private Integer getId() {
        Integer id = 0;
        if (OPERATIONS.size() > 0)
            id = OPERATIONS.lastKey().intValue() + 1;
        return id;
    }

    private Operation addOperation(Operation operation) throws RuntimeException, AriadneException {
        OPERATIONS.put(operation.getId(), operation);

        if(operation instanceof Predicate)
            PREDICATES.put(operation.getId(),(Predicate)operation);
        
        if(operation instanceof Equation)
            EQUATIONS.put(operation.getId(),(Equation)operation);

        OPERATIONS_TO_ID.put(operation, operation.getId());

        if (OPERATIONS.size() != OPERATIONS_TO_ID.size())
            throw new RuntimeException("Operation data stores must be the same size!");

        engine.getOperandOwner(operation);// make sure the predicate is registered
        notifyObservers(operation);
        return operation;
    }


    public Set<Operation> getOperations() {
        return Collections.unmodifiableSet(new LinkedHashSet<Operation>(OPERATIONS.values()));
    }

    /**
     * returns all predicates associated with this knowledge base. This method is used
     * in the application of the rules and actions contained within this KB. To change the priority of execution
     * it is sufficient to simply change the iterative order. 
     * @return
     */
    public Set<Predicate> getPredicates() {
        return Collections.unmodifiableSet(new LinkedHashSet<Predicate>(PREDICATES.values()));
    }

    public Set<Equation> getEquations() {
        return Collections.unmodifiableSet(new LinkedHashSet<Equation>(EQUATIONS.values()));
    }

    public Operation getOperation(Integer id) {
        return OPERATIONS.get(id);
    }


    protected Operation getOperation(Integer id, OperandOwner lho, Operator op, OperandOwner rho) throws AriadneException {
        Operation retVal = null;
        // although these are, in no functional way, different from eachother, they are typed different
        // and instantiated different. I am not completely sure this is the right approach or if I should just ditch
        // the concept of a predicate and equation and simply go with Operation for everything
        if (op.getEvaluationType().equals(Boolean.class))
            retVal = getPredicate(id, lho, op, rho);
        else {
            if (op.getEvaluationType().equals(Double.class))
                retVal = getEquation(id, lho, op, rho);
            else
                retVal = getOperation(new FactoryOperation(id, lho, op, rho));
        }
        return retVal;
    }

    public Operation getOperation(OperandOwner lho, String op, OperandOwner rho) throws AriadneException {
        Operator oper = engine.getOperator(op);
        if(oper == null)
            throw new IllegalArgumentException("Operator " + op + "not found");
        return getOperation(lho, oper,rho);
    }

    public Operation getOperation(OperandOwner lho, Operator op, OperandOwner rho) throws AriadneException {
        return getOperation(getId(),lho,op,rho);
    }

    protected Operation getOperation(Number id, Number lhoId, Number opId, Number rhoId) throws AriadneException {
        return getOperation(id.intValue(), lhoId.intValue(), opId.intValue(), rhoId.intValue());
    }

    protected Operation getOperation(Integer id, Integer lhoId, Integer opId, Integer rhoId) throws AriadneException {
        OperandOwner l = engine.getOperandOwner(lhoId);
        Operator o = engine.getOperator(opId);
        OperandOwner r = engine.getOperandOwner(rhoId);

        return getOperation(id, l, o, r);
    }

    public Predicate getPredicate(Boolean lho, String op, Boolean rho) throws AriadneException {
        LogicalOperator o = engine.getLogicalOperator(op);
        if (o == null)
            throw new AriadneException("Operator " + op + " not found");
        OperandOwner<Boolean, ?> l = engine.getOperandOwner(BooleanOperandOwner.class, lho);
        OperandOwner<Boolean, ?> r = engine.getOperandOwner(BooleanOperandOwner.class, rho);
        return getPredicate(l, o, r);
    }

    public Predicate getPredicate(Equation lho, String op, Equation rho) throws AriadneException {
        return getPredicate(CompoundOperandOwner.class, lho.getId(), op, rho.getId());
    }

    public Predicate getPredicate(Equation lho, Operator op, Equation rho) throws AriadneException {
        return getPredicate(CompoundOperandOwner.class, lho.getId(), op, rho.getId());
    }

    @Override
    public String toString() {
        String newline = System.getProperty("line.separator");
        StringBuffer buff = new StringBuffer();
        for (Operation operation : getOperations())
            buff.append(operation).append(newline);

        return buff.toString();
    }

    private static class FactoryOperation<OO_T, RUN_T> extends UnTypedOperation<Object, OO_T, RUN_T> {
        public FactoryOperation(Integer id, OperandOwner<OO_T, RUN_T> lho, Operator<Object, OO_T> op, OperandOwner<OO_T, RUN_T> rho) {
            super(id, lho, op, rho);
        }
    }

    private class FactoryPredicate<OO_T, RUN_T> extends UnTypedOperation<Boolean, OO_T, RUN_T> implements Predicate<OO_T, RUN_T> {

        private List<Action<RUN_T>> actions = new LinkedList<Action<RUN_T>>();

        public FactoryPredicate(Integer id, OperandOwner<OO_T, RUN_T> lho, Operator<Boolean, OO_T> op, OperandOwner<OO_T, RUN_T> rho) {
            super(id, lho, op, rho);

        }
 

        public boolean addAction(Action<RUN_T> a) {
            return actions.add(a);
        }

        public boolean removeAction(Action<RUN_T> a) {
            return actions.remove(a);
        }

        public List<Action<RUN_T>> getActions() {
            return Collections.unmodifiableList(actions);
        }


    }

    
    private  class FactoryEquation<RUN_T> extends UnTypedOperation<Double, Double, RUN_T> implements Equation<Double, RUN_T> {

        public FactoryEquation(Integer id, OperandOwner<Double, RUN_T> lho, Operator<Double, Double> op, OperandOwner<Double, RUN_T> rho) {
            super(id, lho, op, rho);
        }


        public Class<Double> getReturnType() {
            return Double.class;
        }
    }
}
