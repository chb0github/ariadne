package org.bongiorno.ariadne.operators.logical;

import java.util.Set;


/**
 * @author chbo
 */
public class SubsetOf<T> extends LogicalOperator<Set<T>> {

    private static final Set<String> REGISTRATIONS = createRegistration("subset", "subsetof");

    /**
     * @inheritDoc
     */
    public SubsetOf(Integer id) {
        super(id);
    }

    /**
     * returns true if lho is a subset of rho
     *
     * @param lho cmon, do I need to explain this?
     * @param rho cmon, do I need to explain this?
     * @return true if lho is a subset of rho
     */
    public Boolean evaluate(Set<T> lho, Set<T> rho) {
        return rho.containsAll(lho);
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
        return "subsetof";
    }

    /**
     * @return false
     * @inheritDoc
     */
    public Boolean isCommutative() {
        return false;
    }

    /**
     * @return ['subset', 'subsetof']
     * @inheritDoc
     */
    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }

    public Boolean acceptsOperand(Class<Set<T>> clazz) {
        return Set.class.isAssignableFrom(clazz);
    }
}