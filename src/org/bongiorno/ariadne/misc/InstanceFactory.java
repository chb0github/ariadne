package org.bongiorno.ariadne.misc;


import java.util.Map;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.interfaces.Factory;


/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Oct 12, 2007
 * Time: 6:12:16 PM
 */
public abstract class InstanceFactory<T> implements Factory<String, T> {


    protected Map<Object, Object> objs;


    public InstanceFactory(Map<Object, Object> props) {
        this.objs = props;
    }

    public InstanceFactory() {

    }

    public T get(String type) throws AriadneException {
        load();
        Object retVal = null;
        String fqcn = (String) objs.get(type);
        if (fqcn != null)
            try {
                retVal = Class.forName(fqcn).newInstance();
            }
            catch (Exception e) {
                throw new AriadneException(e.toString(),e);
            }

        return (T) retVal;
    }


    public String toString() {
        return "" + objs;
    }


    public void load() throws AriadneException {
        // where should we load from?
    }
}
