package org.bongiorno.ariadne.interfaces;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 22, 2007
 * Time: 5:29:42 PM
 * An interface defining two operands and a resultant value. None of the types have a restriction of them, that is for
 * Operator sub types to insist upon
 */
public interface Operator<RET_T, OPERAND_T> {

    /**
     * evaluates this operator between the left and right operands. Neither the operands or the results need be in any
     * particular form. THat is for sub types to insist upon
     * @param lho left hand operand
     * @param rho right hand operand
     * @return the result of this operator on the two operands.
     */
    public RET_T evaluate(OPERAND_T lho, OPERAND_T rho);

    /**
     * Some operators have the ability to short circuit their computation. Example: And
     * 0 * x = 0. So, if the LHo value falls into this range, then shortcircuiting is an effective and
     * simple optimization. Important: For this to work, and implementation must hold to: for any operation
     * c <-- (a op b) canShortCircuit = true iff 'a' is type compatable with 'c'. Given this, nulls are safe 
     * @param lho the left hand operand to evaluate for short circuiting
     * @return true iff c <-- (a op b) : 'a' is type compatable with 'c'. Given this, nulls are safe
     */
    public Boolean canShortCircuit(OPERAND_T lho);

    /**
     * If you don't understand this, you should put down the keyboard and go home
     * @return return true iff: pOPq = qOPp
     */
    public Boolean isCommutative();

    /**
     * Forces subclasses to implement equality. It is recommended that equals(Objec) == equals(Operator)
     * That definition is up to subclasses, but this.getClass().equals(oper.getClass()) is a suggestion
     * @param oper the other operator to compare against
     * @return true if they are 'equal'.
     */
    public boolean equals(Operator oper);

    /**
     * runtime type check. Ariadne is all dynamic so generics do us no good. Return true if the type passed in is
     * 'Compatible' -- this maybe be strict class equality, equality including classloader, or some combination with
     * subclass compatibility. Since this only a runtime check and only half the equation
     * the results of just one call are indeterminate. Calling acceptsOperand(lho) && acceptsOperand(rho), however
     * must be conclusive
     * @param clazz the class to check for acceptability
     * @return true only if that type is 'compatible' for this Operator
     */
    public Boolean acceptsOperand(Class<OPERAND_T> clazz);

    /**
     * Gets the type that will result from a call to operator evaluate(). Subtype specificity is not an obligation
     * @return the class type returned from an evaluate call.
     */
    public Class getEvaluationType();

    /**
     * Gets the ID of this operator that can be used to retrieve it again from the same KnowledgeBase
     * @return the unique id of this Operator
     */
    public Integer getId();


    /**
     * this method allows for an Operator implementation to express which operator strings it would like to be
     * associated with. For example addition might be associated with ["+", "add", "addition"]. If any of the returned
     * registrations already exists, an AriadneException will be throws during the registration process
     *
     * @return a set of strings to register this operator with.
     */
    public Set<String> getRegistrations();
}