package org.bongiorno.ariadne.operations;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Operation;
import org.bongiorno.ariadne.interfaces.Operator;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 28, 2007
 * Time: 3:30:06 PM
 * A basic Operation without typing. It encapsulates the ese
 */
public class UnTypedOperation<RET_T, OPERAND_T, RUN_T> implements Operation<RET_T, OPERAND_T, RUN_T> {
    private OperandOwner<OPERAND_T, RUN_T> lho = null;
    private Operator<RET_T, OPERAND_T> op = null;
    private OperandOwner<OPERAND_T, RUN_T> rho = null;
    private Integer id = null;

    public UnTypedOperation(Integer id, OperandOwner<OPERAND_T, RUN_T> lho, Operator<RET_T, OPERAND_T> op,
                            OperandOwner<OPERAND_T, RUN_T> rho) {

        this.id = id;
        this.lho = lho;
        this.op = op;
        this.rho = rho;
    }

    private void validateTypes(OperandOwner lho, Operator op, OperandOwner rho) throws AriadneException {
        Class lhoType = lho.getOperandType();
        Class rhoType = rho.getOperandType();
        // we assume subtypes to be compatible -- this may not be appropriate. Only time will tell
        if (!areCompatible(lhoType,rhoType))
            throw new AriadneException("Operand Types are not compatible: lho: " + lhoType + " rho: " + rhoType);

        // if the operator doesn't accept either type, it's an error
        if (!op.acceptsOperand(lhoType) || !op.acceptsOperand(rhoType))
            throw new AriadneException("Operator " + op.getClass() + " does not accept type " + lhoType);

        lhoType = lho.getRuntimeType();
        rhoType = rho.getRuntimeType();
        // we assume subtypes to be compatible -- this may not be appropriate. Only time will tell
        if (!areCompatible(lhoType,rhoType))
            throw new AriadneException("Runtime Types are not compatible: lho: " + lhoType + " rho: " + rhoType);

        Class thisType = this.getRunTimeType();
        if (!areCompatible(lhoType,thisType) || !areCompatible(rhoType,thisType))
            throw new AriadneException("Runtime Types are not compatible with this operation: lho: " + lhoType + " rho: " + rhoType);
    }

    /**
     * Checks for either way assignability a <--> b
     * @param a an arbitrary class to check against b
     * @param b an arbitrary class to check against a
     * @return a.isAssignableFrom(b) || b.isAssignableFrom(a)
     */
    private Boolean areCompatible(Class a, Class b) {
        return (a.isAssignableFrom(b) || b.isAssignableFrom(a));
    }

    public RET_T getOperand(RUN_T runTimeArg) throws Exception {
        return evaluate(runTimeArg);
    }

    /**
     *
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     */
    public void validate() throws AriadneException {
        lho.validate();
        rho.validate();
        validateTypes(lho, op, rho);
    }

    /**
     *  #equals(UnTypedOperation)
     */
    public boolean equals(Object obj) {
        return equals((UnTypedOperation) obj);
    }

    public Boolean equals(UnTypedOperation<RET_T, OPERAND_T, RUN_T> oo) {

        return (this == oo) || lho.equals(oo.getLho()) && rho.equals(oo.getRho()) && op.equals(oo.getOperator());
    }

    /**
     * required to verify uniqueness
     * @return lho.hashCode() + op.hashCode() + rho.hashCode()
     */
    public int hashCode() {
        return lho.hashCode() + op.hashCode() + rho.hashCode();
    }



    public RET_T evaluate(RUN_T runTimeArg) throws AriadneException {
        OPERAND_T l = lho.getOperand(runTimeArg);
        RET_T result = null;
        if (!op.canShortCircuit(l)) {
            OPERAND_T r = rho.getOperand(runTimeArg);
            result = op.evaluate(l, r);
        } else {
            // the only way this can throw a CCE is if op.canShortCircuit
            // is not implemented properly AND the return type is not the same as the
            // operand types.
            try {
                result = (RET_T) l;
            }
            catch (ClassCastException e) {
                throw new ClassCastException("short circuting for operator " + op.getClass() + "failed. ");
            }
        }
        return result;
    }

    public String toString() {
        return "(" + lho + " " + op + " " + rho + ")";
    }

    public OperandOwner<OPERAND_T, RUN_T> getLho() {
        return lho;
    }

    public Operator<RET_T, OPERAND_T> getOperator() {
        return op;
    }


    public OperandOwner<OPERAND_T, RUN_T> getRho() {
        return rho;
    }


    public Class getEvaluationType() {
        // if they are both equal, an arbitrary choice is fine.
        return lho.getOperandType();
    }

    public Class getRunTimeType() {
        // if they are both equal, an arbitrary choice is fine.
        return lho.getRuntimeType();
    }
}
