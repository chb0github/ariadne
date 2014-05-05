package org.bongiorno.ariadne.operandowners;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.interfaces.OperandOwner;

/**
 * Created by IntelliJ IDEA.
* User: chbo
* Date: Mar 25, 2009
* Time: 10:53:13 AM
 * 
 * puts a minimal burden on OperandOwner subclassors and implementors
*/
public abstract class MinimalOperandOwner<RET_T, RUN_T> implements OperandOwner<RET_T, RUN_T> {

    private Integer id = null;

    public MinimalOperandOwner(Integer id) {
        this.id = id;
    }

    public Object getHint() {
        return null;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return equals((OperandOwner)obj);
    }

    public Boolean equals(OperandOwner o) {
        if(o == null)
            return false;
        Object thisIn = getInput();
        Object thatIn = o.getInput();
        return this == o || (getClass() == o.getClass() &&
                (thisIn == thatIn || thisIn != null && thisIn.equals(thatIn)));
    }

    public Integer getId() {
        return id;
    }

    public Object getInput() {
        return null;
    }

    public void validate() throws AriadneException {

    }
}
