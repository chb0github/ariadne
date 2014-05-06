package org.bongiorno.ariadne.operators.set;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * @author chbo
 * Date: Apr 15, 2008
 * Time: 5:21:30 PM
 */
public class Union<T> extends SetOperator<T> {

    private static final Set<String> REGISTRATIONS = createRegistration("union", "U");

    /**
     *
     */
    public Union(Integer id) {
        super(id);
    }
    /**
     * Peforms the union between the two given sets and returns the result. Neither set is modified
     * @param lho cmon, do I need to explain this?
     * @param rho cmon, do I need to explain this?
     * @return The union set between lho and rho
     */
    public Set<T> evaluate(Set<T> lho, Set<T> rho) {
        Set<T> retVal = new HashSet<T>(lho);
        retVal.addAll(lho);
        retVal.addAll(rho);
        return retVal;
    }

    /**
     * Can only short circuit in the case of lho == null
     * @param lho the set to check against
     * @return lho == null
     */
    public Boolean canShortCircuit(Set<T> lho) {
        return lho == null;
    }

    /**
     * @return 'union'
     */
    public String toString() {
        return "union";
    }

    /**
     *
     * @return true
     */
    public Boolean isCommutative() {
        return true;
    }

    /**
     * @return ['union', 'U']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
