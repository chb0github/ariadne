package org.bongiorno.ariadne.operandowners;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.interfaces.OperandOwner;


/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Oct 16, 2007
 * Time: 4:41:38 PM
 */
public abstract class ConstantOperandOwner<RET_T, RUN_T> implements OperandOwner<RET_T, RUN_T> {

    private RET_T delegate = null;
    private Integer id = null;


    protected ConstantOperandOwner(Integer id, RET_T delegate) {
        this.id = id;
        this.delegate = delegate;
    }

    public RET_T getOperand(RUN_T anyArg) {
        return delegate;
    }

    public Boolean equals(OperandOwner<RET_T, RUN_T> o) {
        return this == o || (o != null && o.getClass() == getClass() && getInput().equals(o.getInput()));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public String toString() {
        return "" + delegate; // let the JVM handle the null condition
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        // obj.getClass() == this.getClass() is an attempt to compare by class loader
        return obj != null && obj.getClass() == this.getClass() && equals((OperandOwner) obj);
    }

    public RET_T getInput() {
        return delegate;
    }


    public Integer getId() {
        return id;
    }

    public void validate() throws AriadneException {
        // intentionally do nothing. Subclasses may override
    }

    public Object getHint() {
        throw new UnsupportedOperationException();

    }

    public Integer getStepCount() {
        return 1;
    }

    public Class getOperandType() {
        return delegate.getClass();
    }

    public Class getRuntimeType() {
        return Object.class; // anytype will do since, as a constant they are never evaluated
    }
}
