package org.bongiorno.ariadne.operators.arithmetic;

import java.util.Set;


/**
 * @author chbo
 * Date: Jan 31, 2008
 * Time: 3:54:32 PM
 *
 * This class represents the mathematical concept of A^B A raised to the power B
 */
public class Power extends ArithmeticOperator {
    
    private static final Set<String> REGISTRATIONS = createRegistration("pow", "power");

    /**
     *
     */
    public Power(Integer id) {
        super(id);
    }

    /**
     * executes Math.pow
     * @param lho duh
     * @param rho duh
     * @return Math.pow
     *  Math#pow(double, double)
     */
    protected Double eval(Double lho, Double rho) {
        return Math.pow(lho,rho);
    }

    /**
     * @return 'pow' always
     */
    public String toString() {
        return "pow";
    }

    /**
     * @param lho duh
     * @return super.canShortCircuit(lho) || lho == 1.0d
     *  ArithmeticOperator#canShortCircuit(Double)
     */
    public Boolean canShortCircuit(Double lho) {
        return super.canShortCircuit(lho) || lho == 1.0d;
    }

    /**
     *
     * @return false -- mathematically non-commutative
     */
    public Boolean isCommutative() {
        return false;
    }

    /**
     * @return ['pow', 'power']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
