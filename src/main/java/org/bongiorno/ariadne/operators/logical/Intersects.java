package org.bongiorno.ariadne.operators.logical;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chbo
 *         Date: Apr 15, 2008
 *         Time: 5:21:30 PM
 *         This class defines a boolean expression of "if A intersects B", that is, if any member of A is in B, or if any
 *         member of B is in A
 *         Note: This class wether or not A intersects B and differs from the class {@see Intersection} class which returns a set of elements that intersect
 */
public class Intersects<T> extends LogicalOperator<Set<T>> {

    private static final Set<String> REGISTRATIONS = createRegistration("intersects");

    /**
     * @inheritDoc
     */
    public Intersects(Integer id) {
        super(id);
    }

    /**
     * Peforms the intersection between the two given sets and returns the result
     *
     * @param lho cmon, do I need to explain this?
     * @param rho cmon, do I need to explain this?
     * @return true if the two sets have any intersection at all
     */
    public Boolean evaluate(Set<T> lho, Set<T> rho) {
        boolean found = false;

        for (Iterator<T> it = lho.iterator(); !found && it.hasNext();)
            found = rho.contains(it.next());

        return found;
    }

    /**
     * if the LHO is null or is empty, then those are your answers respectively
     *
     * @param lho the set to check
     * @return lho == null || lho.isEmpty()
     */
    public Boolean canShortCircuit(Set<T> lho) {
        return lho == null;
    }

    /**
     * @return 'intersects'
     */
    public String toString() {
        return "intersects";
    }

    /**
     * @return true
     * @inheritDoc
     */
    public Boolean isCommutative() {
        return true;
    }

    /**
     * @return ['intersects']
     * @inheritDoc
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }

    /**
     * Technically this method is redundant with the generic in it's signature. However, because we expect that
     * this method will be called with dynamically discovered objects, the generics no longer have meaning
     * @param clazz the class to check for acceptability. Only java.util.Set subclasses will be accepted
     * @return true if clazz represents a subset of java.util.Set
     */
    public Boolean acceptsOperand(Class<Set<T>> clazz) {
        return Set.class.isAssignableFrom(clazz);
    }
}