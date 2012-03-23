package org.bongiorno.ariadne;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Dec 31, 2007
 * Time: 12:24:49 PM
 *
 * This class is a type wrapper to decouple potential underlying exceptions
 * (IOException and such) in order to hide those details from users of Ariadne
 */
public class AriadneException extends Exception {
    /**
     * Nothing to report here. Move along
     */
    public AriadneException() {
    }
    /**
     * This is classic fair for rolling your own Exception.
     * @param message the message that you would like to have displayed with
     * this exception
     */
    public AriadneException(String message) {
        super(message);
    }
    /**
     * @param message the message to be part of this Exception
     * @param cause the nested cause of this exception
     */
    public AriadneException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause The nested cause of this exception
     */
    public AriadneException(Throwable cause) {
        super(cause);
    }
}
