package org.bongiorno.ariadne.operators;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.interfaces.Operator;
import org.bongiorno.ariadne.interfaces.Storable;
import org.bongiorno.ariadne.misc.ExceptionOnDuplicateKeyMap;
import org.bongiorno.ariadne.misc.ExceptionOnDuplicateKeySortedMap;
import org.bongiorno.ariadne.misc.NumberKeyedTreeMap;
import org.bongiorno.ariadne.misc.ObservableFactory;
import org.bongiorno.ariadne.operators.arithmetic.ArithmeticOperator;
import org.bongiorno.ariadne.operators.logical.LogicalOperator;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 28, 2007
 * Time: 5:53:29 PM
 * <p/>
 */
public abstract class OperatorFactory extends ObservableFactory<Number, Operator> implements Storable {

    // if we use a TreeMap we must provide a Comparator to sort numbers since oracle
    // creates a different number type depending on DB field
    private final SortedMap<Number, Operator> OPS =
            new ExceptionOnDuplicateKeySortedMap<Number, Operator>(new NumberKeyedTreeMap<Operator>());
    private final Map<String, Operator> OPS_BY_STRING =
            new ExceptionOnDuplicateKeyMap<String, Operator>(new TreeMap<String, Operator>());


    protected OperatorFactory() {

    }


    protected Operator addOperator(Integer id, String className) throws AriadneException {
        Operator op = loadOp(id.intValue(), className);
        OPS.put(id, op);
        Set<String> regs = op.getRegistrations();
        for (String s : regs)
            OPS_BY_STRING.put(s, op);
        // all Operator creation must funnel through this function for proper notification
        notifyObservers(op);
        return op;
    }

    private Integer getId() {
        Integer id = 0;
        if (OPS.size() > 0)
            id = OPS.lastKey().intValue() + 1;
        return id;
    }

    protected Operator loadOp(Integer id, String className) throws AriadneException {
        Class<Operator> clazz = null;
        try {
            clazz = (Class<Operator>) Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new AriadneException(e);
        }
        return loadOp(id, clazz);
    }

    private Operator loadOp(Integer id,Class<Operator> clazz) throws AriadneException {
        Constructor cnst = null;
        Operator operator = null;
        try {
            cnst = clazz.getDeclaredConstructor(Integer.class);
            operator = (Operator) cnst.newInstance(id);
        }
        catch (Exception e) {
            throw new AriadneException(e);
        }
        return operator;
    }

    public Operator get(Number key) throws AriadneException {
        return OPS.get(key.intValue());
    }

    public LogicalOperator getLogicalOperator(String op) {
        return (LogicalOperator) OPS_BY_STRING.get(op);
    }

    public ArithmeticOperator getArithmeticOperator(String op) {
        return (ArithmeticOperator) OPS_BY_STRING.get(op);
    }

    public Operator getOperator(Class<Operator> c) throws AriadneException {
        Operator operator = null;
        try {
            operator = addOperator(getId(), c.getName());
        }
        catch (Exception e) {
            throw new AriadneException(e.toString(),e);
        }
        return operator;
    }

    /**
     * Get the operator whose registration matches 'op'.
     * @param op the registration id to search for
     * @return the Operator if registered by 'op' or null if not found
     */
    public Operator getOperator(String op) {
        return OPS_BY_STRING.get(op);
    }

    /**
     * Get the operator by it's Id.
     * @param id the id of the operator sought
     * @return The Operator if found, null otherwise
     */
    public Operator getOperator(Number id) {
        return OPS.get(id);
    }

    /**
     * Get the set of all currently registered Operators in this KnowledgeBase
     * @return All operators in this KnowledgeBase
     */
    public Set<Operator> getOperators() {
        // there is the potential that this isn't a set, but not while I am implenting it
        return Collections.unmodifiableSet(new HashSet<Operator>(OPS.values()));
    }


}
