package org.bongiorno.ariadne.operators.set;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Apr 15, 2008
 * Time: 5:21:30 PM
 */
public class SetDifference<T> extends SetOperator<T> {

    private static final Set<String> REGISTRATIONS = createRegistration("setdiff");
    
    /**
     *
     */
    public SetDifference(Integer id) {
        super(id);
    }

    /**
     * This is 'set subtraction' A - B
     * @param lho left hand operand
     * @param rho right hand operand
     * @return lho - rho
     */
    public Set<T> evaluate(Set<T> lho, Set<T> rho) {
        Set<T> retVal = new HashSet<T>(lho);
        retVal.removeAll(rho);

        return retVal;
    }

    /**
     * short circuits only in the case of lho == null
     * @param lho the set to check for null
     * @return lho == null;
     */
    public Boolean canShortCircuit(Set<T> lho) {
        return lho == null;
    }

    /**
     * @return 'setdiff'
     */
    public String toString() {
        return "setdiff";
    }

    /**
     *
     * @return false
     */
    public Boolean isCommutative() {
        return false;
    }

    /**
     *
     * @return ['setdiff']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
