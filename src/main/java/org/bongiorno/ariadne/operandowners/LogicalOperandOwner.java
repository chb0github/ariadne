package org.bongiorno.ariadne.operandowners;

import org.bongiorno.ariadne.interfaces.OperandOwner;


/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 24, 2007
 * Time: 6:01:04 PM
 * This interface is meant only to impose a specific subtype so that only Boolean can result from evaluation
 */
public interface LogicalOperandOwner<RUN_T> extends OperandOwner<Boolean, RUN_T> {
}
