package org.bongiorno.ariadne.operators;

import org.bongiorno.ariadne.AbstractAriadneTest;
import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Operator;
import org.bongiorno.ariadne.operandowners.BooleanOperandOwner;
import org.bongiorno.ariadne.operandowners.NumericOperandOwner;

import static org.junit.Assert.*;


/**
 * @author chribong
 */
public class BasicOperatorTests extends AbstractAriadneTest {

    public void testUnFoundOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        try {
            engine.getEquation(NumericOperandOwner.class, 10.0d, "fail", 10.0d);
            fail();
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        try {
            engine.getPredicate(BooleanOperandOwner.class, Boolean.FALSE, "fail", Boolean.TRUE);
            fail();
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        try {
            OperandOwner test = engine.getOperandOwner(NumericOperandOwner.class, 10.0d);
            engine.getOperation(test, "fail", test);
            fail();
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }

    protected void basicArithmeticOpCheck(Operator<?, Double> operator) {
        assertNull(operator.evaluate(null, 1.0d));
        assertNull(operator.evaluate(1.0d, null));
        assertNull(operator.evaluate(null, 1.0d));
        assertNull(operator.evaluate(1.0d, null));
        assertNull(operator.evaluate(null, null));

        assertTrue(operator.canShortCircuit(null));
    }
}
