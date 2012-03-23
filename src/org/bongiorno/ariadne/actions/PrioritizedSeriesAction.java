package org.bongiorno.ariadne.actions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bongiorno.ariadne.AriadneException;


/**
 * Created by IntelliJ IDEA.
 *
 * @author chbo
 *         Date: Apr 26, 2010
 *         Time: 2:36:32 PM
 */
public class PrioritizedSeriesAction<K extends PrioritizableAction<K,T>, T> extends SeriesAction<K,T> {

    private Comparator<K> comparator;

    public PrioritizedSeriesAction(Comparator<K> comparator, K ... actions) {
        super(actions);
        this.comparator = comparator;
    }

    public PrioritizedSeriesAction() {
    }

    public PrioritizedSeriesAction(K ... actions) {
        super(actions);
    }

    public PrioritizedSeriesAction(List<K> actions) {
        super(actions);
    }

    /**
     * Takes the current series of actions and prioritizes them by natural order
     * @param context the execution context object.
     * @throws AriadneException A chance for implementing actions to freak out if needed
     */
    public void perform(T context) throws AriadneException {
        if(comparator == null)
            Collections.sort(actions);
        else
            Collections.sort(actions,comparator);
        super.perform(context);
    }


}
