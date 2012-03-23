package org.bongiorno.ariadne;

import org.bongiorno.ariadne.interfaces.Action;
import org.bongiorno.ariadne.interfaces.Predicate;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chbo
 *         Date: May 18, 2010
 *         Time: 3:53:22 PM
 */
public class SideEffectDetectedException extends AriadneException {

    public SideEffectDetectedException() {
    }

    public SideEffectDetectedException(String message) {
        super(message);
    }


    public <T> SideEffectDetectedException(Predicate<?,T> rule, Action<T> cause) {
        super("Action: " + cause + " caused a side effect when applying rule " + rule);
    }
}
