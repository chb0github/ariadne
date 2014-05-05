package org.bongiorno.ariadne.operators.set;

import org.bongiorno.ariadne.operators.AbstractOperator;

import java.util.HashSet;
import java.util.Set;

/**
 * @author chbo
 * Date: Apr 15, 2008
 * Time: 5:21:30 PM
 *
 * This class represents set symetric difference: (A U B) - (A ^ B)
 */
public class SymmeticDifference<T> extends SetOperator<T> {

    private static final Set<String> REGISTRATIONS = AbstractOperator.createRegistration("symdiff");
    /**
     * @inheritDoc
     */
    public SymmeticDifference(Integer id) {
        super(id);
    }

    /**
     * Think of this as the 'inverse Union'
     * @param lho left hand operand
     * @param rho right hand operand
     * @return The Symmetric Difference between the two sets
     */
    public Set<T> evaluate(Set<T> lho, Set<T> rho) {
        Set<T> a = new HashSet<T>(lho);
        a.removeAll(rho);

        Set<T> b = new HashSet<T>(rho);
        b.removeAll(lho);

        a.addAll(b);
        return a;
    }

    /**
     *
     * @param lho the left hand operand to evaluate for short circuiting
     * @return true only if lho == null
     */
    public Boolean canShortCircuit(Set<T> lho) {
        return lho == null;
    }

    /**
     * @return 'symdiff'
     */
    public String toString() {
        return "symdiff";
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
     * @return ['symdiff']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
