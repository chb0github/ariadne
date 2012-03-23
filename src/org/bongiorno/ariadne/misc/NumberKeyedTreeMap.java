package org.bongiorno.ariadne.misc;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Jan 10, 2009
 * Time: 3:30:34 PM
 */
public class NumberKeyedTreeMap<V> extends TreeMap<Number, V> {

    private static final Comparator<Number> NUM_CMP = new Comparator<Number>() {
        public int compare(Number n1, Number n2) {
            return n1.intValue() - n2.intValue();
        }
    };

    public NumberKeyedTreeMap() {
        this(NUM_CMP);
    }

    public NumberKeyedTreeMap(Comparator<? super Number> comparator) {
        super(comparator);
    }

    public NumberKeyedTreeMap(Map<? extends Number, ? extends V> m) {
        super(m);
    }

    public NumberKeyedTreeMap(SortedMap<Number, ? extends V> m) {
        super(m);
    }
}
