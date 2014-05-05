package org.bongiorno.ariadne.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.interfaces.Action;

/**
 *
 * @author chbo
 *         Date: Apr 27, 2010
 *         Time: 11:10:25 AM
 */
public class SeriesAction<A extends Action<T>,T> implements Action<T> {
    protected List<A> actions;

    public SeriesAction(List<A> actions) {
        this.actions = actions;
    }
    
    public SeriesAction(A ... actions) {
        // for this constructor we do not want to leave the implementation to Arrays.asList because
        // it is of unknown type.
        this(new ArrayList<A>(Arrays.asList(actions)));
    }

    public SeriesAction() {
        this(new ArrayList<A>());
    }

    public void perform(T context) throws AriadneException {

        for (Action<T> action : actions)
            action.perform(context);

    }

    public boolean addAction(A action) {
        return this.actions.add(action);
    }

    public boolean removeAction(A action) {
        return this.actions.remove(action);
    }
}
