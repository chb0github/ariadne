package org.bongiorno.ariadne.operators.arithmetic;

import java.util.Set;

/**
 * @author chbo
 * Date: Jan 31, 2008
 * Time: 3:54:32 PM
 *
 * This class represents the concept of min of A and B
 */
public class Min extends ArithmeticOperator {
    
    private static final Set<String> REGISTRATIONS = createRegistration("min", "minimum");

    /**
     * @inheritDoc
     */
    public Min(Integer id) {
        super(id);
    }

    /**
     * Returns as per Math.min
     * @param lho duh
     * @param rho duh
     * @return Math.min
     * @see Math#min(double, double) 
     */
    protected Double eval(Double lho, Double rho) {
        return Math.min(lho,rho);
    }

    /**
     * @return min
     */
    public String toString() {
        return "min";
    }

    /**
     * @return true
     */
    public Boolean isCommutative() {
        return true;
    }

    /**
     * @return ['min', 'minimum']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
