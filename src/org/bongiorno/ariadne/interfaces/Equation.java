package org.bongiorno.ariadne.interfaces;


/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 23, 2007
 * Time: 10:51:55 AM
 * An equation represents an arithmetic runtime evaluation and is compatible with use in logical expressions
 * <p/>
 * The way to use this class is to specify the runtime evaluation argument as the Generic type and then compose an
 * equation of ArithmeticOperandOwners.
 *
 * This interface is merely a convenience interface and insists on no extra functionality.
 */
public interface Equation<OO_T,RUN_T> extends Operation<Double, OO_T, RUN_T> {
 
}
