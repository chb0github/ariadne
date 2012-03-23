package org.bongiorno.ariadne.operators.logical;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chbo
 *         Date: Jan 14, 2010
 *         Time: 3:59:45 PM
 *
 * This class defines the basic properties of an operator that using ordering and returns a boolean result.
 * essentially meant to reduce C&P for this operator type
 */
public abstract class LogicalComparableOperator<T extends Comparable<T>> extends LogicalOperator<T> {
    protected LogicalComparableOperator(Integer id) {
        super(id);
    }

    /**
     * @inheritDoc
     * by definition, if it's comparable, it most certainly isn't logical
     * @return lho == null
     */
    public Boolean canShortCircuit(T lho) {
        return lho == null;
    }

    /**
     * NOPE! By definition, something that is orderable is NEVER commutative
     * @return false
     */
    public Boolean isCommutative() {
        return false;
    }

    /**
     * Only comparables are acceptable. The genereric is merely a formality as Ariadne is very much dynamic.
     * Besides, if you knew the compile time type of your operator you would also obviously know what operands are
     * acceptable. This is meant only as a runtime validation
     * @param clazz  the class to compare against
     * @return Comparable.class.isAssignableFrom(clazz)
     */
    public Boolean acceptsOperand(Class<T> clazz) {
        // this is not an assurance of safety, because it's only half the equation
        return Comparable.class.isAssignableFrom(clazz);
    }
}
