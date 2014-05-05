package org.bongiorno.ariadne.interfaces;

import org.bongiorno.ariadne.AriadneException;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Jun 30, 2008
 * Time: 1:45:50 PM
 */
public interface Storable {

    /**
     * functionally  excapsulate the concept of store();
     * @throws org.bongiorno.ariadne.AriadneException as determined by implementors
     */
    public void store() throws AriadneException;
}
