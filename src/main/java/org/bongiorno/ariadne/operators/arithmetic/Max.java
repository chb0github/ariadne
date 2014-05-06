package org.bongiorno.ariadne.operators.arithmetic;

import java.util.Set;

/**
 * @author chbo
 * Date: Jan 31, 2008
 * Time: 3:54:32 PM
 *
 * This class represents the concept of max of A and B
 */
public class Max extends ArithmeticOperator {
    
    private static final Set<String> REGISTRATIONS = createRegistration("max", "maximum");

    /**
     *
     */
    public Max(Integer id) {
        super(id);
    }

    /**
     * @return Math.max
     *  Math#max(double, double)
     */
    public Double eval(Double lho, Double rho) {
        return Math.max(lho, rho);
    }

    /**
     *
     * @return 'max' always
     */
    public String toString() {
        return "max";
    }

    /**
     * Always true
     * @return true
     */
    public Boolean isCommutative() {
        return true;
    }

    /**
     *
     * @return 'max','maximum
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
