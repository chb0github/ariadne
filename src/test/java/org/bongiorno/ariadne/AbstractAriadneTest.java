package org.bongiorno.ariadne;

import org.bongiorno.ariadne.implementations.EmptyKnowledgeBase;
import org.bongiorno.ariadne.implementations.ReferenceKnowledgeBase;
import org.bongiorno.ariadne.interfaces.Equation;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Operator;
import org.bongiorno.ariadne.interfaces.Predicate;
import org.bongiorno.ariadne.operandowners.*;
import org.bongiorno.ariadne.operations.OperationFactory;
import org.bongiorno.ariadne.operators.OperatorFactory;
import org.bongiorno.ariadne.operators.arithmetic.Add;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author chribong
 */
public abstract class AbstractAriadneTest {


    @Test
    public void testPass() throws Exception {


    }

    // each one of these private TestStep classes allows us to have, more or less, indentical tests for XML
    // database or any other functional component by abstracting out certain maintenance portions crucial
    // to maintaining the underlying data store. Really, this is most crucial to the XML test for teardown.
    // However, it is also used for validation steps like in a DB test where it veri
    protected static AbstractDBIntegrationTest.TestStep NO_OP = new AbstractDBIntegrationTest.TestStep() {
        public void perform() {

        }
    };

    protected KnowledgeBase createExample(KnowledgeBase kb) throws AriadneException {
        kb.load();
        kb.validate();

        Predicate alpha = kb.getPredicate(Boolean.TRUE, "&&", Boolean.FALSE);
        Predicate a = kb.getPredicate(NumericOperandOwner.class, 123.12d, ">", 100.0d);   // true
        Predicate b = kb.getPredicate(NumericOperandOwner.class, 400.0d, "<=", 100.0d); //false

        Predicate aANDb = kb.getPredicate(a, "&&", b);            // false
        Predicate c = kb.getPredicate(NumericOperandOwner.class, 24.0d, ">=", 87.0d); // false
        Predicate d = kb.getPredicate(NumericOperandOwner.class, 1.0d, "<", 3.14159d); //true

        Predicate cORd = kb.getPredicate(c, "||", d); // true


        Equation<Double,Object> e = kb.getEquation(123.12d, "+", 100.0d);   // 223.12
        Equation<Double,Object> f = kb.getEquation(400.0d, "/", 100.0d); //4

        Equation<?,Object> eMINf = kb.getEquation(e, "min", f); // 4
        Equation g = kb.getEquation(2.110d, "*", 3.0d); // 6.33
        Equation h = kb.getEquation(1.0d, "-", 3.14159d); //-2.14159

        Equation gMAXh = kb.getEquation(g, "max", h); // 6.33

        Equation<Double, Double> equation = kb.getEquation(eMINf, "pow", gMAXh); // 4 ^ 6 = 6472.018426784786d


        Predicate<Boolean, Object> p = kb.getPredicate(aANDb, "&&", cORd); // false
        OperandOwner oo = kb.getOperandOwner(NumericOperandOwner.class, 10000.0d);
        Predicate<Boolean, Object> q = kb.getPredicate(oo, ">", equation);
        Predicate<Boolean, Object> last = kb.getPredicate(p, "||", q);
        return kb;
    }

    protected KnowledgeBase getLoadedKnowledgeBase(String knowledgeStore) throws Exception {
        KnowledgeBase kb = KnowledgeBase.getInstance(knowledgeStore);
        kb.load();
        return kb;
    }

    @Test
    public void testExample() throws Exception {
        KnowledgeBase kb = KnowledgeBase.getInstance();

        kb.load();

        OperandOwner xoo = kb.getOperandOwner(XOperandOwner.class);
        OperandOwner yoo = kb.getOperandOwner(YOperandOwner.class);


        Equation<Double,?> e = kb.getEquation(xoo, "+", yoo);   // 10 + 20
        Equation<Double,?> f = kb.getEquation(yoo, "/", xoo); // 20 /10

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

    protected KnowledgeBase getLoadedKnowledgeBase() throws Exception {
        return getLoadedKnowledgeBase("reference");
    }

    protected void doLargeInsert(String kbSource, TestStep init, TestStep preStore, TestStep postStore) throws Exception {
        init.perform();
        KnowledgeBase kb = KnowledgeBase.getInstance(kbSource);
        kb.load();
        kb.validate();
        Predicate p = kb.getPredicate(Boolean.TRUE, "||", Boolean.FALSE); // (true || False) 15 chars
        Predicate pPrime = p;
        for (int length = 0; length < 5000; length += 15)
            pPrime = kb.getPredicate(pPrime, "&&", p); // continue to build this tree until the string expression is huge!

        preStore.perform();
        kb.store();
        postStore.perform();
    }

    /**
     * create a whole lot of random equations to populate our table to make sure sclability isn't a problem
     *
     * @param kb
     * @throws org.bongiorno.ariadne.AriadneException
     */
    protected void randomizeKnowledgeBase(KnowledgeBase kb) throws AriadneException {
        Random rand = new Random();

        String[] ops = {"+", "-", "/", "*", "min", "max", "pow"};
        for (Double i = 1d; i < 10000d; i++)
            kb.getEquation(Math.random(), ops[rand.nextInt(ops.length)], Math.random());


        int opsCount = kb.getOperations().size();
        for (int i = 1; i < 500; i++) {
            Equation l = kb.getEquation(rand.nextInt(opsCount));
            Equation r = kb.getEquation(rand.nextInt(opsCount));
            kb.getEquation(l, ops[rand.nextInt(ops.length)], r);
        }
    }

    protected void nullOerandOwnerInputStore(String kbSource, TestStep initStep, TestStep preStore, TestStep postStore) throws Exception {
        initStep.perform();
        KnowledgeBase kb = KnowledgeBase.getInstance(kbSource);
        kb.load();
        OperandOwner noo = kb.getOperandOwner(NullInputValueOperandOwner.class);
        OperandOwner nooo = kb.getOperandOwner(NullInputValueOperandOwner.class);

        OperandOwner ten = kb.getOperandOwner(NumericOperandOwner.class, 10.0d);
        OperandOwner eleven = kb.getOperandOwner(NumericOperandOwner.class, 11.0d);

        Equation<Double, Object> test = kb.getEquation(noo, "+", ten);
        kb.getEquation(ten, "+", eleven); // just to make sure null and non-null input work ok
        Double d = test.evaluate(null);
        assertEquals(d, new Double(20d));

        test = kb.getEquation(nooo, "+", noo);
        d = test.evaluate(null);
        assertEquals(d, new Double(20d));

        kb.validate();
        preStore.perform();
        kb.store();
        postStore.perform();

        KnowledgeBase kb2 = KnowledgeBase.getInstance(kbSource);
        kb2.load();

        assertEquals(kb, kb2);
    }

    static interface TestStep {
        void perform() throws Exception;
    }


    protected static abstract class TestOperandOwner extends MinimalOperandOwner<Double, TestRuntimeObject> {
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

    protected static class TestRuntimeObject {
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




    protected static class SimpleCycleOperandOwnerFactory extends OperandOwnerFactory {
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


    public static class NullInputValueOperandOwner extends MinimalOperandOwner<Double, Object> {
        public NullInputValueOperandOwner(Integer id, KnowledgeBase kb) {
            super(id);
        }

        public Double getOperand(Object anyArg) throws AriadneException {
            return 10d;
        }

        public Class getOperandType() {
            return Double.class;
        }

        public Class getRuntimeType() {
            return Object.class;
        }

        @Override
        public String toString() {
            return "NullInput:10.0";

        }
    }



    protected static class SimpleCycleOperationFactory extends OperationFactory {
        public SimpleCycleOperationFactory(KnowledgeBase engine) {
            super(engine);
        }

        public void load() throws AriadneException {

            getPredicate(1, 1, 7, 8); // true || false?
        }

        public void store() throws AriadneException {

        }
    }
    protected static class ComplexCycleOperationFactory extends OperationFactory {
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

    protected static class ComplexCycleOperandOwner extends OperandOwnerFactory {

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



    protected static class FailOperator implements Operator {
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
}
