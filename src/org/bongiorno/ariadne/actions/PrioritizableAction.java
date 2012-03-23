package org.bongiorno.ariadne.actions;

import org.bongiorno.ariadne.interfaces.Action;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chbo
 *         Date: Apr 26, 2010
 *         Time: 2:41:48 PM
 */
public interface PrioritizableAction<K extends PrioritizableAction<K,T>,T> extends Action<T>, Comparable<K>{
}
