package org.bongiorno.ariadne;

import org.bongiorno.ariadne.interfaces.*;
import org.bongiorno.ariadne.operandowners.NumericOperandOwner;
import org.bongiorno.ariadne.operandowners.StringOperandOwner;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 * @author chribong
 */
public class KnowledgeBaseTests extends AbstractAriadneTest {


    @Test
    public void testKnowledgeBaseEquality() throws Exception {
        KnowledgeBase alpha = getLoadedKnowledgeBase();
        KnowledgeBase beta = getLoadedKnowledgeBase();

        assertEquals(alpha, alpha); // shallow check

        assertTrue(alpha != beta);// we want a deep equality check
        assertEquals(alpha, beta);

    }

    @Test(expected = AriadneException.class)
    public void testCheckForSimpleCycleDetection() throws Exception {
        // this System property insert here is also a test for the dynamic insertion of
        // knowledge bases into the KB factory
        System.getProperties().put("engines.simple_cycle",
                "com.org.bongiorno.ariadne.UnitTests$SimpleCycleKnowledgeBase");

        KnowledgeBase cycle = getLoadedKnowledgeBase("simple_cycle");
        cycle.validate();
    }


    @Test(expected = AriadneException.class)
    public void testCheckForComplexCycleDetection() throws Exception {
        // this System property insert here is also a test for the dynamic insertion of
        // knowledge bases into the KB factory
        System.getProperties().put("engines.complex_cycle",
                "com.org.bongiorno.ariadne.UnitTests$ComplexCycleKnowledgeBase");

        KnowledgeBase cycle = getLoadedKnowledgeBase("complex_cycle");
        cycle.validate();

    }

    @Test(expected = AriadneException.class)
    public void testDupOperatorRegistrationFail() throws Exception {
        // this System property insert here is also a test for the dynamic insertion of
        // knowledge bases into the KB factory
        System.getProperties().put("engines.fail_operator",
                "com.org.bongiorno.ariadne.UnitTests$FailOperatorKnowledgeBase");

        KnowledgeBase kb = KnowledgeBase.getInstance("fail_operator");

        kb.load();

    }


    @Test(expected = AriadneException.class)
    public void testIncompatibleReturnTypeCompatibility() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase("empty");
        OperandOwner l = engine.getOperandOwner(NumericOperandOwner.class, 10d);
        OperandOwner r = engine.getOperandOwner(NumericOperandOwner.class, 100d);
        Equation e = engine.getEquation(l, "+", r);
        OperandOwner rr = engine.getOperandOwner(StringOperandOwner.class, "Christian");
        Equation ef = engine.getEquation(e, "==", rr);
        engine.validate();

    }

    @Test
    public void testApplyRules() throws Exception {
        KnowledgeBase kb = KnowledgeBase.getInstance();

        kb.load();

        OperandOwner xoo = kb.getOperandOwner(XOperandOwner.class);
        OperandOwner yoo = kb.getOperandOwner(YOperandOwner.class);


        Equation e = kb.getEquation(xoo, "+", yoo);
        Equation f = kb.getEquation(yoo, "/", xoo);

        Equation<Double, TestRuntimeObject> ef = kb.getEquation(e, "/", f);

        OperandOwner nine = kb.getOperandOwner(NumericOperandOwner.class, 9.0d);
        Predicate<Double, TestRuntimeObject> p = kb.getPredicate(ef, ">", nine);
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
        assertEquals(new HashSet<Predicate<?, TestRuntimeObject>>(Arrays.asList(p)), results);
        results = kb.apply(new TestRuntimeObject(1d, 1d));
        assertTrue(results.size() == 0);

        // have a default set of rule application methods is still under consideration
//        kb.setRuleMethod(APPLY_ALL);
//        results = kb.apply(new TestRuntimeObject(10d, 20d));
//        assertEquals(results,new HashSet<Predicate<?,TestRuntimeObject>>(Arrays.asList(p)));
//        results = kb.apply(new TestRuntimeObject(1d, 1d));
//        assertTrue(results.size() == 0);


    }


    @Test
    public void testCrossTypeRuntimeAssignability() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase("empty");
        OperandOwner l = engine.getOperandOwner(NumericOperandOwner.class, 10d);
        OperandOwner xoo = engine.getOperandOwner(XOperandOwner.class);
        Equation e = engine.getEquation(l, "+", xoo);
        engine.validate();
    }

    @Test
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


    @Test
    public void testCreateNoDuplicateOperations() throws Exception {
        KnowledgeBase kb = getLoadedKnowledgeBase();

        Set<Operation> operations = kb.getOperations();
        assertTrue(operations.size() > 0);

        for (Operation operation : operations) {
            Operation other = kb.getOperation(operation.getId());
            assertTrue(operation == other);
        }
    }

    @Test
    public void testCreateNoDuplicateOperandOwners() throws Exception {
        KnowledgeBase kb = getLoadedKnowledgeBase();
        Set<OperandOwner> owners = kb.getOperandOwners();
        assertTrue(owners.size() > 0);

        for (OperandOwner oo : owners) {
            OperandOwner other = kb.getOperandOwner(oo.getClass(), oo.getInput());
            assertTrue(oo == other);
        }
    }

    @Test
    public void testCreateNoDuplicatePredicatesSimple() throws Exception {
        KnowledgeBase kb = getLoadedKnowledgeBase();

        Predicate p = kb.getPredicate(NumericOperandOwner.class, 100.00d, "==", 10.0d);
        Predicate q = kb.getPredicate(NumericOperandOwner.class, 100.00d, "==", 10.0d);
        assertTrue(p == q);// no duplicates allowed. A different instance is not allowed
        kb.validate();
    }

    @Test
    public void testCreateNoDuplicatePredicatesComplex() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();


        Predicate<Double, Object> a = engine.getPredicate(NumericOperandOwner.class, 123.12d, ">", 100.0d);   // true
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

    @Test
    public void testNullValueOperandOwner() throws Exception {
        KnowledgeBase engine = getLoadedKnowledgeBase();

        OperandOwner noo = engine.getOperandOwner(NullInputValueOperandOwner.class);
        OperandOwner ten = engine.getOperandOwner(NumericOperandOwner.class, 10.0d);

        Equation<Double, Object> test = engine.getEquation(noo, "+", ten);
        Double d = test.evaluate(null);
        assertEquals(d, 20d);

    }

    @Test
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

    @Test
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


}
