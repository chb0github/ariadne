package org.bongiorno.ariadne.misc;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 8, 2008
 * Time: 4:16:43 PM
 */
public class ExceptionOnDuplicateKeyMap<K, V> implements Map<K,V> {
    private final Map<K,V> delegate;

    public ExceptionOnDuplicateKeyMap(Map<K,V> delegate) {
        this.delegate = delegate;
    }
    /**
     * @inheritDoc
     */
    public void clear() {
        delegate.clear();
    }
    /**
     * @inheritDoc
     */
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }
    /**
     * @inheritDoc
     */
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }
    /**
     * @inheritDoc
     */
    public Set<Map.Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }
    /**
     * @inheritDoc
     */
    public V get(Object key) {
        return delegate.get(key);
    }
    /**
     * @inheritDoc
     */
    public boolean isEmpty() {
        return delegate.isEmpty();
    }
    /**
     * @inheritDoc
     */
    public Set<K> keySet() {
        return delegate.keySet();
    }

    /**
     * This method assumes that a valid key will not contain a null value. In the Ariadne world, this is a fact.
     * @param key the key to attempt to put
     * @param value the value to attempt to put
     * @return always null since no prior key would have existed and thus no value
     */
    public V put(K key, V value) {
        V retVal = delegate.put(key, value);
        if(retVal != null) {
            String msg = "Duplicate key " + key + " found. Prior value was ";
            msg += retVal.getClass() + "(" + retVal + ")";
            msg += " new val is " + (value == null ? null : value.getClass()) + "(" + value + ")";
            throw new IllegalArgumentException(msg);
        }
        return retVal;
    }
    /**
     * @inheritDoc
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet())
            put(entry.getKey(),entry.getValue());
    }

    /**
     * @inheritDoc
     */
    public V remove(Object key) {
        return delegate.remove(key);
    }
    /**
     * @inheritDoc
     */
    public int size() {
        return delegate.size();
    }
    /**
     * @inheritDoc
     */
    public Collection<V> values() {
        return delegate.values(); 
    }

}
