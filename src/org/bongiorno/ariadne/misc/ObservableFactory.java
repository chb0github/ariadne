package org.bongiorno.ariadne.misc;

import java.util.Observable;

import org.bongiorno.ariadne.interfaces.Factory;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Dec 11, 2008
 * Time: 12:11:45 PM
 *
 * This combination class exists so that I can 1) Load via factory, 2) Observe 'this' and 3, because javas  implementation
 * of observable annoys me
 */
public abstract class ObservableFactory<KEY_T,VAL_T> extends Observable implements Factory<KEY_T,VAL_T> {
    @Override

    public void notifyObservers(Object arg) {
        //when using javas version of Observable we have to "setChanged" before we can properly notify. Otherwise
        // a call to 'notifyObservers' doesn't really notify them
        setChanged();
        super.notifyObservers(arg);
    }
}
