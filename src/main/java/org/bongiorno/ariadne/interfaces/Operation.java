package org.bongiorno.ariadne.interfaces;

import org.bongiorno.ariadne.AriadneException;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 23, 2007
 * Time: 11:20:45 AM
 * a Root level interface to perform functions resulting in true/false as well as numeric results
 *
 * This interface is core to Ariadne and is, from a design perspective, no different than Predicates or Equations
 */
public interface Operation<RET_T, OO_T, RUN_T> {

    /**
     * a call to get an actualy value from this operation.
     * @param runTimeArg a user defined runtime argument that implementors of OperandOwner may use. For example,
     * if you have implemented an OperandOwner to get the weight of a "PurchaseItem" then an instance of "PurchaseItem"
     * would be the runtime argument
     * @return the result of having evaluated this Operation
     * @throws org.bongiorno.ariadne.AriadneException if there was some problem during the execution
     */
    public RET_T evaluate(RUN_T runTimeArg) throws AriadneException;

    /**
     * Gets the Operator that will be used to perform 'this' Operation
     * @return Operator that will be used to perform 'this' Operation
     */
    public Operator getOperator();

    /**
     * gets the Left hand OperandOwner of this Operation
     * @return the Left hand OperandOwner of this Operation
     */
    public OperandOwner<OO_T,RUN_T> getLho();
    /**
     * gets the right hand OperandOwner of this Operation
     * @return the Right hand OperandOwner of this Operation
     */
    public OperandOwner<OO_T,RUN_T> getRho();

    /**
     * This method is very important. Implementors of an Operation MUST assure certain things within the validate method
     * 1) lho.getOperandType() is compatible with rho.getOperandType()
     * 2) op.acceptsType(lho.getOperandType()) && op.acceptsType(rho.getOperandType())
     * 3) this.getRunTimeType() is compatible with lho.getRuntimeType() && rho.getRuntimeType()
     * 4) invokes: lho.validate() and rho.validate()
     * @throws AriadneException if any of those conditions fail.
     */
    public void validate() throws AriadneException;

    /**
     * gets the runtime type that will be supplied to the evaluate() method
     * @return the runtime type that will be supplied to the evaluate() method
     */
    public Class getRunTimeType();

    /**
     * gets the type that will be return from evaluating this Operation. It SHOULD be the same Operator#getEvaluationType()
     *
     * @return the type that will be returned from a call to evaluate
     */
    public Class getEvaluationType();


    /**
     * returns an id that may be used to obtain 'this' Operation from the KnowledgeBase it was originally constructed
     * in. More specifically, from the OperationFactory it was created in
     * @return an id uniquely representing this Operation that can be used to retrieve it again from the original
     * knowledge base it was obtained from
     */
    public Integer getId();
}
