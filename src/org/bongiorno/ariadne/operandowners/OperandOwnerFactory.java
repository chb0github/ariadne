package org.bongiorno.ariadne.operandowners;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Storable;
import org.bongiorno.ariadne.misc.ExceptionOnDuplicateKeyMap;
import org.bongiorno.ariadne.misc.ExceptionOnDuplicateKeySortedMap;
import org.bongiorno.ariadne.misc.NumberKeyedTreeMap;
import org.bongiorno.ariadne.misc.ObservableFactory;
import org.bongiorno.ariadne.operations.OperationFactory;
import org.bongiorno.ariadne.interfaces.Operation;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Sep 5, 2007
 * Time: 11:10:48 AM
 * .
 */
public abstract class OperandOwnerFactory extends ObservableFactory<Number, OperandOwner> implements Storable {

    private final SortedMap<Number, OperandOwner> OP_OWNERS =
            new ExceptionOnDuplicateKeySortedMap<Number, OperandOwner>(new NumberKeyedTreeMap<OperandOwner>());


    private final Map<OpClassKey, OperandOwner> OP_BY_CLASS_VAL =
            new ExceptionOnDuplicateKeyMap<OpClassKey, OperandOwner>(new HashMap<OpClassKey, OperandOwner>(10000));

    private final Map<OpClassKey, Integer> ID_BY_CLASS_VAL =
            new ExceptionOnDuplicateKeyMap<OpClassKey, Integer>(new HashMap<OpClassKey, Integer>(10000));

    private final Map<Class<OperandOwner>, Set<OperandOwner>> CLASSES =
            new HashMap<Class<OperandOwner>, Set<OperandOwner>>(10000);

    protected KnowledgeBase engine = null;

    protected OperationFactory operFact = null;


    protected OperandOwnerFactory(KnowledgeBase engine) {
        this.engine = engine;
    }

    protected OperandOwner instantiate(Integer key, String fqcn, Object val) throws AriadneException {
        Class<?> c = null;
        try {
            c = Class.forName(fqcn);
        }
        catch (ClassNotFoundException e) {
            throw new AriadneException(e);
        }
        return instantiateFailFast(key, c, val);
    }

    /**
     * Man, I wish I remembered why I seperated the instantiation process. I know I needed to distinguish between
     * Database instantiation and regular usage instantiation. I just don't remember why
     *
     * @param id  the id/key of the OperandOwner to instantiate
     * @param c   the class of the operand owner to instantiate
     * @param val the constructor time value to be passed to the OperandOwner
     * @return an instance of OperandOwner. DO NOT assume this to be c.newInstance()
     * @throws Exception if there was a problem instantiating. Usually because the constructor didn't have the proper
     *                   signature
     */
    protected OperandOwner instantiateFailFast(Integer id, Class c, Object val) throws AriadneException {
        OperandOwner oo = instantiate(id, c, val);
        return addToMaps(id, oo, oo.getInput());
    }

    private OperandOwner instantiateFailSafe(Integer id, Class c, Object val) throws Exception {
        // creat a new instance
        OperandOwner oo = instantiate(id, c, val);
        // now lookup if it exists
        OpClassKey testKey = new OpClassKey(c, oo.getInput());
        // do a look up of the key to see if one already exists.
        OperandOwner testResult = OP_BY_CLASS_VAL.get(testKey);
        // if it doesn't, add it to our known set of operand owners, if it does, return the prior instance
        return (testResult == null ? addToMaps(oo) : testResult);
    }

    private OperandOwner addToMaps(OperandOwner oo) throws Exception {
        return addToMaps(oo.getId(), oo, oo.getInput());
    }

    /**
     * This method is meant to allow subclasses to circumvent the normal registration process for loading OperandOwners,
     * specifically with the intent of allowing the load method of the subclass to insist that a certain ID be assigned
     * to a certain OperandOwner. If your subclass is persisting data, then you will almost certainly be making use
     * of this method.
     *
     * @param id  the id/key to be associated with an instance of c
     * @param c the class to instantiate
     * @param val the value to pass to that class (AKA getInput()). May be null.
     * @return
     * @throws org.bongiorno.ariadne.AriadneException
     *
     */
    protected OperandOwner instantiate(Integer id, Class<OperandOwner> c, Object val) throws AriadneException {
        Constructor cnst = null;
        Object[] args = null;
        // it is possible that there is no value to pass to the KB. The simplest approach is to just
        // look for a 2 arg constructor. if this method fails, the whole call must fail
        if (val == null) {
            try {
                cnst = c.getDeclaredConstructor(Integer.class, KnowledgeBase.class);
                args = new Object[] {id,engine};
            }

            catch (NoSuchMethodException e) {
                throw new AriadneException(e);
            }

        }
        else {
            try {
                // try to find a constructor that specifically matches our value parameter
                cnst = c.getDeclaredConstructor(Integer.class, KnowledgeBase.class, val.getClass());
            }
            catch (NoSuchMethodException e) {
                // but if that fails, fallback to the string version
                try {
                    cnst = c.getDeclaredConstructor(Integer.class, KnowledgeBase.class, String.class);
                }
                catch (NoSuchMethodException e1) {
                    throw new AriadneException(e1);
                }
                // and convert the object to it's toString() form.
                val = val.toString();
            }
            args = new Object[] {id,engine,val};

        }
        OperandOwner oo = null;
        try {
            oo = (OperandOwner) cnst.newInstance(args);
        }
        catch (Exception e) {
            throw new AriadneException(e);
        }
        return oo;
    }

    /**
     * By the time it has hit this function, and finished, the OperandOwner has been officially added
     *
     * @param id  the ID or key of the OperandOwner being added
     * @param oo  the OperandOwner being added
     * @param val the value passed to the constructor for 'oo'
     * @return the OperandOwner just added to the map, or 'oo'
     * @throws Exception only if an attempt at a duplicate insert occurred
     */

    private OperandOwner addToMaps(Integer id, OperandOwner oo, Object val) throws AriadneException {
        Class c = oo.getClass();
        OP_OWNERS.put(id, oo);
        // combine these to form a key the
        // these maps must be identical in size. If they are not it indicates that a duplicate entry was
        // created which is not permissable                             
        OpClassKey key = new OpClassKey(c, val);

        OP_BY_CLASS_VAL.put(key, oo);
        ID_BY_CLASS_VAL.put(key, id);
        addClass(c, oo);
        notifyObservers(oo);
        return oo;
    }

    /**
     * This method is for bookkeeping. Associate all isntance of a given OO, with it's class 'c'
     * c -> {oo(c),oo1(c),oo2(c)...}
     *
     * @param c  the OperandOwner class
     * @param oo and instance of 'c' to be appending to a set of existing instances
     */
    private void addClass(Class<OperandOwner> c, OperandOwner oo) {
        Set<OperandOwner> previous = CLASSES.get(c);
        if (previous == null)
            previous = new HashSet<OperandOwner>();
        previous.add(oo);
        CLASSES.put(c, previous);
    }

    public OperandOwner getOperandOwner(String fqcn, Object val) throws AriadneException {
        Class<?> c = null;
        try {
            c = Class.forName(fqcn);
        }
        catch (ClassNotFoundException e) {
            throw new AriadneException(e);
        }
        return getOperandOwner(c, val);
    }

    public Integer getId(Class<? extends OperandOwner> operandOwner, Object val) {
        return ID_BY_CLASS_VAL.get(new OpClassKey(operandOwner, val));
    }

    public Set<OperandOwner> getOperandOwners() {
        return Collections.unmodifiableSet(new LinkedHashSet<OperandOwner>(OP_OWNERS.values()));
    }

    /**
     * returns a set of all operand owners that have been instantiated, by a given class type. So, if you want all
     * NumericOperandOwners, then you would call  getOperandOwners(NumericOperandOwner.class);
     *
     * @param ownerType the operand owner type of the instances you are looking for
     * @return a set of all instances of class 'ownerType'
     */
    public Set<OperandOwner> getOperandOwners(Class<? extends OperandOwner> ownerType) {
        Set<OperandOwner> retVal = CLASSES.get(ownerType);

        return retVal == null ? new HashSet<OperandOwner>() : Collections.unmodifiableSet(retVal);
    }

    /**
     * Accepts any number but will always convert to Number.intValue()
     * As far as I am concerned, Numbers in the java Object tree are wrongly designed
     *
     * @param id the id of this OO
     * @return an OperandOwner associated with the key. Null if it doesn't exist
     */
    public OperandOwner getOperandOwner(Number id) {
        return OP_OWNERS.get(id.intValue());
    }

    /**
     * returns an operand owner when queried by it's id/key as found in OperandOwner.getId()
     *
     * @param key the lookup key for the operand owner in question
     * @return if it exists, the OperandOwner for that id. If it doesn't, null
     * @throws Exception no exceptions are thrown, but since it's a method on factory, other 'Factories' might
     */
    public OperandOwner get(Number key) throws AriadneException {
        return getOperandOwner(key);
    }

    /**
     * Instantiates and registers 'c' as an OperandOwner. The registration process is part of what help Ariadne ensure
     * memory == equality between same types.
     *
     * @param c             the class to be registered and used with ariadne. NOTE: Do NOT assume an instance of this class
     *                      will be returned from this function!
     * @param operandOwnArg the argument that will be passed into the OperandOwner constructor
     * @return and instance of OperandOwner. Do NOT assume an instance of this class
     *         will be returned from this function!
     * @throws AriadneException if there was a problem instantiating or registering your operandOwner
     */
    public OperandOwner getOperandOwner(Class c, Object operandOwnArg) throws AriadneException {
        OperandOwner oo = null;
        try {
            oo = instantiateFailSafe(getId(), c, operandOwnArg);
        }
        catch (Exception e) {
            throw new AriadneException(e.toString(), e);
        }

        return oo;
    }

    /**
     * simply generates a new ID for and OperandOwner by taking the largest ID and adding 1. It would be ncie
     * to fill in pockets of IDs. That would be a great way to eliminate cruft when OperandOwners are deleted outside
     * of Ariadne. This shouldn't be a problem either as it would be the insertion of a new OperandOwner with a new id
     * and could never collide with previous cases.
     *
     * @return an id for a new OperandOwner
     */
    private Integer getId() {
        Integer key = 0;
        if (OP_OWNERS.size() > 0)
            key = OP_OWNERS.lastKey().intValue() + 1;
        return key;
    }

    public OperandOwner getOperandOwner(Operation p) throws AriadneException {
        return getOperandOwner(CompoundOperandOwner.class, p.getId());
    }

    public OperandOwner getOperandOwner(String s) throws AriadneException {
        return getOperandOwner(StringOperandOwner.class, s);
    }

    public OperandOwner getOperandOwner(Date date) throws AriadneException {
        return getOperandOwner(DateOperandOwner.class, date);
    }

    public OperandOwner getOperandOwner(Boolean b) throws AriadneException {
        return getOperandOwner(Boolean.class, b);
    }

    public OperandOwner getOperandOwner(Double bd) throws AriadneException {
        return getOperandOwner(NumericOperandOwner.class, bd);
    }

    private static class OpClassKey implements Comparable<OpClassKey> {
        private Class<? extends OperandOwner> clazz = null;
        private Object value = null;

        public OpClassKey(Class<? extends OperandOwner> clazz, Object value) {
            this.clazz = clazz;
            this.value = value;
        }

        /**
         * Eventhough this class implements compareTo(), equals is not based on it because that method is not a good
         * sort. It's meant for display ordering in my debugger
         *
         * @param o the other Key to check for equality
         * @return true if the class and values are equal
         */
        public boolean equals(Object o) {
            boolean eq = (this == o);


            if (!eq && o instanceof OpClassKey) {
                OpClassKey that = (OpClassKey) o;
                eq = this.clazz.equals(that.clazz);
                eq = eq && (this.value == that.value || (this.value != null && this.value.equals(that.value)));
            }

            return eq;
        }

        public int hashCode() {
            return clazz.hashCode() ^ (value == null ? 0 : value.hashCode());
        }

        public Class<? extends OperandOwner> getClazz() {
            return clazz;
        }

        public Object getValue() {
            return value;
        }


        public String toString() {
            return clazz + "(" + value + ")";
        }

        /**
         * for display organization. Not a really good ordering
         *
         * @param o
         * @return
         */
        public int compareTo(OpClassKey o) {
            int retVal = clazz.getName().compareTo(o.clazz.getName());
            if (retVal == 0 && value != null)
                retVal = value.toString().compareTo(o.value.toString());
            return retVal;
        }
    }

}
