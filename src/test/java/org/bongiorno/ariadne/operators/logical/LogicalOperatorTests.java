package org.bongiorno.ariadne.operators.logical;

import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.operators.BasicOperatorTests;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author chribong
 */
public class LogicalOperatorTests extends BasicOperatorTests {

    /**
     * must implement
     * <pre>
     *          T       | NULL | F
     * ------------------------------
     * T    |   T       | NULL | F
     * NULL |   NULL    | NULL | F
     * F    |   F       | F    | F
     * </pre>
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void testAndOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();


        LogicalOperator<Boolean> and = engine.getLogicalOperator("&&");
        assertTrue(and.evaluate(Boolean.TRUE, Boolean.TRUE));
        assertFalse(and.evaluate(Boolean.FALSE, Boolean.TRUE));
        assertFalse(and.evaluate(Boolean.TRUE, Boolean.FALSE));
        assertFalse(and.evaluate(Boolean.FALSE, Boolean.FALSE));

        assertNull(and.evaluate(null, Boolean.TRUE));
        assertNull(and.evaluate(Boolean.TRUE, null));

        assertFalse(and.evaluate(null, Boolean.FALSE));
        assertFalse(and.evaluate(Boolean.FALSE, null));

        assertNull(and.evaluate(null, null));

        assertTrue(and.canShortCircuit(Boolean.FALSE));
        assertFalse(and.canShortCircuit(null));
        assertFalse(and.canShortCircuit(Boolean.TRUE));
    }

    @Test
    public void testMatchesOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();


        LogicalOperator<String> match = engine.getLogicalOperator("matches");
        assertTrue(match.evaluate("Christian", "Chris.*"));
        assertFalse(match.evaluate("Christian", "Bob.*"));


        assertNull(match.evaluate(null, "Chri.*"));
        assertNull(match.evaluate("Christian", null));

        assertFalse(match.canShortCircuit("Christian"));
        assertTrue(match.canShortCircuit(null));
    }

    /**
     * Must implement
     * <pre>
     *          T       | NULL | F
     * ------------------------------
     * T    |   T       | T     | T
     * NULL |   T       | N     | N
     * F    |   T       | N     | F
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void testOrOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();


        LogicalOperator<Boolean> or = engine.getLogicalOperator("||");
        assertTrue(or.evaluate(Boolean.TRUE, Boolean.TRUE));
        assertTrue(or.evaluate(Boolean.FALSE, Boolean.TRUE));
        assertTrue(or.evaluate(Boolean.TRUE, Boolean.FALSE));
        assertFalse(or.evaluate(Boolean.FALSE, Boolean.FALSE));
        assertTrue(or.evaluate(null, Boolean.TRUE));
        assertTrue(or.evaluate(Boolean.TRUE, null));

        assertNull(or.evaluate(null, Boolean.FALSE));
        assertNull(or.evaluate(Boolean.FALSE, null));
        assertNull(or.evaluate(null, null));

        assertFalse(or.canShortCircuit(Boolean.FALSE));
        assertFalse(or.canShortCircuit(null));
        assertTrue(or.canShortCircuit(Boolean.TRUE));
    }

    /**
     * Must implement
     * <pre>
     *          A       | NULL | B
     * ------------------------------
     * A    |   T       | F     | F
     * NULL |   F       | T     | F
     * B    |   F       | F     | T
     * </pre>
     *
     * @throws Exception just because underlying code might
     */
    @Test
    public void testEqualOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();

        String a = new String("a");
        String b = new String("a");
        LogicalOperator<Object> eq = engine.getLogicalOperator("==");
        assertTrue(eq.evaluate(a, b)); // specifically check for internalized objects
        assertTrue(eq.evaluate(Boolean.TRUE, Boolean.TRUE));
        assertFalse(eq.evaluate(Boolean.FALSE, Boolean.TRUE));
        assertFalse(eq.evaluate(Boolean.TRUE, Boolean.FALSE));
        assertTrue(eq.evaluate(Boolean.FALSE, Boolean.FALSE));

        assertFalse(eq.evaluate(null, Boolean.TRUE));
        assertFalse(eq.evaluate(Boolean.TRUE, null));
        assertFalse(eq.evaluate(null, Boolean.FALSE));
        assertFalse(eq.evaluate(Boolean.FALSE, null));
        assertTrue(eq.evaluate(null, null));

        assertTrue(eq.canShortCircuit(null));
        assertFalse(eq.canShortCircuit(new Object()));
    }

    /**
     * <pre>
     *          A       | NULL | B
     * ------------------------------
     * A    |   F       | T     | T
     * NULL |   T       | F     | T
     * B    |   T       | T     | F
     * </pre>
     *
     * @throws Exception just because underlying code might
     */
    @Test
    public void testNotEqualOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();

        String a = new String("a");
        String b = new String("a");
        LogicalOperator<Object> neq = engine.getLogicalOperator("!=");
        assertFalse(neq.evaluate(a, b));
        assertFalse(neq.evaluate(Boolean.TRUE, Boolean.TRUE));

        assertTrue(neq.evaluate(Boolean.FALSE, Boolean.TRUE));
        assertTrue(neq.evaluate(Boolean.TRUE, Boolean.FALSE));

        assertTrue(neq.evaluate(null, Boolean.TRUE));
        assertTrue(neq.evaluate(Boolean.TRUE, null));
        assertTrue(neq.evaluate(null, Boolean.FALSE));
        assertTrue(neq.evaluate(Boolean.FALSE, null));
        assertFalse(neq.evaluate(null, null));

        assertTrue(neq.canShortCircuit(null));
        assertFalse(neq.canShortCircuit(new Object()));
    }

    /**
     * <pre>
     *          N       | 1     | 2
     * ------------------------------
     * N    |   N       | N     | N
     * 1    |   N       | F     | F
     * 2    |   N       | T     | F
     * </pre>
     *
     * @throws Exception just because underlying code might
     */
    @Test
    public void testGTOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();

        LogicalOperator<Double> gt = engine.getLogicalOperator(">");
        assertFalse(gt.evaluate(0.0d, 1.0d));
        assertTrue(gt.evaluate(1.0d, 0.0d));
        assertFalse(gt.evaluate(1.0d, 1.0d));

        basicArithmeticOpCheck(gt);
    }

    /**
     * <pre>
     *          N       | 1     | 2
     * ------------------------------
     * N    |   N       | N     | N
     * 1    |   N       | F     | T
     * 2    |   N       | F     | F
     * </pre>
     *
     * @throws Exception just because underlying code might
     */
    @Test
    public void testLTOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();


        LogicalOperator<Double> lt = engine.getLogicalOperator("<");
        assertTrue(lt.evaluate(0.0d, 1.0d));
        assertFalse(lt.evaluate(1.0d, 0.0d));
        assertFalse(lt.evaluate(1.0d, 1.0d));

        basicArithmeticOpCheck(lt);
    }

    /**
     * <pre>
     *          N       | 1     | 2
     * ------------------------------
     * N    |   N       | N     | N
     * 1    |   N       | T     | F
     * 2    |   N       | T     | T
     * </pre>
     *
     * @throws Exception just because underlying code might
     */
    @Test
    public void testGTEOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();


        LogicalOperator<Double> gte = engine.getLogicalOperator(">=");
        assertFalse(gte.evaluate(0.0d, 1.0d));
        assertTrue(gte.evaluate(1.0d, 0.0d));
        assertTrue(gte.evaluate(1.0d, 1.0d));

        basicArithmeticOpCheck(gte);
    }

    /**
     * <pre>
     *          N       | 1     | 2
     * ------------------------------
     * N    |   N       | N     | N
     * 1    |   N       | T     | T
     * 2    |   N       | F     | T
     * </pre>
     *
     * @throws Exception just because underlying code might
     */
    @Test
    public void testLTEOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();

        LogicalOperator<Double> lte = engine.getLogicalOperator("<=");
        assertTrue(lte.evaluate(0.0d, 1.0d));
        assertFalse(lte.evaluate(1.0d, 0.0d));
        assertTrue(lte.evaluate(1.0d, 1.0d));

        basicArithmeticOpCheck(lte);
    }
}
