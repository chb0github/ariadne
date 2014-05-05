package org.bongiorno.ariadne.operators.arithmetic;

import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.operators.BasicOperatorTests;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author chribong
 */
public class ArithmeticOperatorTests extends BasicOperatorTests {

    @Test
    public void testPlusOperator() throws Exception {

        KnowledgeBase engine = getLoadedKnowledgeBase();

        ArithmeticOperator plus = engine.getArithmeticOperator("+");

        assertTrue(plus.evaluate(3.21, 10d) == 13.21d);
        assertTrue(plus.evaluate(10.0d, 3.21d) == 13.21d);
        assertTrue(plus.evaluate(-1.0d, 10.0d) == 9.0d);
        assertTrue(plus.evaluate(1.0d, -10.0d) == -9.0d);
        assertTrue(plus.evaluate(-1.0d, -10.0d) == -11.0d);

        basicArithmeticOpCheck(plus);

        assertFalse(plus.canShortCircuit(1.0d));


    }

    @Test
    public void testMinusOperator() throws Exception {

        KnowledgeBase engine = getLoadedKnowledgeBase();

        ArithmeticOperator minus = engine.getArithmeticOperator("-");

        assertTrue(minus.evaluate(3.21d, 10.0d) == -6.79); //3.21 - 10
        assertTrue(minus.evaluate(10.0d, 3.21d) == 6.79);
        assertTrue(minus.evaluate(10.0d, 1.0d) == 9.0d);
        assertTrue(minus.evaluate(-1.0d, 10.0d) == -11.0d);
        assertTrue(minus.evaluate(1.0d, -10d) == 11.0d);
        assertTrue(minus.evaluate(-1.0d, -10.0d) == 9.0d);

        basicArithmeticOpCheck(minus);
        assertFalse(minus.canShortCircuit(1.0d));

    }

    @Test
    public void testDivideOperator() throws Exception {

        KnowledgeBase engine = getLoadedKnowledgeBase();

        ArithmeticOperator divide = engine.getArithmeticOperator("/");


        assertTrue(divide.evaluate(3.21d, 10d) == 0.321d); //3.21 / 10
        assertTrue(divide.evaluate(10.0d, 3.21d) == 3.115264797507788); // 10 / 3.21 = 3.11...
        assertTrue(divide.evaluate(10.0d, 1.0d) == 10.0d);
        assertTrue(divide.evaluate(-1.0d, 10.0d) == -0.1d);
        assertTrue(divide.evaluate(1.0d, -10.0d) == -0.1d);
        assertTrue(divide.evaluate(-1.0d, -10.0d) == 0.1d);
        assertTrue(divide.evaluate(10.0d, 0.0d) == null);

        basicArithmeticOpCheck(divide);
        assertFalse(divide.canShortCircuit(1.0d));
        assertFalse(divide.canShortCircuit(0.0d));

    }


    @Test
    public void testMultiplyOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();

        ArithmeticOperator multiply = engine.getArithmeticOperator("*");


        assertTrue(multiply.evaluate(3.21d, 10d) == 32.1d);
        assertTrue(multiply.evaluate(-1.0d, 10.0d) == -10.0d);
        assertTrue(multiply.evaluate(1.0d, -10.0d) == -10.0d);
        assertTrue(multiply.evaluate(-1.0d, -10.0d) == 10.0d);
        assertTrue(multiply.evaluate(10.0d, 0.0d) == 0.0d);

        basicArithmeticOpCheck(multiply);

        assertTrue(multiply.canShortCircuit(0.0d));
        assertFalse(multiply.canShortCircuit(1.0d));
    }

    @Test
    public void testMinOperator() throws Exception {

        KnowledgeBase engine = getLoadedKnowledgeBase();

        ArithmeticOperator minus = engine.getArithmeticOperator("-");

        assertTrue(minus.evaluate(3.21d, 10d) == -6.79d);
        assertTrue(minus.evaluate(-1.0d, 10.0d) == -11.0d);
        assertTrue(minus.evaluate(1.0d, -10.0d) == 11.0d);
        assertTrue(minus.evaluate(-1.0d, -10.0d) == 9.0d);
        assertTrue(minus.evaluate(10.0d, 0.0d) == 10.0d);

        basicArithmeticOpCheck(minus);

        assertFalse(minus.canShortCircuit(1.0d));
    }

    @Test
    public void testMaxOperator() throws Exception {

        KnowledgeBase engine = getLoadedKnowledgeBase();

        ArithmeticOperator max = engine.getArithmeticOperator("max");

        assertTrue(max.evaluate(3.21d, 10.0d) == 10.0d);
        assertTrue(max.evaluate(-1.0d, 10.0d) == 10.0d);
        assertTrue(max.evaluate(1.0d, -10.0d) == 1.0d);
        assertTrue(max.evaluate(-1.0d, -10.0d) == -1.0d);
        assertTrue(max.evaluate(10.0d, 0.0d) == 10.0d);

        basicArithmeticOpCheck(max);
        assertFalse(max.canShortCircuit(1.0d));
    }

    @Test
    public void testPower() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();

        ArithmeticOperator power = engine.getArithmeticOperator("pow");

        assertTrue(power.evaluate(3.21d, 10.0d) == 116158.32049659154d);
        assertTrue(power.evaluate(-1.0d, 10.0d) == 1.0d);
        assertTrue(power.evaluate(1.0d, -10.0d) == 1.0d);
        assertTrue(power.evaluate(-1.0d, -10.0d) == 1.0d);
        assertTrue(power.evaluate(10.0d, 0.0d) == 1.0d);
        assertTrue(power.evaluate(0.0d, 1.0d) == 0.0d);
        // java implements this as 1
        assertTrue(power.evaluate(0.0d, 0.0d) == 1.0d);

        basicArithmeticOpCheck(power);
        assertTrue(power.canShortCircuit(1.0d));
        assertFalse(power.canShortCircuit(0.0d));
    }




}
