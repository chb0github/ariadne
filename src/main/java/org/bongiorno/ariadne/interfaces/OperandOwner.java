package org.bongiorno.ariadne.interfaces;

import org.bongiorno.ariadne.AriadneException;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 22, 2007
 * Time: 5:29:49 PM
 *
 * Ah yes, the OperandOwner. In any Operation there are operands and an operator. 3 + 5 for example. However, to be of
 * any use, integration with existing systems is in order. This interface supplies a facility for determining
 *
 * Length(x) * width(x)
 *
 * In this example, Length and Width would be OperandOwners capable of extracting their appropriate value (AKA the operand)
 * from 'x'. So, while we can't perform L * W we can perform 10 * 20
 */
public interface OperandOwner<RET_T, RUN_T> {

    /**
     * This method is meant to do the runtime evaluation and utlimately return the Operand
     * to be evaluated in an Operation
     * @param anyArg a user defined runtime argument
     * @return anything you'd like. This is a function of what context it is being used in and us up
     * to the implementor
     * @throws org.bongiorno.ariadne.AriadneException subclasses may throw an exception
     */
    public RET_T getOperand(RUN_T anyArg) throws AriadneException;


    /**
     * This method exist to enforce equality check on all operandOwners. We can't just make the
     * object level method public boolean equals(Object o) an interface method because
     * then every class would have it implemented by default and would defeat the purpose. A strongly
     * recommended implementation is equals(Object o) return equals(((OperandOwner)o);
     * @param otherOO the other OperandOwner to compare to
     * @return true if: Consider OperandOwners as functions. Then, equality is f(x) = g(x)
     */
    public Boolean equals(OperandOwner<RET_T, RUN_T> otherOO);

    /**
     * a chance to ask the OperandOwner for it's initial input arguments
     * @return the input argument used to prime this OperandOwner
     */
    public Object getInput();

    /**
     * Gives OperandOwners an extra step beyond the contructor to validate
     * @throws AriadneException implementors may throw as they deem fit
     */
    public void validate() throws AriadneException;

    /**
     * For future use with shortcutting hueristics. Currently not used
     * @return a hint about the most likely result of calling getOperand()
     */
    public Object getHint();

    /**
     * returns the type expected from a call to getOperand(). This is the runtime equivalent to the generic
     * RET_T
     * @return the runtime equivalent to RET_T
     */
    public Class getOperandType();

    /**
     * The runtime type of the user defined argument used for execution. This must be equal to the generic type RUN_T
     * @return the runtime equivalent to RUN_T
     */
    public Class getRuntimeType();

    /**
     * returns an id that may be used to obtain 'this' OperandOwner from the KnowledgeBase it was originally constructed
     * in. More specifically, from the OperandOwnerFactory it was created in
     * @return an id uniquely representing this OperandOwner that can be used to retrieve it again from the original
     * knowledge base it was obtained from 
     */
    public Integer getId();
  
}
