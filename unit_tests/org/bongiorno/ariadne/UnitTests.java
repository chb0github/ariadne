package org.bongiorno.ariadne;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import junit.framework.TestCase;

import org.bongiorno.ariadne.actions.PrioritizedSeriesAction;
import org.bongiorno.ariadne.actions.SeriesAction;
import org.bongiorno.ariadne.actions.SimplePrioritizableAction;
import org.bongiorno.ariadne.implementations.EmptyKnowledgeBase;
import org.bongiorno.ariadne.implementations.ReferenceKnowledgeBase;
import org.bongiorno.ariadne.interfaces.Action;
import org.bongiorno.ariadne.interfaces.Equation;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Operation;
import org.bongiorno.ariadne.interfaces.Operator;
import org.bongiorno.ariadne.interfaces.Predicate;
import org.bongiorno.ariadne.operandowners.BooleanOperandOwner;
import org.bongiorno.ariadne.operandowners.CompoundOperandOwner;
import org.bongiorno.ariadne.operandowners.DateOperandOwner;
import org.bongiorno.ariadne.operandowners.MinimalOperandOwner;
import org.bongiorno.ariadne.operandowners.OperandOwnerFactory;
import org.bongiorno.ariadne.operandowners.SetOperandOwner;
import org.bongiorno.ariadne.operandowners.NumericOperandOwner;
import org.bongiorno.ariadne.operations.OperationFactory;
import org.bongiorno.ariadne.operators.OperatorFactory;
import org.bongiorno.ariadne.operators.arithmetic.Add;
import org.bongiorno.ariadne.operators.arithmetic.ArithmeticOperator;
import org.bongiorno.ariadne.operators.logical.LogicalOperator;
import org.bongiorno.ariadne.operandowners.StringOperandOwner;


/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 23, 2007
 * Time: 11:22:19 AM
 * One big unit test to confirm the runtime combination between Arithmetic expressions and logical Expressions
 */
public class UnitTests extends TestCase {


    public void testKnowledgeBaseEquality() throws Exception {
        KnowledgeBase alpha = getLoadedKnowledgeBase();
        KnowledgeBase beta = getLoadedKnowledgeBase();

        assertEquals(alpha, alpha); // shallow check

        assertTrue(alpha != beta);// we want a deep equality check
        assertEquals(alpha, beta);

    }

//    public void testSum() throws AriadneException {
//        for(int i = 0; i <10; i++)
//            evalTest(6000);
//
//
//    }
//    private void evalTest(int count) throws AriadneException {
//        KnowledgeBase kb = KnowledgeBase.getInstance(); // default, in memory, empty but for operators
//        kb.load();
//        kb.validate();
//        Random rand = new Random(1);
//        Equation last = kb.getEquation(0d, "+", 0d);
//        long memstart = currentMem();
//        System.out.println("Evaluating " + count);
//
//        for (double i = 1; i <= count; i++) {
//            last = kb.getEquation(last, "+", (double) rand.nextInt(count));
//        }
//        kb.validate();
//        long now = System.nanoTime();
//        Double sum = (Double) last.evaluate(null);
//        long end = System.nanoTime();
//        System.out.println("Time to evaluate using ariadne: " + ((end - now) / 1000d));
//        sum = 0.0d;
//        now = System.nanoTime();
//        for (double i = 1; i <= count; i++) {
//            sum += (double) rand.nextInt(count);
//        }
//        end = System.nanoTime();
//        System.out.println("Time to evaluate natively: " + ((end - now) / 1000d));
//    }
//    private long currentMem() {
//        Runtime runtime = Runtime.getRuntime();
//        long memStart = runtime.totalMemory() - runtime.freeMemory();
//        return ((memStart / 1024) / 1024);
//    }

    public void testCheckForSimpleCycleDetection() throws Exception {
        // this System property insert here is also a test for the dynamic insertion of
        // knowledge bases into the KB factory
        System.getProperties().put("engines.simple_cycle",
                "com.org.bongiorno.ariadne.UnitTests$SimpleCycleKnowledgeBase");

        KnowledgeBase cycle = getLoadedKnowledgeBase("simple_cycle");
        try {
            cycle.validate();
            fail();
        }
        catch (AriadneException e) {
            // we expect it to throw an exception because of an intentional cycle introduction
        }

    }


    public void testCheckForComplexCycleDetection() throws Exception {
        // this System property insert here is also a test for the dynamic insertion of
        // knowledge bases into the KB factory
        System.getProperties().put("engines.complex_cycle",
                "com.org.bongiorno.ariadne.UnitTests$ComplexCycleKnowledgeBase");

        KnowledgeBase cycle = getLoadedKnowledgeBase("complex_cycle");
        try {
            cycle.validate();
            fail();
        }
        catch (AriadneException e) {
            // we expect it to throw an exception because of an intentional cycle introduction
        }

    }

    public void testDupOperatorRegistrationFail() throws Exception {
        // this System property insert here is also a test for the dynamic insertion of
        // knowledge bases into the KB factory
        System.getProperties().put("engines.fail_operator",
                "com.org.bongiorno.ariadne.UnitTests$FailOperatorKnowledgeBase");

        KnowledgeBase kb = KnowledgeBase.getInstance("fail_operator");
        try {
            kb.load();
            fail();
        }
        catch (IllegalArgumentException e) {
            // should fail
        }
    }

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

    public void testIncompatibleReturnTypeCompatibility() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase("empty");
        OperandOwner l = engine.getOperandOwner(NumericOperandOwner.class, 10d);
        OperandOwner r = engine.getOperandOwner(NumericOperandOwner.class, 100d);
        Equation e = engine.getEquation(l, "+", r);
        OperandOwner rr = engine.getOperandOwner(StringOperandOwner.class, "Christian");
        Equation ef = engine.getEquation(e, "==", rr);
        try {
            engine.validate();
            fail();
        }
        catch (AriadneException e1) {
            // should fail
        }
    }

    public void testApplyRules() throws Exception {
        KnowledgeBase kb = KnowledgeBase.getInstance();

        kb.load();

        OperandOwner xoo = kb.getOperandOwner(XOperandOwner.class);
        OperandOwner yoo = kb.getOperandOwner(YOperandOwner.class);


        Equation e = kb.getEquation(xoo, "+", yoo);
        Equation f = kb.getEquation(yoo, "/", xoo);

        Equation<Double, TestRuntimeObject> ef = kb.getEquation(e, "/", f);

        OperandOwner nine = kb.getOperandOwner(NumericOperandOwner.class,9.0d);
        Predicate<Double,TestRuntimeObject> p = kb.getPredicate(ef, ">",nine);
        Action<TestRuntimeObject> a = new Action<TestRuntimeObject>() {
            public void perform(TestRuntimeObject context) throws AriadneException {
            }
        };
        Action<TestRuntimeObject> b = new Action<TestRuntimeObject>() {
            public void perform(TestRuntimeObject context) throws AriadneException {
            }
        };
        p.addAction(a);
        p.addAction(b);



        Set<Predicate<?, TestRuntimeObject>> results = kb.apply(new TestRuntimeObject(10d, 20d));
        assertEquals(new HashSet<Predicate<?,TestRuntimeObject>>(Arrays.asList(p)),results);
        results = kb.apply(new TestRuntimeObject(1d, 1d));
        assertTrue(results.size() == 0);

        // have a default set of rule application methods is still under consideration
//        kb.setRuleMethod(APPLY_ALL);
//        results = kb.apply(new TestRuntimeObject(10d, 20d));
//        assertEquals(results,new HashSet<Predicate<?,TestRuntimeObject>>(Arrays.asList(p)));
//        results = kb.apply(new TestRuntimeObject(1d, 1d));
//        assertTrue(results.size() == 0);


    }

    public void testActionSeries() throws Exception {
        KnowledgeBase kb = KnowledgeBase.getInstance();

        kb.load();

        OperandOwner xoo = kb.getOperandOwner(XOperandOwner.class);
        OperandOwner yoo = kb.getOperandOwner(YOperandOwner.class);


        Equation e = kb.getEquation(xoo, "+", yoo);
        Equation f = kb.getEquation(yoo, "/", xoo);

        Equation<Double, TestRuntimeObject> ef = kb.getEquation(e, "/", f);

        OperandOwner nine = kb.getOperandOwner(NumericOperandOwner.class,9.0d);
        Predicate<Double,TestRuntimeObject> p = kb.getPredicate(ef, ">",nine);
        final List<Action<TestRuntimeObject>> actionsRun = new LinkedList<Action<TestRuntimeObject>>();

        Action<TestRuntimeObject> a = new Action<TestRuntimeObject>() {
            public void perform(TestRuntimeObject context) throws AriadneException {
                actionsRun.add(this);
            }
        };
        Action<TestRuntimeObject> b = new Action<TestRuntimeObject>() {
            public void perform(TestRuntimeObject context) throws AriadneException {
                actionsRun.add(this);
            }
        };

        p.addAction(new SeriesAction<Action<TestRuntimeObject>,TestRuntimeObject>(a,b));
        Set<Predicate<?, TestRuntimeObject>> results = kb.apply(new TestRuntimeObject(10d, 20d));

        assertEquals(p.getActions().size(),1);
        assertTrue(actionsRun.containsAll(Arrays.asList(a,b)));

    }

    public void testPrioritizableSeries() throws Exception {
        KnowledgeBase kb = KnowledgeBase.getInstance();

        kb.load();

        OperandOwner xoo = kb.getOperandOwner(XOperandOwner.class);
        OperandOwner yoo = kb.getOperandOwner(YOperandOwner.class);


        Equation e = kb.getEquation(xoo, "+", yoo);
        Equation f = kb.getEquation(yoo, "/", xoo);

        Equation<Double, TestRuntimeObject> ef = kb.getEquation(e, "/", f);

        OperandOwner nine = kb.getOperandOwner(NumericOperandOwner.class,9.0d);
        Predicate<Double,TestRuntimeObject> p = kb.getPredicate(ef, ">",nine);
        final List<Action<TestRuntimeObject>> actionsRun = new LinkedList<Action<TestRuntimeObject>>();

        Action<TestRuntimeObject> a = new Action<TestRuntimeObject>() {
            public void perform(TestRuntimeObject context) throws AriadneException {
                actionsRun.add(this);
            }
        };
        Action<TestRuntimeObject> b = new Action<TestRuntimeObject>() {
            public void perform(TestRuntimeObject context) throws AriadneException {
                actionsRun.add(this);
            }
        };
        Action<TestRuntimeObject> c = new Action<TestRuntimeObject>() {
            public void perform(TestRuntimeObject context) throws AriadneException {
                actionsRun.add(this);
            }
        };
        PrioritizedSeriesAction<SimplePrioritizableAction<TestRuntimeObject>,TestRuntimeObject> actions =
                new PrioritizedSeriesAction<SimplePrioritizableAction<TestRuntimeObject>,TestRuntimeObject>();

        actions.addAction(new SimplePrioritizableAction<TestRuntimeObject>(3,a));
        actions.addAction(new SimplePrioritizableAction<TestRuntimeObject>(2,b));
        actions.addAction(new SimplePrioritizableAction<TestRuntimeObject>(1,c));
        SimplePrioritizableAction<TestRuntimeObject> series = new SimplePrioritizableAction<TestRuntimeObject>(4,
                new SeriesAction<Action<TestRuntimeObject>, TestRuntimeObject>(a, b));
        actions.addAction(series);

        p.addAction(actions);

        Set<Predicate<?, TestRuntimeObject>> results = kb.apply(new TestRuntimeObject(10d, 20d));

        assertEquals(p.getActions().size(),1);
        assertTrue(actionsRun.equals(Arrays.asList(c,b,a,a,b)));

        actions.removeAction(series);

        actionsRun.clear();
        results = kb.apply(new TestRuntimeObject(10d, 20d));

        assertEquals(p.getActions().size(),1);
        assertTrue(actionsRun.equals(Arrays.asList(c,b,a)));

        series = new SimplePrioritizableAction<TestRuntimeObject>(0,
                new SeriesAction<Action<TestRuntimeObject>, TestRuntimeObject>(a, b));
        actions.addAction(series);
        actionsRun.clear();
        results = kb.apply(new TestRuntimeObject(10d, 20d));
        assertEquals(p.getActions().size(),1);
        assertTrue(actionsRun.equals(Arrays.asList(a,b,c,b,a)));

    }
    public void testSideEffectCheck() throws Exception {
        KnowledgeBase kb = KnowledgeBase.getInstance();

        kb.load();

        OperandOwner xoo = kb.getOperandOwner(XOperandOwner.class);
        OperandOwner yoo = kb.getOperandOwner(YOperandOwner.class);


        Equation e = kb.getEquation(xoo, "+", yoo);
        Equation f = kb.getEquation(yoo, "/", xoo);

        Equation<Double, TestRuntimeObject> ef = kb.getEquation(e, "/", f);

        OperandOwner nine = kb.getOperandOwner(NumericOperandOwner.class,9.0d);
        Predicate<Double,TestRuntimeObject> p = kb.getPredicate(ef, ">",nine);
        Action<TestRuntimeObject> a = new Action<TestRuntimeObject>() {
            public void perform(TestRuntimeObject context) throws AriadneException {
                context.setX(context.getX() * -1);
            }
        };
        Action<TestRuntimeObject> b = new Action<TestRuntimeObject>() {
            public void perform(TestRuntimeObject context) throws AriadneException {
                System.out.println("context is: " +context);
            }
        };
        p.addAction(a);
        p.addAction(b);


        kb.setSideEffectChecking(true);
        try {
            kb.apply(new TestRuntimeObject(10d, 20d));
            fail(); // above will cause side effects and should fail
        }
        catch (AriadneException e1) {
            // expected
        }



    }
    public void testSameTypeRuntimeAssignability() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase("empty");
        OperandOwner l = engine.getOperandOwner(NumericOperandOwner.class, 10d);
        OperandOwner r = engine.getOperandOwner(NumericOperandOwner.class, 1d);
        Equation e = engine.getEquation(l, "+", r);
        engine.validate();
    }

    public void testCrossTypeRuntimeAssignability() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase("empty");
        OperandOwner l = engine.getOperandOwner(NumericOperandOwner.class, 10d);
        OperandOwner xoo = engine.getOperandOwner(XOperandOwner.class);
        Equation e = engine.getEquation(l, "+", xoo);
        engine.validate();
    }

    public void testCreateNoDuplicateOperators() throws Exception {

        KnowledgeBase engine = getLoadedKnowledgeBase();

        Set<Operator> operators = engine.getOperators();
        assertTrue(operators.size() > 0);

        for (Operator op : operators) {
            Set<String> opCodes = op.getRegistrations();
            for (String opCode : opCodes) {
                Operator other = engine.getOperator(opCode);
                assertTrue(op == other);
            }
        }
    }


    public void testCreateNoDuplicateOperations() throws Exception {
        KnowledgeBase kb = getLoadedKnowledgeBase();

        Set<Operation> operations = kb.getOperations();
        assertTrue(operations.size() > 0);

        for (Operation operation : operations) {
            Operation other = kb.getOperation(operation.getId());
            assertTrue(operation == other);
        }
    }

    public void testCreateNoDuplicateOperandOwners() throws Exception {
        KnowledgeBase kb = getLoadedKnowledgeBase();
        Set<OperandOwner> owners = kb.getOperandOwners();
        assertTrue(owners.size() > 0);

        for (OperandOwner oo : owners) {
            OperandOwner other = kb.getOperandOwner(oo.getClass(), oo.getInput());
            assertTrue(oo == other);
        }
    }

    public void testCreateNoDuplicatePredicatesSimple() throws Exception {
        KnowledgeBase kb = getLoadedKnowledgeBase();

        Predicate p = kb.getPredicate(NumericOperandOwner.class, 100.00d, "==", 10.0d);
        Predicate q = kb.getPredicate(NumericOperandOwner.class, 100.00d, "==", 10.0d);
        assertTrue(p == q);// no duplicates allowed. A different instance is not allowed
        kb.validate();
    }

    public void testCreateNoDuplicatePredicatesComplex() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();


        Predicate a = engine.getPredicate(NumericOperandOwner.class, 123.12d, ">", 100.0d);   // true
        Predicate b = engine.getPredicate(NumericOperandOwner.class, 400.0d, "<=", 100.0d); //false

        Predicate aANDb = engine.getPredicate(a, "&&", b);            // false
        Predicate c = engine.getPredicate(NumericOperandOwner.class, 24.0d, ">=", 87.0d); // false
        Predicate d = engine.getPredicate(NumericOperandOwner.class, 1.0d, "<", 3.14159d); //true

        Predicate cORd = engine.getPredicate(c, "||", d); // true

        Predicate<Boolean, Object> p = engine.getPredicate(aANDb, "&&", cORd); // false
        Predicate q = engine.getPredicate(aANDb, "&&", cORd);
        assertTrue(p == q);
        engine.validate();

        // this also serves as an evaluation test too
        assertFalse(p.evaluate(null));
    }

    public void testNullValueOperandOwner() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();

        OperandOwner noo = engine.getOperandOwner(NullInputValueOperandOwner.class);
        OperandOwner ten = engine.getOperandOwner(NumericOperandOwner.class, 10.0d);

        Equation<Double, Object> test = engine.getEquation(noo, "+", ten);
        Double d = test.evaluate(null);
        assertEquals(d, 20d);

    }

    public void testCreateComplexEquation() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();


        Equation a = engine.getEquation(123.12d, "+", 100.0d);   // 223.12
        Equation b = engine.getEquation(400.0d, "/", 100.0d); //4

        Equation aMINb = engine.getEquation(a, "min", b); // 4
        Equation c = engine.getEquation(2.110d, "*", 3.0d); // 6.33
        Equation d = engine.getEquation(1.0d, "-", 3.14159d); //-2.14159

        Equation cMAXd = engine.getEquation(c, "max", d); // 6.33

        Equation<Double, ?> p = engine.getEquation(aMINb, "pow", cMAXd); // 4 ^ 6 = 6472.018426784786d
        Equation q = engine.getEquation(aMINb, "pow", cMAXd);
        assertTrue(p == q);
        engine.validate();

        // this also serves as an evaluation test too
        assertTrue(p.evaluate(null) == 6472.018426784786d);
    }

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
    public void testLTEOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();

        LogicalOperator<Double> lte = engine.getLogicalOperator("<=");
        assertTrue(lte.evaluate(0.0d, 1.0d));
        assertFalse(lte.evaluate(1.0d, 0.0d));
        assertTrue(lte.evaluate(1.0d, 1.0d));

        basicArithmeticOpCheck(lte);
    }

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

    public void testAllKnowledgeBaseFunctions() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();

        // OperandOwner a = engine.getOperandOwner(String,Object);
        // OperandOwner b = engine.getOperandOwner(Number);
        // OperandOwner c = engine.getOperandOwner(Class,Object);
        // OperandOwner d = engine.getOperandOwner(Operation);
        // OperandOwner e = engine.getOperandOwner(String);
        // OperandOwner f = engine.getOperandOwner(java.util.Date);
        // OperandOwner g = engine.getOperandOwner(Boolean);
        // OperandOwner efg = engine.getOperandOwner(Double);
        // Predicate hi = engine.getPredicate(OperandOwner,String,Equation);
        // Predicate jk = engine.getPredicate(Equation,String,OperandOwner);
        Equation e = engine.getEquation(123.12d, "+", 100.0d);   // 223.12
        Equation f = engine.getEquation(400.0d, "/", 100.0d); //4
        Predicate l = engine.getPredicate(e, ">", f);
        Equation a = engine.getEquation("3", "+", "3");

        // Predicate m = engine.getPredicate(Boolean,String,Boolean);
        // Predicate n = engine.getPredicate(Integer)
        // Predicate o = engine.getPredicate(Class,Object,String,Object);
        // Predicate p = engine.getPredicate(OperandOwner,String,OperandOwner);
        // Predicate q = engine.getPredicate(OperandOwner,LogicalOperator,OperandOwner);
        // Predicate r = engine.getPredicate(Predicate,String,Predicate);
        //Equation getPredicate(OperandOwner lho, ArithmeticOperator op, OperandOwner rho);
        //Equation getEquation(OperandOwner lho, String op, OperandOwner rho);
        //Equation getEquation(OperandOwner lho, String op, Equation rho);
        //Equation getEquation(Equation lho, String op, OperandOwner rho);
        // Equation s = engine.getEquation(Number);
        // Equation t = engine.getEquation(Class,Object,String,Object);
        // Equation u = engine.getEquation(Object,String,Object);
        // Equation v = engine.getEquation(Equation,String,Equation);
        // Equation w = engine.getEquation(Equation,ArithmeticOperator,Equation);
        // LogicalOperator y = engine.getLogicalOperator(String);
        // ArithmeticOperator x = engine.getArithmeticOperator(String);

        //todo invoke every public function on KnowledgeBase


        Predicate<Boolean, Object> g = engine.getPredicate(e, ">", f);
        assertTrue(g.evaluate(null));

    }

    public void testObservers() throws Exception {
        KnowledgeBase engine = KnowledgeBase.getInstance();
        CountingObserver obs = new CountingObserver();
        engine.addObserver(obs);
        // only load after observing
        engine.load();

        Predicate a = engine.getPredicate(NumericOperandOwner.class, 123.12d, ">", 100.0d);   // true
        Predicate b = engine.getPredicate(NumericOperandOwner.class, 400.0d, "<=", 100.0d); //false

        Predicate aANDb = engine.getPredicate(a, "&&", b);            // false
        Predicate c = engine.getPredicate(NumericOperandOwner.class, 24.0d, ">=", 87.0d); // false
        Predicate d = engine.getPredicate(NumericOperandOwner.class, 1.0d, "<", 3.14159d); //true

        Predicate cORd = engine.getPredicate(c, "||", d); // true


        Equation e = engine.getEquation(123.12d, "+", 100.0d);   // 223.12
        Equation f = engine.getEquation(400.0d, "/", 100.0d); //4

        Equation eMINf = engine.getEquation(e, "min", f); // 4
        Equation g = engine.getEquation(2.110d, "*", 3.0d); // 6.33
        Equation h = engine.getEquation(1.0d, "-", 3.14159d); //-2.14159

        Equation gMAXh = engine.getEquation(g, "max", h); // 6.33

        Equation<Double, Double> equation = engine.getEquation(eMINf, "pow", gMAXh); // 4 ^ 6 = 6472.018426784786d


        Predicate<Boolean, Object> p = engine.getPredicate(aANDb, "&&", cORd); // false
        OperandOwner oo = engine.getOperandOwner(NumericOperandOwner.class, 10000.0d);
        Predicate<Boolean, Object> q = engine.getPredicate(oo, ">", equation);
        Predicate<?, Object> last = engine.getPredicate(p, "||", q);

        assertEquals(obs.getOperationCount(), 16);
        assertEquals(obs.getOperatorCount(), 19);
        assertEquals(obs.getOperandOwnerCount(), 26);

    }

    public void testCombinedArithmeticWithLogical() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();


        Predicate a = engine.getPredicate(NumericOperandOwner.class, 123.12d, ">", 100.0d);   // true
        Predicate b = engine.getPredicate(NumericOperandOwner.class, 400.0d, "<=", 100.0d); //false

        Predicate aANDb = engine.getPredicate(a, "&&", b);            // false
        Predicate c = engine.getPredicate(NumericOperandOwner.class, 24.0d, ">=", 87.0d); // false
        Predicate d = engine.getPredicate(NumericOperandOwner.class, 1.0d, "<", 3.14159d); //true

        Predicate cORd = engine.getPredicate(c, "||", d); // true


        Equation e = engine.getEquation(123.12d, "+", 100.0d);   // 223.12
        Equation f = engine.getEquation(400.0d, "/", 100.0d); //4

        Equation eMINf = engine.getEquation(e, "min", f); // 4
        Equation g = engine.getEquation(2.110d, "*", 3.0d); // 6.33
        Equation h = engine.getEquation(1.0d, "-", 3.14159d); //-2.14159

        Equation gMAXh = engine.getEquation(g, "max", h); // 6.33

        Equation<Double, Double> equation = engine.getEquation(eMINf, "pow", gMAXh); // 4 ^ 6 = 6472.018426784786d


        Predicate<Boolean, Object> p = engine.getPredicate(aANDb, "&&", cORd); // false
        OperandOwner oo = engine.getOperandOwner(NumericOperandOwner.class, 10000.0d);
        Predicate<Boolean, Object> q = engine.getPredicate(oo, ">", equation);
        Predicate<?, Object> last = engine.getPredicate(p, "||", q);

        engine.validate();
        // this also serves as an evaluation test too
        Boolean complicated = last.evaluate(null);
        assertTrue(complicated);

    }

    public void testSetOperandOwner() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner oo = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Joe,Bob,John");
        OperandOwner o1 = engine.getOperandOwner(SetOperandOwner.class, "Double|10.1,20.0,1.211,.0034");
        OperandOwner o2 = engine.getOperandOwner(SetOperandOwner.class, "Date|1981-12-02T23:00:00Z,1976-09-10T09:00:00Z");
        OperandOwner o3 = engine.getOperandOwner(SetOperandOwner.class, "Boolean|true,false,TRUE,FALSE");

        Operation op = engine.getOperation(oo, "==", oo);
        assertEquals(op.evaluate(null), Boolean.TRUE);

        op = engine.getOperation(o1, "==", oo);
        assertEquals(op.evaluate(null), Boolean.FALSE);

        op = engine.getOperation(o2, "==", oo);
        assertEquals(op.evaluate(null), Boolean.FALSE);

        op = engine.getOperation(o3, "==", oo);
        assertEquals(op.evaluate(null), Boolean.FALSE);
    }

    public void testDateOperandOwner() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner vBday = engine.getOperandOwner(DateOperandOwner.class, "1981-12-02T23:00:00Z");
        OperandOwner myBday = engine.getOperandOwner(DateOperandOwner.class, "1976-09-10T09:00:00Z");
        Predicate p = engine.getPredicate(myBday, "<", vBday);
        assertEquals(p.evaluate(null), Boolean.TRUE);
    }

    public void testStringOperandOwner() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner a = engine.getOperandOwner(StringOperandOwner.class, "TEST");
        OperandOwner b = engine.getOperandOwner(StringOperandOwner.class, "BLAH");
        Predicate p = engine.getPredicate(a, "==", b);
        assertEquals(p.evaluate(null), Boolean.FALSE);
    }

    public void testNumericOperandOwner() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner a = engine.getOperandOwner(NumericOperandOwner.class, "10.1");
        OperandOwner b = engine.getOperandOwner(NumericOperandOwner.class, 10.1);
        Predicate p = engine.getPredicate(a, "==", b);
        assertEquals(p.evaluate(null), Boolean.TRUE);
    }

    public void testBooleanOperandOwner() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner a = engine.getOperandOwner(BooleanOperandOwner.class, "true");
        OperandOwner b = engine.getOperandOwner(BooleanOperandOwner.class, Boolean.FALSE);
        Predicate p = engine.getPredicate(a, "==", b);
        assertEquals(p.evaluate(null), Boolean.FALSE);
    }

    public void testSetIntersectionOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner a = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Nimish,Todor");
        OperandOwner b = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Kevin,Charles");

        Operation<Set<String>, ?, Object> intersect = engine.getOperation(a, "intersection", b);


        Set<String> result = intersect.evaluate(null);
        assertTrue(result.size() == 1);
        assertTrue(result.contains("Christian"));

        Operator op = engine.getOperator("intersection");
        assertTrue(op.canShortCircuit(new HashSet<String>()));
        assertTrue(op.canShortCircuit(null));
        assertFalse(op.canShortCircuit(new HashSet<String>(Arrays.asList("Christian"))));
    }


    public void testSetIntersectsOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner a = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Nimish,Todor");
        OperandOwner b = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Kevin,Charles");

        Operation<Boolean, ?, Object> intersect = engine.getOperation(a, "intersects", b);


        assertTrue(intersect.evaluate(null));

        Operator op = engine.getOperator("intersects");
        assertFalse(op.canShortCircuit(new HashSet<String>()));
        assertTrue(op.canShortCircuit(null));
        assertFalse(op.canShortCircuit(new HashSet<String>(Arrays.asList("Christian"))));
    }

    public void testSubSetOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner a = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Nimish,Todor");
        OperandOwner b = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Kevin,Charles");
        OperandOwner c = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Nimish");

        Operation<Boolean, ?, Object> subset = engine.getOperation(a, "psubset", b);
        assertFalse(subset.evaluate(null));

        subset = engine.getOperation(a, "psubset", a);
        assertFalse(subset.evaluate(null));

        subset = engine.getOperation(c, "psubset", a);
        assertTrue(subset.evaluate(null));


        Operation<Boolean, ?, Object> intersect = engine.getOperation(a, "psubset", b);
        assertFalse(intersect.evaluate(null));

        Operator op = engine.getOperator("psubset");
        assertFalse(op.canShortCircuit(new HashSet<String>()));
        assertTrue(op.canShortCircuit(null));
        assertFalse(op.canShortCircuit(new HashSet<String>(Arrays.asList("Christian"))));
    }

    public void testProperSubSetOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner a = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Nimish,Todor");
        OperandOwner b = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Kevin,Charles");
        OperandOwner c = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Nimish");

        Operation<Boolean, ?, Object> subset = engine.getOperation(a, "subsetof", b);
        assertFalse(subset.evaluate(null));

        subset = engine.getOperation(a, "subsetof", a);
        assertTrue(subset.evaluate(null));

        subset = engine.getOperation(c, "subsetof", a);
        assertTrue(subset.evaluate(null));

        Operator op = engine.getOperator("subsetof");
        assertFalse(op.canShortCircuit(new HashSet<String>()));
        assertTrue(op.canShortCircuit(null));
        assertFalse(op.canShortCircuit(new HashSet<String>(Arrays.asList("Christian"))));
    }

    public void testSetUnionOperator() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner a = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Nimish,Todor");
        OperandOwner b = engine.getOperandOwner(SetOperandOwner.class, "String|Christian,Kevin,Charles");

        Operation<Set<String>, ?, Object> union = engine.getOperation(a, "union", b);

        Set<String> result = union.evaluate(null);
        assertTrue(result.size() == 5);
        assertTrue(result.containsAll(Arrays.asList("Christian", "Nimish", "Todor", "Kevin", "Charles")));

        Operator op = engine.getOperator("union");
        assertFalse(op.canShortCircuit(new HashSet<String>()));
        assertTrue(op.canShortCircuit(null));
        assertFalse(op.canShortCircuit(new HashSet<String>(Arrays.asList("Christian"))));
    }

    public void testSetRelativeComplimentOperator() throws Exception {
        Set<String> a = new HashSet<String>(Arrays.asList("Christian", "Nimish", "Todor"));
        Set<String> b = new HashSet<String>(Arrays.asList("Christian", "Kevin", "Charles"));
        KnowledgeBase engine = getLoadedKnowledgeBase();
        Operator<Set<String>, Set<String>> union = engine.getOperator("setdiff");

        Set<String> result = union.evaluate(a, b);
        assertTrue(result.size() == 2);
        assertTrue(result.containsAll(Arrays.asList("Nimish", "Todor")));

        assertFalse(union.canShortCircuit(new HashSet<String>()));
        assertTrue(union.canShortCircuit(null));
        assertFalse(union.canShortCircuit(new HashSet<String>(Arrays.asList("Christian"))));
    }

    public void testSetSymetricDifferenceOperator() throws Exception {
        Set<String> a = new HashSet<String>(Arrays.asList("Christian", "Nimish", "Todor"));
        Set<String> b = new HashSet<String>(Arrays.asList("Christian", "Kevin", "Charles"));
        KnowledgeBase engine = getLoadedKnowledgeBase();

//        if(this != null)
//            throw new Exception("reimplement these tests");

        Operator<Set<String>, Set<String>> union = engine.getOperator("symdiff");

        Set<String> result = union.evaluate(a, b);
        assertTrue(result.size() == 4);
        assertTrue(result.containsAll(Arrays.asList("Nimish", "Todor", "Kevin", "Charles")));

        assertFalse(union.canShortCircuit(new HashSet<String>()));
        assertTrue(union.canShortCircuit(null));
        assertFalse(union.canShortCircuit(new HashSet<String>(Arrays.asList("Christian"))));
    }

    public void testComplexSetOperation() {
        Set<String> a = new HashSet<String>(Arrays.asList("Christian", "Nimish", "Todor"));
        Set<String> b = new HashSet<String>(Arrays.asList("Christian", "Kevin", "Charles"));

    }

    public void testExample() throws Exception {
        KnowledgeBase kb = KnowledgeBase.getInstance();

        kb.load();

        OperandOwner xoo = kb.getOperandOwner(XOperandOwner.class);
        OperandOwner yoo = kb.getOperandOwner(YOperandOwner.class);


        Equation e = kb.getEquation(xoo, "+", yoo);   // 10 + 20
        Equation f = kb.getEquation(yoo, "/", xoo); // 20 /10

        Equation<Double, TestRuntimeObject> ef = kb.getEquation(e, "/", f);
        Equation equation12 = kb.getEquation(1.0d, "+", 2.0d);
        Equation equation12a = kb.getEquation(1.0d, "+", 2.0d);

        OperandOwner two = kb.getOperandOwner(2.0d);
        Equation equation122 = kb.getEquation(equation12, "+", two);
        Equation equation12122 = kb.getEquation(equation12a, "+", equation122);

        Equation<Double, Double> equation112122 = kb.getEquation(equation12, "+", equation12122);
        kb.validate();

        Double d = ef.evaluate(new TestRuntimeObject(10d, 20d));

        d = ef.evaluate(new TestRuntimeObject(125d, 234d));

        d = equation112122.evaluate(null);

    }


    private KnowledgeBase getLoadedKnowledgeBase() throws Exception {
        return getLoadedKnowledgeBase("reference");
    }

    private void basicArithmeticOpCheck(Operator<?, Double> operator) {
        assertNull(operator.evaluate(null, 1.0d));
        assertNull(operator.evaluate(1.0d, null));
        assertNull(operator.evaluate(null, 1.0d));
        assertNull(operator.evaluate(1.0d, null));
        assertNull(operator.evaluate(null, null));

        assertTrue(operator.canShortCircuit(null));
    }

    private KnowledgeBase getLoadedKnowledgeBase(String knowledgeStore) throws Exception {
        KnowledgeBase kb = KnowledgeBase.getInstance(knowledgeStore);
        kb.load();
        return kb;
    }


    public static class SimpleCycleKnowledgeBase extends KnowledgeBase {
        private final KnowledgeBase rre = new ReferenceKnowledgeBase();
        private final OperationFactory PFACT = new SimpleCycleOperationFactory(this);
        private final OperandOwnerFactory OO_FACT = new SimpleCycleOperandOwnerFactory(this);

        protected OperandOwnerFactory getOperandOwnerFactory() {
            return OO_FACT;
        }

        protected OperatorFactory getOperatorFactory() {
            return rre.getOperatorFactory();
        }

        protected OperationFactory getOperationFactory() {
            return PFACT;
        }
    }

    private static class SimpleCycleOperandOwnerFactory extends OperandOwnerFactory {
        public SimpleCycleOperandOwnerFactory(KnowledgeBase engine) {
            super(engine);
        }

        public void load() throws AriadneException {
            try {
                instantiateFailFast(1, BooleanOperandOwner.class, Boolean.TRUE);
                instantiateFailFast(2, BooleanOperandOwner.class, Boolean.FALSE);

                instantiateFailFast(3, NumericOperandOwner.class, 100.10d);
                instantiateFailFast(4, NumericOperandOwner.class, 10.0d);
                instantiateFailFast(5, NumericOperandOwner.class, 100.00d);
                instantiateFailFast(6, NumericOperandOwner.class, 1.0d);

                instantiateFailFast(7, NumericOperandOwner.class, 123.0d);
                instantiateFailFast(8, CompoundOperandOwner.class, 1);
            }
            catch (AriadneException rex) {
                throw rex; // we don't want these to be filtered
            }
            catch (Exception e) {
                throw new AriadneException(e.toString(), e);
            }

        }

        public void store() throws AriadneException {

        }
    }

    private static class SimpleCycleOperationFactory extends OperationFactory {
        public SimpleCycleOperationFactory(KnowledgeBase engine) {
            super(engine);
        }

        public void load() throws AriadneException {

            getPredicate(1, 1, 7, 8); // true || false?
        }

        public void store() throws AriadneException {

        }
    }

    public static class ComplexCycleKnowledgeBase extends KnowledgeBase {
        private static final KnowledgeBase rkb = new ReferenceKnowledgeBase();
        private final OperationFactory P_FACT = new ComplexCycleOperationFactory(this);
        private final OperandOwnerFactory ooFact = new ComplexCycleOperandOwner(this);

        protected OperandOwnerFactory getOperandOwnerFactory() {
            return ooFact;
        }

        protected OperatorFactory getOperatorFactory() {
            return rkb.getOperatorFactory();
        }

        protected OperationFactory getOperationFactory() {
            return P_FACT;
        }
    }


    private static class ComplexCycleOperationFactory extends OperationFactory {
        public ComplexCycleOperationFactory(KnowledgeBase engine) {
            super(engine);
        }

        public void load() throws AriadneException {

            getPredicate(1, 10, 7, 2); // the use of 10 will cause a cycle
            getPredicate(2, 3, 1, 4); // 100.10 >= 10
            getPredicate(3, 5, 4, 6); // 100.00 <= 1
            getPredicate(4, 8, 7, 9); // (100.100 >= 10) || (true || false)
            getPredicate(5, 10, 8, 11); // ((100.100 >= 10) || (true || false)) && (100 <= 1)
        }

        public void store() throws AriadneException {

        }
    }

    private static class ComplexCycleOperandOwner extends OperandOwnerFactory {

        public ComplexCycleOperandOwner(KnowledgeBase engine) {
            super(engine);
        }

        public void load() throws AriadneException {

            try {
                instantiateFailFast(1, BooleanOperandOwner.class, Boolean.TRUE);
                instantiateFailFast(2, BooleanOperandOwner.class, Boolean.FALSE);

                instantiateFailFast(3, NumericOperandOwner.class, 100.10d);
                instantiateFailFast(4, NumericOperandOwner.class, 10.0d);
                instantiateFailFast(5, NumericOperandOwner.class, 100.00d);
                instantiateFailFast(6, NumericOperandOwner.class, 1.0d);

                instantiateFailFast(7, NumericOperandOwner.class, 123.0d);

                instantiateFailFast(8, CompoundOperandOwner.class, 2);   //100.10 >= 10
                instantiateFailFast(9, CompoundOperandOwner.class, 1);   //
                instantiateFailFast(10, CompoundOperandOwner.class, 4);   // (100.10 >= 10) || 
                instantiateFailFast(11, CompoundOperandOwner.class, 3);   //11
            }
            catch (AriadneException rex) {
                throw rex; // we don't want these to be filtered
            }
            catch (Exception e) {
                throw new AriadneException(e.toString(), e);
            }
        }

        public void store() throws AriadneException {

        }
    }

    public static class NullInputValueOperandOwner extends TestOperandOwner {
        public NullInputValueOperandOwner(Integer id, KnowledgeBase kb) {
            super(id);
        }

        public Double getOperand(TestRuntimeObject anyArg) throws AriadneException {
            return 10d;
        }
    }

    private static abstract class TestOperandOwner extends MinimalOperandOwner<Double, TestRuntimeObject> {
        protected TestOperandOwner(Integer id) {
            super(id);
        }

        public Class getRuntimeType() {
            return TestRuntimeObject.class;
        }

        public Class getOperandType() {
            return Double.class;
        }
    }

    public static class XOperandOwner extends TestOperandOwner {

        public XOperandOwner(Integer id, KnowledgeBase kb) {
            super(id);
        }

        public Double getOperand(TestRuntimeObject anyArg) throws AriadneException {
            return anyArg.getX();
        }

        public String toString() {
            return "x";
        }

    }

    public static class YOperandOwner extends TestOperandOwner {

        public YOperandOwner(Integer id, KnowledgeBase kb) {
            super(id);
        }

        public Double getOperand(TestRuntimeObject anyArg) throws AriadneException {
            return anyArg.getY();
        }

        public String toString() {
            return "y";
        }
    }

    private static class TestRuntimeObject {
        private Double x = 10d;
        private Double y = 20d;


        public TestRuntimeObject(Double x, Double y) {
            this.x = x;
            this.y = y;
        }

        public Double getX() {
            return x;
        }

        public Double getY() {
            return y;
        }

        public void setX(Double x) {
            this.x = x;
        }

        public void setY(Double y) {
            this.y = y;
        }
    }

    public static class FailOperatorKnowledgeBase extends EmptyKnowledgeBase {

        protected OperatorFactory getOperatorFactory() {
            return new OperatorFactory() {

                public void load() throws AriadneException {
                    try {
                        addOperator(1, Add.class.getName());
                        // this operator will try to register the same op String as Add
                        addOperator(2, FailOperator.class.getName());
                    }
                    catch (IllegalArgumentException iae) {
                        throw iae;
                    }
                    catch (AriadneException ex) {
                        throw ex;
                    }
                    catch (Exception e) {
                        throw new AriadneException("A specific error was expected. This code shouldn't be hit");
                    }
                }

                public void store() throws AriadneException {

                }
            };
        }
    }

    public static class FailOperator implements Operator {
        private Integer id = null;

        public FailOperator(Integer id) {
            this.id = id;
        }

        public Boolean acceptsOperand(Class clazz) {
            return null;
        }

        public Boolean canShortCircuit(Object lho) {
            return null;
        }

        public boolean equals(Operator oper) {
            return false;
        }

        public Object evaluate(Object lho, Object rho) {
            return null;
        }

        public Class getEvaluationType() {
            return null;
        }

        public Integer getId() {
            return id;
        }

        public Set<String> getRegistrations() {
            return new HashSet<String>(Arrays.asList("+", "-", "min"));
        }

        public Boolean isCommutative() {
            return null;
        }

        public String toString() {
            return "failed operator";
        }
    }

    private static class CountingObserver implements Observer {
        private int opCount = 0;
        private int operCount = 0;
        private int ooCount = 0;

        public int getOperatorCount() {
            return opCount;
        }

        public int getOperationCount() {
            return operCount;
        }

        public int getOperandOwnerCount() {
            return ooCount;
        }

        public void update(Observable o, Object arg) {
            if (arg instanceof Operation)
                operCount++;
            else if (arg instanceof Operator)
                opCount++;
            else if (arg instanceof OperandOwner)
                ooCount++;
            else
                throw new IllegalArgumentException("Observing illegal type " + arg.getClass());
        }

        @Override
        public String toString() {
            return "Operations = " + operCount + " Operators " + opCount + " OperandOwners " + ooCount;
        }
    }
}
