package org.bongiorno.ariadne.interfaces;

import org.bongiorno.ariadne.AriadneException;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Apr 15, 2008
 * Time: 4:42:40 PM
 *
 * This interface formalizes the loading aspect of many portions of Ariadne. KnowledgeBases load, Factories load
 * etc. The correlary is the Storable interface
 */
public interface Loadable {

    /**
     * Loads from whatever subtype data store is represented by 'this'
     *
     * @throws org.bongiorno.ariadne.AriadneException if an underlying load failed
     */
    public void load() throws AriadneException;
}
