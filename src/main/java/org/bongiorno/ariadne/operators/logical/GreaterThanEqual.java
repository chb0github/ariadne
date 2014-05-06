package org.bongiorno.ariadne.operators.logical;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Dec 14, 2007
 * Time: 10:47:10 AM
 * <p/>
 * This class implements Greater Than logic. However, we don't simply have boolean logic. I know that might seems obvious.
 * We have the case of NULL!!
 * <pre>
 *          N       | 1     | 2
 * ------------------------------
 * N    |   N       | N     | N
 * 1    |   N       | T     | F
 * 2    |   N       | T     | T
 * </pre>
 */
public class GreaterThanEqual<T extends Comparable<T>> extends LogicalComparableOperator<T> {

    private static final Set<String> REGISTRATIONS = createRegistration(">=", "gte","GTE");

    /**
     *
     */
    public GreaterThanEqual(Integer id) {
        super(id);
    }

    /**
     * Implements >= through comparability
     * @param lho whatever it is, it must be comparable
     * @param rho whatever it is, it must be comparable
     * @return  if either operand is null --> null else lho.compareTo(rho) >= 0
     */
    public Boolean evaluate(T lho, T rho) {
        return (lho == null || rho == null ? null : lho.compareTo(rho) >= 0);
    }

    /**
     * @return '>=' always
     */
    public String toString() {
        return ">=";
    }

    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
