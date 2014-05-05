package org.bongiorno.ariadne.implementations;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.operandowners.OperandOwnerFactory;
import org.bongiorno.ariadne.operations.OperationFactory;
import org.bongiorno.ariadne.operators.OperatorFactory;
import org.bongiorno.ariadne.operators.arithmetic.Add;
import org.bongiorno.ariadne.operators.arithmetic.Divide;
import org.bongiorno.ariadne.operators.arithmetic.Min;
import org.bongiorno.ariadne.operators.arithmetic.Power;
import org.bongiorno.ariadne.operators.arithmetic.Subtract;
import org.bongiorno.ariadne.operators.logical.And;
import org.bongiorno.ariadne.operators.arithmetic.Multiply;
import org.bongiorno.ariadne.operators.logical.GreaterThan;
import org.bongiorno.ariadne.operators.logical.GreaterThanEqual;
import org.bongiorno.ariadne.operators.logical.LessThan;
import org.bongiorno.ariadne.operators.logical.LessThanEqual;
import org.bongiorno.ariadne.operators.logical.NotEqual;
import org.bongiorno.ariadne.operators.logical.Equal;
import org.bongiorno.ariadne.operators.set.Intersection;
import org.bongiorno.ariadne.operators.arithmetic.Max;
import org.bongiorno.ariadne.operators.logical.Or;
import org.bongiorno.ariadne.operators.set.SymmeticDifference;
import org.bongiorno.ariadne.operators.set.SetDifference;
import org.bongiorno.ariadne.operators.set.Union;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Apr 14, 2008
 * Time: 5:30:03 PM
 *
 * This class represents an empty set of knowledge. There are no OperandOwners defined and no Operations. THe only
 * thing you get by default are Operators
 */
public class EmptyKnowledgeBase extends KnowledgeBase {

    private OperatorFactory opFact = new EmptyOperatorFactory();
    private OperandOwnerFactory opOwnFact = new EmptyOperandOwnerFactory(this);
    private OperationFactory pFact = new EmptyOperationFactory(this);

    /**
     * Asbtract factory method required for interfacing
     * @return An Operation factory that is completely empty
     */
    protected OperationFactory getOperationFactory() {
        return pFact;
    }
    /**
     * Asbtract factory method required for interfacing
     * @return An OperandOwner factory that is completely empty
     */
    protected OperandOwnerFactory getOperandOwnerFactory() {
        return opOwnFact;
    }
    /**
     * Asbtract factory method required for interfacing
     * @return An Operator factory that contains all the default Operators as defined in
     * com.org.bongiorno.ariadne.operators.*
     */
    protected OperatorFactory getOperatorFactory() {
        return opFact;
    }


    public void store() throws AriadneException {
        throw new UnsupportedOperationException("Please override me");
    }

    private static class EmptyOperatorFactory extends OperatorFactory {

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
                addOperator(16, Intersection.class.getName());
                addOperator(17, SetDifference.class.getName());
                addOperator(18, Union.class.getName());
                addOperator(19, SymmeticDifference.class.getName());

            }
            catch (Exception e) {
                throw new AriadneException(e.toString(), e);
            }
        }

        public void store() throws AriadneException {

        }
    }

    private static class EmptyOperandOwnerFactory extends OperandOwnerFactory {

        public EmptyOperandOwnerFactory(KnowledgeBase engine) {
            super(engine);
        }

        public void load() throws AriadneException {

        }

        public void store() throws AriadneException {

        }
    }

    private static class EmptyOperationFactory extends OperationFactory {


        public EmptyOperationFactory(KnowledgeBase engine) {
            super(engine);
        }

        public void load() throws AriadneException {

        }

        public void store() throws AriadneException {

        }
    }
}
