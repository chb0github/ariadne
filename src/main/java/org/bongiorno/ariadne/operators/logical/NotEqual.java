package org.bongiorno.ariadne.operators.logical;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Dec 14, 2007
 * Time: 10:46:53 AM
 *
  *
 * This class implements NOT EQUAL logic. However, we don't simply have boolean logic. I know that might seems obvious.
 * We have the case of NULL!!
 * <pre>
 *          T       | NULL | F
 * ------------------------------
 * A    |   F       | T     | T
 * NULL |   T       | F     | T
 * B    |   T       | T     | F
 * </pre>
 */
public class NotEqual<T> extends LogicalOperator<T> {

    private static final Set<String> REGISTRATIONS = createRegistration("!=", "neq","NEQ","<>");

    /**
     *
     */
    public NotEqual(Integer id) {
        super(id);
    }

    public Boolean evaluate(T lho, T rho) {
        // if Lho and Rho are not null and bot referencially equal then call
        return !((lho == rho) || ((lho != null) && lho.equals(rho)));
    }

    public String toString() {
        return "!=";
    }
    public Boolean canShortCircuit(T lho) {
        return lho == null;
    }

    public Boolean isCommutative() {
        return true;
    }

    public Boolean acceptsOperand(Class clazz) {
        return true; // safe by java requirements
    }

    /**
     *
     *
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
