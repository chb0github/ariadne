package org.bongiorno.ariadne.operators.logical;

import org.bongiorno.ariadne.operators.AbstractOperator;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Dec 14, 2007
 * Time: 10:47:47 AM
 * <p/>
 * This class implements Greater Than logic. However, we don't simply have boolean logic. I know that might seems obvious.
 * We have the case of NULL!!
 * <pre>
 *          N       | 1     | 2
 * ------------------------------
 * N    |   N       | N     | N
 * 1    |   N       | F     | F
 * 2    |   N       | T     | F
 * </pre>
 */

public class GreaterThan<T extends Comparable<T>> extends LogicalComparableOperator<T> {

    private static final Set<String> REGISTRATIONS = AbstractOperator.createRegistration(">", "gt", "GT");

    /**
     * @inheritDoc
     */
    public GreaterThan(Integer id) {
        super(id);
    }

    /**
     * If either operand is null, null is returned. Else, lho.compareTo(rho)
     * @param lho duh
     * @param rho duh
     * @return (lho == null || rho == null ? null : lho.compareTo(rho) > 0)
     */
    public Boolean evaluate(T lho, T rho) {
        return (lho == null || rho == null ? null : lho.compareTo(rho) > 0);

    }

    /**
     * @return >
     */
    public String toString() {
        return ">";
    }

    /**
     * @return ['gt', 'GT', '>']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
