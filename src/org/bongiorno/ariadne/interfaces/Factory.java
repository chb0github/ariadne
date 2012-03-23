package org.bongiorno.ariadne.interfaces;

import org.bongiorno.ariadne.AriadneException;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Nov 12, 2007
 * Time: 4:39:02 PM
 *
 * This interface provides for the dynamic creation of objects. OperationFactory, OperatoryFactory and
 * OperandOwnerFactory are examples of it's usage, but it can also be used to dynamically load and instantiate class
 * files.
 */
public interface Factory<KEY_T,VAL_T> extends Loadable {

    /**
     * @param key the lookup key for this object
     * @return an instantiated version of your object
     * @throws Exception as the implementor sees fit.
     */
    public VAL_T get(KEY_T key) throws AriadneException;
}
