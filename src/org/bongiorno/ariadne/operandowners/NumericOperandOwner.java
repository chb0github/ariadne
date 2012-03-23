package org.bongiorno.ariadne.operandowners;


import org.bongiorno.ariadne.KnowledgeBase;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 22, 2007
 * Time: 5:32:50 PM
 *
 * An OperandOwner that always returns a Double. RUN_T is the runtime type for evaluation, but it will be ignored.
 * This object simply wraps an immutable constant and always returns it. This is so you can compare an object that
 * requires runtime operand expression (the purpose of OperandOwner) with a constant of some number like "10.23"
 *
 * Implementing ArithmeticOperand owner is partially redundant but it does indicate that
 * this OperandOwner is same for Arithmetic Operand. I am not sure how useful this will be.
 */
public class NumericOperandOwner<RUN_T> extends ConstantOperandOwner<Double,RUN_T> implements ArithmeticOperandOwner<RUN_T>{


    /**
     * Creates this object and delegates it's return type to num
     * @param num the value that will always be returned by {@see #getOperandOwner(Object)}
     * @param id the id number for this operand owner
     *
     */
    public NumericOperandOwner(Integer id, Double num) {
        super(id,num);
    }

    /**
     * Converts a String to it's equivalent Double form
     * @param engine not used.
     * @param number a String version of Double that will be parsed.
     * @param id the id number for this operand owner
     */
    public NumericOperandOwner(Integer id, KnowledgeBase engine,String number) {
        this(id,new Double(number));
    }

    public NumericOperandOwner(Integer id, KnowledgeBase engine,Double number) {
        this(id,number);
    }


    public Class getOperandType() {
        return Double.class;
    }
}
