package org.bongiorno.ariadne.misc;

import java.util.Comparator;
import java.util.SortedMap;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 8, 2008
 * Time: 4:16:43 PM
 */
public class ExceptionOnDuplicateKeySortedMap<K, V> extends ExceptionOnDuplicateKeyMap<K, V> implements SortedMap<K, V> {
    private SortedMap<K, V> delegate = null;

    public ExceptionOnDuplicateKeySortedMap(SortedMap<K, V> delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    public Comparator<? super K> comparator() {
        return delegate.comparator();
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return delegate.subMap(fromKey, toKey);
    }

    public SortedMap<K, V> headMap(K toKey) {
        return delegate.headMap(toKey);
    }

    public SortedMap<K, V> tailMap(K fromKey) {
        return delegate.tailMap(fromKey);
    }

    public K firstKey() {
        return delegate.firstKey();
    }

    public K lastKey() {
        return delegate.lastKey();
    }
}