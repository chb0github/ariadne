package org.bongiorno.ariadne.operandowners;

import org.bongiorno.ariadne.AbstractAriadneTest;
import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.interfaces.Equation;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Operation;
import org.bongiorno.ariadne.interfaces.Predicate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author chribong
 */
public class OperandOwnTests extends AbstractAriadneTest{

    public static class NullInputValueOperandOwner extends TestOperandOwner {
        public NullInputValueOperandOwner(Integer id, KnowledgeBase kb) {
            super(id);
        }

        public Double getOperand(TestRuntimeObject anyArg) throws AriadneException {
            return 10d;
        }
    }


    @Test
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

    @Test
    public void testDateOperandOwner() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner vBday = engine.getOperandOwner(DateOperandOwner.class, "1981-12-02T23:00:00Z");
        OperandOwner myBday = engine.getOperandOwner(DateOperandOwner.class, "1976-09-10T09:00:00Z");
        Predicate p = engine.getPredicate(myBday, "<", vBday);
        assertEquals(p.evaluate(null), Boolean.TRUE);
    }

    @Test
    public void testStringOperandOwner() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner a = engine.getOperandOwner(StringOperandOwner.class, "TEST");
        OperandOwner b = engine.getOperandOwner(StringOperandOwner.class, "BLAH");
        Predicate p = engine.getPredicate(a, "==", b);
        assertEquals(p.evaluate(null), Boolean.FALSE);
    }

    @Test
    public void testNumericOperandOwner() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner a = engine.getOperandOwner(NumericOperandOwner.class, "10.1");
        OperandOwner b = engine.getOperandOwner(NumericOperandOwner.class, 10.1);
        Predicate p = engine.getPredicate(a, "==", b);
        assertEquals(p.evaluate(null), Boolean.TRUE);
    }

    @Test
    public void testBooleanOperandOwner() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();
        OperandOwner a = engine.getOperandOwner(BooleanOperandOwner.class, "true");
        OperandOwner b = engine.getOperandOwner(BooleanOperandOwner.class, Boolean.FALSE);
        Predicate p = engine.getPredicate(a, "==", b);
        assertEquals(p.evaluate(null), Boolean.FALSE);
    }
    @Test
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

}
