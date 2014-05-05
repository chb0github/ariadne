package org.bongiorno.ariadne.operators.set;

import java.util.HashSet;
import java.util.Set;

/**
 * @author chbo
 * Date: Apr 15, 2008
 * Time: 5:21:30 PM
 *
 * Note: This class returns the intersection of A and B and differs from the class {@see Intersects} class which
 * returns true/false
 */
public class Intersection<T> extends SetOperator<T> {

    private static final Set<String> REGISTRATIONS = createRegistration("intersect", "intersection","intersection of");

    /**
     * @inheritDoc
     */
    public Intersection(Integer id) {
        super(id);
    }

    /**
     * Peforms the intersection between the two given sets and returns the result
     * @param lho cmon, do I need to explain this?
     * @param rho cmon, do I need to explain this?
     * @return The intersected set between lho and rho
     */
    public Set<T> evaluate(Set<T> lho, Set<T> rho) {
        Set<T> retVal = new HashSet<T>(lho);
        retVal.addAll(lho);
        retVal.retainAll(rho);

        return retVal;
    }

    /**
     * if the LHO is null or is empty, then those are your answers respectively
     * @param lho the set to check
     * @return lho == null || lho.isEmpty()
     */
    public Boolean canShortCircuit(Set<T> lho) {
        return lho == null || lho.isEmpty();
    }

    /**
     * @return 'intersection of'
     */
    public String toString() {
        return "intersection of";
    }

    /**
     * @inheritDoc
     * @return true
     */
    public Boolean isCommutative() {
        return true;
    }

    /**
     * @inheritDoc
     * @return ['intersect', 'intersection','intersection of']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
