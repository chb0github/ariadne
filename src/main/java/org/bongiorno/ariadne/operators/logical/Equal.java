package org.bongiorno.ariadne.operators.logical;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Dec 14, 2007
 * Time: 10:42:37 AM
 *
 * This class implements EQUAL logic. However, we don't simply have boolean logic. I know that might seems obvious.
 * We have the case of NULL!!
 * <pre>
 *          T       | NULL | F
 * ------------------------------
 * A    |   T       | F     | F
 * NULL |   F       | T     | F
 * B    |   F       | F     | T
 * </pre>
 */
public class Equal<T> extends LogicalOperator<T> {

    private static final Set<String> REGISTRATIONS = createRegistration("==", "eq", "equal");

    /**
     * @inheritDoc
     */
    public Equal(Integer id) {
        super(id);
    }

    /**
     * performs and equaliy check based on Object.equals
     * @param lho -
     * @param rho -
     * @return (lho == rho) || ((lho != null) && lho.equals(rho))
     */
    public Boolean evaluate(T lho, T rho) {
        return (lho == rho) || ((lho != null) && lho.equals(rho));
    }

    /**
     *
     * @return '=='
     */
    public String toString() {
        return "==";
    }

    /**
     * @inheritDoc
     * @return lho = null;
     */
    public Boolean canShortCircuit(T lho) {
        return lho == null;
    }

    /**
     * @inheritDoc
     * @return true
     */
    public Boolean isCommutative() {
        return true;
    }

    /**
     * Always returns true since equals() is type safe per java
     * @param clazz unused
     * @return true
     */
    public Boolean acceptsOperand(Class clazz) {
        return true; // always true since equality is type-safe by java requirements.
    }

    /**
     *
     * @return ['eq', '==','EQ']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
