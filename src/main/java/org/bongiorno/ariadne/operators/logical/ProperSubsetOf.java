package org.bongiorno.ariadne.operators.logical;

import java.util.Set;


/**
 * @author chbo
 *
 * a class representing the mathematical concept of Proper Subset
 */
public class ProperSubsetOf<T> extends LogicalOperator<Set<T>> {

    private static final Set<String> REGISTRATIONS = createRegistration("psubset", "psubsetof");

    /**
     * @inheritDoc
     */
    public ProperSubsetOf(Integer id) {
        super(id);
    }

    /**
     * returns true if lho is a subset of rho and they are not equal
     *
     * @param lho cmon, do I need to explain this?
     * @param rho cmon, do I need to explain this?
     * @return true if lho is a subset of rho
     */
    public Boolean evaluate(Set<T> lho, Set<T> rho) {
        return rho.containsAll(lho) && !lho.equals(rho);
    }

    /**
     * Shortcircuiting may only proceed when null is input
     *
     * @param lho the set to check
     * @return lho = null
     */
    public Boolean canShortCircuit(Set<T> lho) {
        return lho == null;
    }

    /**
     * @return 'subset'
     */
    public String toString() {
        return "psubsetof";
    }

    /**
     * @return false
     * @inheritDoc
     */
    public Boolean isCommutative() {
        return false;
    }

    /**
     * @return ['psubset', 'psubsetof']
     * @inheritDoc
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }

    /**
     * Only accepts sets for input
     *
     * @param clazz the class to check for acceptability
     * @return clazz.equals(Set.class);
     */
    public Boolean acceptsOperand(Class<Set<T>> clazz) {
        return Set.class.isAssignableFrom(clazz);
    }
}