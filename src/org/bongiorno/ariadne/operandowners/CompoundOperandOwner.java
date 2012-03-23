package org.bongiorno.ariadne.operandowners;

import java.util.HashSet;
import java.util.Set;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Operation;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Sep 5, 2007
 * Time: 5:16:40 PM
 * This class acts as an operand owner and by cross-referencing and delegating to a predicate
 * by implementing LogicalOperandOwner we indicate that this OperandOwner
 * is safe for Logical Operations
 */
public class CompoundOperandOwner implements OperandOwner {
    private Operation delegate = null;
    private Integer operationId = null;
    private KnowledgeBase engine = null;
    private Integer id = null;

    public CompoundOperandOwner(Integer id, KnowledgeBase engine, String s) throws AriadneException {
        this(id,engine,new Integer(s));
    }

    public CompoundOperandOwner(Integer id, KnowledgeBase engine, Integer delegateId) throws AriadneException {
        this.engine = engine;
        this.id = id;
        operationId = delegateId;
    }
    
    public CompoundOperandOwner(Operation p) throws AriadneException {
        delegate = p;
    }

    public Integer getId() {
        return id;
    }

    public Object getOperand(Object anyArg) throws AriadneException {
        validate();
        // we need lazy instantiation so we don't have a cyclic dependency

        return delegate.evaluate(anyArg);
    }


    public Object getHint() {
        return null;
    }

    public Class getOperandType() {
        // requires validation to have run first
        return delegate.getOperator().getEvaluationType();
    }

    /**
     * checks to make sure that all Operation references can indeed be referenced and also checks for cycles
     * that might have been introduced.
     * @throws AriadneException if a cycle is found or if an Operation cannot be resolved
     */
    public void validate() throws AriadneException {

        if (delegate == null) {
            delegate = engine.getOperation(operationId);
            if (delegate != null) {
                Set<Operation> visited = new HashSet<Operation>();
                checkForCycle(visited, delegate);
            }
            else {
                throw new AriadneException("Operation id " + operationId + " does not exist. " +
                                    "Likely it has been defined twice with different keys");
            }
        }

    }

    /**
     * Recursively looks for cycles using DPF
     * @param visited a set of all the previously visited Operations
     * @param operation the operation to look for cycles from
     * @throws AriadneException if a cycle was found
     */
    private void checkForCycle(Set<Operation> visited, Operation operation) throws AriadneException {
        assert operation != null;
        OperandOwner ooL = operation.getLho();
        OperandOwner ooR = operation.getRho();
        if (!visited.contains(operation)) {
            visited.add(operation);
            ooL.validate();
            ooR.validate();

            if (ooL instanceof CompoundOperandOwner)
                checkForCycle(visited, ((CompoundOperandOwner) ooL).getDelegate());
            if (ooR instanceof CompoundOperandOwner)
                checkForCycle(visited, ((CompoundOperandOwner) ooR).getDelegate());

            visited.remove(operation);
        }
        else {
            Number pid = operation.getId();
            Number lho = ooL.getId();
            Number rho = ooR.getId();
            Number op = operation.getOperator().getId();
            String msg = "Cycle in graph found for predicate (" + pid + ":" + lho + "," + op + "," + rho + ")";
            throw new AriadneException(msg);
        }
    }
    private static class StackPack {
        private Set<Operation> operations;
        private Operation operation;

        private StackPack(Set<Operation> operations, Operation operation) {
            this.operations = operations;
            this.operation = operation;
        }
    }

    /**
     * retrieves the Operation delegated to by this OperandOwner. It will be null until 1) validate() occurs or
     * getOperand is called
     * @return the Operation delegated to
     */
    public Operation getDelegate() {
        return delegate;
    }

    /**
     * Combined hashes of this class and the input hashcode
     * @return this.getClass().hashCode() + this.getInput().hashCode();
     */
    public int hashCode() {
        return this.getClass().hashCode() + this.getInput().hashCode();
    }

    /**
     * a simple '-' if validate has not yet been called.
     * @return the String representation of the delgate or '-' if validate has not been called
     */
    public String toString() {
        return (delegate == null ? "-" : delegate.toString());
    }

    /**
     * calls equals(OperandOwner)
     * @param o the other OperandOwner to compare to
     * @return see equals(OperandOwner)
     */
    public boolean equals(Object o) {
        return equals((OperandOwner) o);
    }

    /**
     * Equality check by class and input
     * @param o the other OperandOwner
     * @return true if class types and input types are equal
     */
    public Boolean equals(OperandOwner o) {
        return this == o || (o != null && (o.getClass() == getClass() && getInput().equals(o.getInput())));
    }

    /**
     * Returns the acceptable runtime type for this OperandOwner
     * @return returns the acceptable runtimeType for the Operation it delegates to.
     */
    public Class getRuntimeType() {
        return delegate.getRunTimeType();
    }

    /**
     * Get's the input type as interpreted by the this Object. Whatever type is returned it will be used to resolve
     *
     * @return
     */
    public Integer getInput() {
        return operationId;
    }

}
