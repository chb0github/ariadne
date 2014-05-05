package org.bongiorno.ariadne.actions;

import org.bongiorno.ariadne.AbstractAriadneTest;
import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.interfaces.Action;
import org.bongiorno.ariadne.interfaces.Equation;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Predicate;
import org.bongiorno.ariadne.operandowners.NumericOperandOwner;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


/**
 * @author chribong
 */
public class ActionTests extends AbstractAriadneTest{

    @Test
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

        p.addAction(new SeriesAction<>(a,b));
        Set<Predicate<?, TestRuntimeObject>> results = kb.apply(new TestRuntimeObject(10d, 20d));

        assertEquals(p.getActions().size(),1);
        assertTrue(actionsRun.containsAll(Arrays.asList(a, b)));

    }

    @Test
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

        actions.addAction(new SimplePrioritizableAction<>(3,a));
        actions.addAction(new SimplePrioritizableAction<>(2,b));
        actions.addAction(new SimplePrioritizableAction<>(1,c));
        SimplePrioritizableAction<TestRuntimeObject> series = new SimplePrioritizableAction<TestRuntimeObject>(4,
                new SeriesAction<>(a, b));
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

        series = new SimplePrioritizableAction<>(0,
                new SeriesAction<>(a, b));
        actions.addAction(series);
        actionsRun.clear();
        results = kb.apply(new TestRuntimeObject(10d, 20d));
        assertEquals(p.getActions().size(),1);
        assertTrue(actionsRun.equals(Arrays.asList(a,b,c,b,a)));

    }
    @Test
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
    @Test
    public void testSameTypeRuntimeAssignability() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase("empty");
        OperandOwner l = engine.getOperandOwner(NumericOperandOwner.class, 10d);
        OperandOwner r = engine.getOperandOwner(NumericOperandOwner.class, 1d);
        Equation e = engine.getEquation(l, "+", r);
        engine.validate();
    }
}
