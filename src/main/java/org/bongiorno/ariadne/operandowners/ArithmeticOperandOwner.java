package org.bongiorno.ariadne.operandowners;

import org.bongiorno.ariadne.interfaces.OperandOwner;


/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 24, 2007
 * Time: 5:58:33 PM
 * This interface is meant only to impose a specific subtype so that only Double can be evaluated
 */
public interface ArithmeticOperandOwner<RUN_T> extends OperandOwner<Double, RUN_T> {
    
}
