package org.bongiorno.ariadne.actions;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.interfaces.Action;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chbo
 *         Date: May 12, 2010
 *         Time: 12:10:34 PM
 */
public class SimplePrioritizableAction<T> implements PrioritizableAction<SimplePrioritizableAction<T>,T>{
    private int ordinal = 0;
    private Action<T> delegate = null;

    public SimplePrioritizableAction(int ordinal, Action<T> delegate) {
        this.delegate = delegate;
        this.ordinal = ordinal;
    }

    public int compareTo(SimplePrioritizableAction<T> o) {
        return this.ordinal - o.ordinal;
    }

    public void perform(T context) throws AriadneException {
        delegate.perform(context);
    }
}
