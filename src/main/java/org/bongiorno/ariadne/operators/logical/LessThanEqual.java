package org.bongiorno.ariadne.operators.logical;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Dec 14, 2007
 * Time: 10:47:21 AM
 *
 * <p/>
 * This class implements Less Than Equal to logic. However, we don't simply have boolean logic. I know that might seems obvious.
 * We have the case of NULL!!
 * <pre>
 *          N       | 1     | 2
 * ------------------------------
 * N    |   N       | N     | N
 * 1    |   N       | T     | T
 * 2    |   N       | F     | T
 * </pre>
 */
public class LessThanEqual<T extends Comparable<T>> extends LogicalComparableOperator<T> {

    private static final Set<String> REGISTRATIONS = createRegistration("<=", "lte","LTE");

    /**
     * @inheritDoc
     */
    public LessThanEqual(Integer id) {
        super(id);
    }

    /**
     * Returns lho <= rho as defined by compareTo. Or null if either operand is null
     * @param lho -
     * @param rho -
     * @return (lho == null || rho == null ? null : lho.compareTo(rho) <= 0)
     */
    public Boolean evaluate(T lho, T rho) {
        return (lho == null || rho == null ? null : lho.compareTo(rho) <= 0);
    }

    /**
     *
     * @return '<='
     *
     */
    public String toString() {
        return "<=";
    }

    /**
     * @inheritDoc
     * @return ['lte', 'LTE', '<=']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
