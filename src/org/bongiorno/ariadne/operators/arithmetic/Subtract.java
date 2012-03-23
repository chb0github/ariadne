package org.bongiorno.ariadne.operators.arithmetic;

import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * @author chbo
 * Date: Jan 31, 2008
 * Time: 3:54:32 PM
 *
 * This class represents and provides the mathimatical subtract operator
 */
public class Subtract extends ArithmeticOperator {

    private static final Set<String> REGISTRATIONS = createRegistration("-", "minus", "sub", "subtract");

    /**
     * @inheritDoc
     */
    public Subtract(Integer id) {
        super(id);
    }

    /**
     * As per java syntax subtraction between Doubles
     * @param lho duh
     * @param rho duh
     * @return lho -rho
     * @see Double
     */
    public Double eval(Double lho, Double rho) {
        return lho - rho;
    }

    /**
     *
     * @return '-'
     */
    public String toString() {
        return "-";
    }

    /**
     *
     * @return false -- mathematically non-commutative
     */
    public Boolean isCommutative() {
        return false;
    }

    /**
     *
     * @return ['-', 'minus', 'sub', 'subtract']
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
