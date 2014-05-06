package org.bongiorno.ariadne.operators.arithmetic;

import org.bongiorno.ariadne.operators.AbstractOperator;


/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Apr 21, 2008
 * Time: 4:46:14 PM
 */
public abstract class ArithmeticOperator extends AbstractOperator<Double,Double> {

    /**
     *
     *
     */
    protected ArithmeticOperator(Integer id) {
        super(id);
    }

    /**
     * Makes sure that both operands are not null and if so, evaluates based upon the coresponding
     * sub-class
     * @param lho duh
     * @param rho suh
     * @return see specific subclasses for results.
     */
    public Double evaluate(Double lho, Double rho) {
        Double val = null;
        if(lho != null && rho != null)
            val = eval(lho,rho);
        return val;
    }
    
    public Boolean canShortCircuit(Double lho) {
        return lho == null;
    }

    /**
     * Call from #evaluate after validation. 
     * @param lho duh
     * @param rho duh
     * @return see specific subtypes
     */
    protected abstract Double eval(Double lho, Double rho);

    /**
     * ArithmeticOperators only deal with doubles.
     * @param clazz the class to check for compatability
     * @return clazz.equals(Double.class)
     */
    public Boolean acceptsOperand(Class clazz) {
        // although Number.isAssignableFrom Double, Integer and Double are not comparable by decree of sun.
        // It would be nice to have something like: RealNumber .... 
        return clazz.equals(Double.class);
    }

    /**
     * ArithmeticOperator only deals with Doubles
     * @return Double.class
     */
    public Class getEvaluationType() {
        return Double.class;
    }
}
