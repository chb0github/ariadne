package org.bongiorno.ariadne.operators.logical;

import java.util.Set;

/**
 * User: chbo
 * Date: Dec 14, 2007
 * Time: 10:48:05 AM
 * <p/>
 * This class implements AND logic. However, we don't simply have boolean logic. I know that might seems obvious.
 * We have the case of NULL!!
 * <pre>
 *          T       | NULL | F
 * ------------------------------
 * T    |   T       | NULL | F
 * NULL |   NULL    | NULL | F
 * R    |   F       | F    | F
 * </pre>
 */
public class And extends LogicalOperator<Boolean> {
    private static final Set<String> REGISTRATIONS = createRegistration("&&", "and","AND");

    /**
     * @inheritDoc
     */
    public And(Integer id) {
        super(id);
    }

    /**
     * <pre>
     *          T       | NULL | F
     * ------------------------------
     * T    |   T       | NULL | F
     * NULL |   NULL    | NULL | F
     * R    |   F       | F    | F
     * </pre>
     * @param lho left hand operand
     * @param rho right hand operand
     * @return see above table
     */
    public Boolean evaluate(Boolean lho, Boolean rho) {
        Boolean retVal = null;

        if (lho != null && rho != null)
            retVal = lho.equals(Boolean.TRUE) && rho.equals(Boolean.TRUE);
        else if (lho == null && Boolean.FALSE.equals(rho))
            retVal = Boolean.FALSE;
        else if (rho == null && Boolean.FALSE.equals(lho))
            retVal = Boolean.FALSE;

        return retVal;
    }

    /**
     * java language expression of 'and'
     * @return always '&&'
     */
    public String toString() {
        return "&&";
    }

    /**
     * Follows boolean arithmetic 0 && X => False
     * @param lho the left hand operand to check for short circuit
     * @return lho != null && !lho
     */
    public Boolean canShortCircuit(Boolean lho) {
        return lho != null && !lho;
    }

    /**
     * Always returns true
     * @return true
     */
    public Boolean isCommutative() {
        return true;
    }

    /**
     * Only Boolean.class is accepted. This is the runtime equivalent to using generics on the evaluate method.
     * Since Ariadne is all about dynamic creation, the generic for this method is just a formality. If you had
     * the type of operator at compile time, then you would obviously know if it's a valid input for this operator
     *
     * @param clazz the class to compare against
     * @return clazz.equals(Boolean.class)
     */
    public Boolean acceptsOperand(Class<Boolean> clazz) {
        return clazz.equals(Boolean.class);
    }

    /**
     *
     * @inheritDoc
     * @return ['and','AND','&&']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
