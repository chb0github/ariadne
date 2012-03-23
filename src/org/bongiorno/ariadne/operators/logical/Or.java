package org.bongiorno.ariadne.operators.logical;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Dec 14, 2007
 * Time: 10:47:57 AM
 * <p/>
 * This class implements OR logic. However, we don't simply have boolean logic. I know that might seems obvious.
 * We have the case of NULL!!
 * <pre>
 *          T       | NULL | F
 * ------------------------------
 * T    |   T       | T     | T
 * NULL |   T       | N     | N
 * R    |   T       | N     | F
 * </pre>
 */
public class Or extends LogicalOperator<Boolean> {

    private static final Set<String> REGISTRATIONS = createRegistration("||", "or", "OR");

    /**
     * @inheritDoc
     */
    public Or(Integer id) {
        super(id);
    }

    /**
     * Peforms a logical or on the two operands
     *
     * @param lho -
     * @param rho -
     * @return lho || rho
     *         <pre>
     *                  T       | NULL | F
     *         ------------------------------
     *         T    |   T       | T     | T
     *         NULL |   T       | N     | N
     *         R    |   T       | N     | F
     *         </pre>
     */
    public Boolean evaluate(Boolean lho, Boolean rho) {
        Boolean retVal = null;

        if (lho != null && rho != null)
            retVal = lho.equals(Boolean.TRUE) || rho.equals(Boolean.TRUE);
        else if (lho == null && Boolean.TRUE.equals(rho))
            retVal = Boolean.TRUE;
        else if (rho == null && Boolean.TRUE.equals(lho))
            retVal = Boolean.TRUE;

        return retVal;
    }

    /**
     * @return ||
     */
    public String toString() {
        return "||";
    }

    /**
     * @param lho
     * @return
     * @inheritDoc
     */
    public Boolean canShortCircuit(Boolean lho) {
        return lho != null && lho;
    }

    /**
     * @return true
     * @inheritDoc
     */
    public Boolean isCommutative() {
        return true;
    }

    /**
     * @return clazz.equals(Boolean.class)
     * @inheritDoc
     */
    public Boolean acceptsOperand(Class<Boolean> clazz) {
        return clazz.equals(Boolean.class);
    }

    /**
     * @return ['or', 'OR', '||']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
