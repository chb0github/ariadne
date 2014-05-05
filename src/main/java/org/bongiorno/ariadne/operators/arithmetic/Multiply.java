package org.bongiorno.ariadne.operators.arithmetic;

import java.util.Set;


/**
 * @author chbo
 * Date: Jan 31, 2008
 * Time: 3:54:32 PM
 *
 * This class represents the mathematical concept of multiplication
 */
public class Multiply extends ArithmeticOperator {
    private static final Set<String> REGISTRATIONS = createRegistration("*", "mul","multiply");

    /**
     * @inheritDoc
     */
    public Multiply(Integer id) {
        super(id);
    }

    /**
     * Multiplies doubles as per java lang spec
     * @param lho duh
     * @param rho duh
     * @return what you would expect
     * @see Double
     */
    public Double eval(Double lho, Double rho) {
        return lho * rho;
    }

    /**
     * Java syntactic expression for multiply
     * @return '*' always
     */
    public String toString() {
        return "*";
    }

    /**
     * if it fails basic null check or lho == 0
     * @param lho duh
     * @return  super.canShortCircuit(lho) || lho == 0.0d
     */
    public Boolean canShortCircuit(Double lho) {
        return super.canShortCircuit(lho) || lho == 0.0d;
    }

    /**
     *
     * @return true
     */
    public Boolean isCommutative() {
        return true;
    }

    /**
     *
     * @return ['*', 'mul', 'multiply']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
