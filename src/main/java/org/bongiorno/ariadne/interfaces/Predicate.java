package org.bongiorno.ariadne.interfaces;

import java.util.List;


/**
 * @author chbo
 * Date: Aug 27, 2007
 * Time: 4:42:05 PM
 * an Operation whose result is true or false. Also maintains action to be run when evaluated to true.
 */
public interface Predicate<OO_T,RUN_T> extends Operation<Boolean, OO_T, RUN_T> {

    /**
     * Adds an action to be executed
     * @param a the action to be added and executed upon this predicated resolving in true
     * @return true if the action was added
     */
    public boolean addAction(Action<RUN_T> a);

    /**
     * Removes a previously inserted action. Only removes 1 action. If the same action was added multiple times
     * then multiple calls may be in order
     * @param a the action to remove
     * @return true if the action was removed
     */
    public boolean removeAction(Action<RUN_T> a);

    /**
     * Retrieves all actions that are to be execute on true
     * @return a list of all actions to be executed. Actions may be added multiple times.
     */
    public List<Action<RUN_T>> getActions();
}
