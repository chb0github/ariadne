package org.bongiorno.ariadne.implementations;


import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.operandowners.CompoundOperandOwner;
import org.bongiorno.ariadne.operandowners.BooleanOperandOwner;
import org.bongiorno.ariadne.operandowners.OperandOwnerFactory;
import org.bongiorno.ariadne.operations.OperationFactory;
import org.bongiorno.ariadne.operators.OperatorFactory;
import org.bongiorno.ariadne.operators.arithmetic.Add;
import org.bongiorno.ariadne.operators.arithmetic.Divide;
import org.bongiorno.ariadne.operandowners.NumericOperandOwner;
import org.bongiorno.ariadne.operators.arithmetic.Min;
import org.bongiorno.ariadne.operators.arithmetic.Power;
import org.bongiorno.ariadne.operators.arithmetic.Subtract;
import org.bongiorno.ariadne.operators.logical.And;
import org.bongiorno.ariadne.operators.arithmetic.Multiply;
import org.bongiorno.ariadne.operators.logical.GreaterThan;
import org.bongiorno.ariadne.operators.logical.GreaterThanEqual;
import org.bongiorno.ariadne.operators.logical.Intersects;
import org.bongiorno.ariadne.operators.logical.LessThan;
import org.bongiorno.ariadne.operators.logical.LessThanEqual;
import org.bongiorno.ariadne.operators.logical.Matches;
import org.bongiorno.ariadne.operators.logical.NotEqual;
import org.bongiorno.ariadne.operators.logical.ProperSubsetOf;
import org.bongiorno.ariadne.operators.logical.SubsetOf;
import org.bongiorno.ariadne.operators.set.Intersection;
import org.bongiorno.ariadne.operators.set.SymmeticDifference;
import org.bongiorno.ariadne.operators.arithmetic.Max;
import org.bongiorno.ariadne.operators.logical.Equal;
import org.bongiorno.ariadne.operators.logical.Or;
import org.bongiorno.ariadne.operators.set.SetDifference;
import org.bongiorno.ariadne.operators.set.Union;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Apr 14, 2008
 * Time: 5:30:03 PM
 *
 * This KnowledgeBase is the one used by the unit tests. It is functional and contains all of the default operators.
 *
 */
public class ReferenceKnowledgeBase extends KnowledgeBase {

    private OperatorFactory opFact = new ReferenceKnowledgeBase.ReferenceOperatorFactory();
    private OperandOwnerFactory opOwnFact = new ReferenceOperandOwnerFactory(this);
    private OperationFactory pFact = new ReferenceOperationFactory(this);


    protected OperationFactory getOperationFactory() {
        return pFact;
    }

    protected OperandOwnerFactory getOperandOwnerFactory() {
        return opOwnFact;
    }

    protected OperatorFactory getOperatorFactory() {
        return opFact;
    }

    public void store() throws AriadneException {
        throw new UnsupportedOperationException("Please override me");
    }

    private static class ReferenceOperatorFactory extends OperatorFactory {

        public void load() throws AriadneException {
            try {
                addOperator(1, Equal.class.getName());
                addOperator(2, LessThan.class.getName());
                addOperator(3, LessThanEqual.class.getName());
                addOperator(4, NotEqual.class.getName());
                addOperator(5, GreaterThan.class.getName());
                addOperator(6, GreaterThanEqual.class.getName());

                addOperator(7, Or.class.getName());
                addOperator(8, And.class.getName());
                addOperator(9, Add.class.getName());
                addOperator(10, Subtract.class.getName());
                addOperator(11, Multiply.class.getName());
                addOperator(12, Divide.class.getName());
                addOperator(13, Min.class.getName());
                addOperator(14, Max.class.getName());
                addOperator(15, Power.class.getName());
                addOperator(16, Intersects.class.getName());
                addOperator(17, Intersection.class.getName());
                addOperator(18, SetDifference.class.getName());
                addOperator(19, Union.class.getName());
                addOperator(20, SymmeticDifference.class.getName());
                addOperator(21, ProperSubsetOf.class.getName());
                addOperator(22, SubsetOf.class.getName());
                addOperator(23, Matches.class.getName());

            }
            catch (Exception e) {
                throw new AriadneException(e.toString(), e);
            }
        }

        public void store() throws AriadneException {

        }
    }

    private static class ReferenceOperandOwnerFactory extends OperandOwnerFactory {

        public ReferenceOperandOwnerFactory(KnowledgeBase engine) {
            super(engine);
        }


        public void load() throws AriadneException {

            try {
                instantiateFailFast(1, BooleanOperandOwner.class, Boolean.TRUE);  //1
                instantiateFailFast(2, BooleanOperandOwner.class, Boolean.FALSE); //2

                instantiateFailFast(3, NumericOperandOwner.class, 100.10d);//3
                instantiateFailFast(4, NumericOperandOwner.class, 10.0d);    //4
                instantiateFailFast(5, NumericOperandOwner.class, 100.00d);//5
                instantiateFailFast(6, NumericOperandOwner.class, 1.0d);     //6

                instantiateFailFast(7, NumericOperandOwner.class, 123.0d);   //7

                instantiateFailFast(8, CompoundOperandOwner.class, 2);   //8
                instantiateFailFast(9, CompoundOperandOwner.class, 1);   //9
                instantiateFailFast(10, CompoundOperandOwner.class, 4);   //10
                instantiateFailFast(11, CompoundOperandOwner.class, 3);   //11

            }
            catch (Exception e) {
                throw new AriadneException(e.toString(), e);
            }
//            throw new KnowledgeBaseException("Must implement testing of compound equation owners");

        }

        public void store() throws AriadneException {
            // intentionally do nothing
            
        }
    }

    private static class ReferenceOperationFactory extends OperationFactory {


        public ReferenceOperationFactory(KnowledgeBase engine) {
            super(engine);
        }

        public void load() throws AriadneException {
        
            getPredicate(1, 1, 8, 2); // true || false?
            getPredicate(2, 3, 1, 4); // 100.10 >= 10
            getPredicate(3, 5, 4, 6); // 100.00 <= 1
            getPredicate(4, 8, 7, 9); // (100.100 >= 10) || (true || false)
            getPredicate(5, 10, 8, 11); // ((100.100 >= 10) || (true || false)) && (100 <= 1)
        }

        public void store() throws AriadneException {

        }
    }
}
