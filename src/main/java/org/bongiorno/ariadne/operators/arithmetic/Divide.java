package org.bongiorno.ariadne.operators.arithmetic;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Jan 31, 2008
 * Time: 3:54:32 PM
 *
 * This class represents the concept mathematical operation of division
 */
public class Divide extends ArithmeticOperator {
    
    private static final Set<String> REGISTRATIONS = createRegistration("/", "div","divide");

    /**
     * @inheritDoc
     */
    public Divide(Integer id) {
        super(id);
    }

    /**
     * Just like java '/' for doubles but, in the event of /0 you get null
     * @param lho duh
     * @param rho duh
     * @return what you would expect, except divide 0 => null
     */
    protected Double eval(Double lho, Double rho) {
        Double val = null;
        // a true quandry: what to do with divide by zero?
        if(rho != 0.0d)
            val = lho / rho;
        return val;
    }

    /**
     * @return '/' always
     */
    public String toString() {
        return "/";
    }

    /**
     * @return false
     */
    public Boolean isCommutative() {
        return false;
    }

    /**
     *
     * @return ['/', 'div','divide]
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }
}
