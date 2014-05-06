package org.bongiorno.ariadne.operators.logical;

import org.bongiorno.ariadne.operators.AbstractOperator;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 23, 2007
 * Time: 10:27:59 AM
 * An interface for LogicalOperators (those that return true or false) and
 * implementations for all the simple Operations
 */
public abstract class LogicalOperator<OO_T> extends AbstractOperator<Boolean, OO_T> {
    /**
     *
     */
    public LogicalOperator(Integer id) {
        super(id);
    }

    /**
     * The definition of a logical Operator is the it's evaluation result in a Boolean
     * @return Boolean.class
     */
    public Class getEvaluationType() {
        return Boolean.class;
    }
}
