package org.bongiorno.ariadne.operators.arithmetic;

import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Jan 31, 2008
 * Time: 3:54:32 PM
 */
public class Add extends ArithmeticOperator {

    private static final Set<String> REGISTRATIONS = createRegistration("+", "add", "addition", "plus");

    /**
     * @inheritDoc
     */
    public Add(Integer id) {
        super(id);
    }

    /**
     * used integrated java syntax for adding
     * @param lho self explanatory
     * @param rho self explanatory
     * @return see the java lang spec for the rules governing the adding of doubles
     * @see Double
     */
    protected Double eval(Double lho, Double rho) {
        return lho  + rho;
    }

    /**
     * Returns the java expression of plus
     * @return '+' always
     */
    public String toString() {
        return "+";
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
     * @return '+', 'add', 'addition', 'plus'
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
