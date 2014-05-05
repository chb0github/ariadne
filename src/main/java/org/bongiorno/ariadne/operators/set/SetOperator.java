package org.bongiorno.ariadne.operators.set;

import java.util.Set;

import org.bongiorno.ariadne.operators.AbstractOperator;

/**
 * @author chbo
 * Date: Apr 15, 2008
 * Time: 5:20:27 PM
 */
public abstract class SetOperator<T> extends AbstractOperator<Set<T>,Set<T>> {

    /**
     * @inheritDoc
     */
    public SetOperator(Integer id) {
        super(id);
    }

    /**
     * Only accepts sets as input
     * @param clazz The class type to check
     * @return Set.class.isAssignableFrom(clazz)
     */
    public Boolean acceptsOperand(Class<Set<T>> clazz) {
        return Set.class.isAssignableFrom(clazz);
    }

    /**
     * Set operations can only result in sets
     * @return Set.class
     */
    public Class getEvaluationType() {
        return Set.class;
    }
}
