package org.bongiorno.ariadne.operators;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bongiorno.ariadne.interfaces.Operator;

/**
 * @author chbo
 * Date: Jul 2, 2008
 * Time: 4:48:31 PM
 */
public abstract class AbstractOperator<RET_T, OO_T> implements Operator<RET_T,OO_T> {

    private final Integer id;

    /**
     * Accepts the ID assigned from the knowledge base
     * @param id the unique ID associated with a knowledgebase
     */
    protected AbstractOperator(Integer id) {
        this.id = id;
    }

    /**
     * What truly idenifies a functional concept like addition? Two operators may in fact be functionally equivalent
     * but defining f(x) = g(x) in an infinite discrete domain is not possible. Therefore, the class hash is used
     * @return a hash code identifying this operator
     */
    public int hashCode() {
        return this.getClass().hashCode();
    }

    /**
     * The only possible equality is based on class equality. Even if they are (instanceof) equal,
     * that does not mean they are compatible equals due to potentially loading from different class loaders
     * @param oper the operator to compare to
     * @return true if their classes are referentially equal
     */
    public boolean equals(Operator oper) {
        return this.getClass() == oper.getClass();
    }

    /**
     * @see #equals(org.bongiorno.ariadne.interfaces.Operator)
     * @param obj the other Operator to compare to
     * @return true if the classes are equal
     */
    public boolean equals(Object obj) {
        return equals((Operator)obj);
    }

    /**
     * The unique ID of this operator
     * @return an ID uniquely identifying this operator
     */
    public Integer getId() {
        return id;
    }

    protected static Set<String> createRegistration(String ... registrations) {
        Set<String> temp = new HashSet<String>(Arrays.asList(registrations));
        return Collections.unmodifiableSet(temp);
    }
}
