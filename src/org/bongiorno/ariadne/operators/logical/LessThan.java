package org.bongiorno.ariadne.operators.logical;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Dec 14, 2007
 * Time: 10:47:33 AM
 *
* This class implements Less Than logic. However, we don't simply have boolean logic. I know that might seems obvious.
 * We have the case of NULL!!
 * <pre>
 *          N       | 1     | 2
 * ------------------------------
 * N    |   N       | N     | N
 * 1    |   N       | F     | T
 * 2    |   N       | F     | F
 * </pre>
 */

public class LessThan<T extends Comparable<T>> extends LogicalComparableOperator<T> {

    private static final Set<String> REGISTRATIONS = createRegistration("<", "lt","LT");

    /**
     * @inheritDoc
     */
    public LessThan(Integer id) {
        super(id);
    }

    /**
     * performs < on lho and rho based upon comparability
     * @param lho -
     * @param rho -
     * @return (lho == null || rho == null ? null : lho.compareTo(rho) < 0)
     */
    public Boolean evaluate(T lho, T rho) {
        return (lho == null || rho == null ? null : lho.compareTo(rho) < 0);
    }

    /**
     * Styled like java syntax
     * @return '<'
     */
    public String toString() {
        return "<";
    }

    /**
     *
     * @return ['<' , 'lt', 'LT']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
