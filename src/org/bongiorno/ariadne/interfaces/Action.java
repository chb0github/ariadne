package org.bongiorno.ariadne.interfaces;

import org.bongiorno.ariadne.AriadneException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chbo
 *         Date: Apr 21, 2010
 *         Time: 10:13:06 AM
 */
public interface Action<T> {

    public void perform(T context) throws AriadneException;
}
