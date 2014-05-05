package org.bongiorno.ariadne;

import org.bongiorno.ariadne.interfaces.*;
import org.bongiorno.ariadne.operandowners.NumericOperandOwner;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import static org.junit.Assert.*;



/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 23, 2007
 * Time: 11:22:19 AM
 * One big unit test to confirm the runtime combination between Arithmetic expressions and logical Expressions
 */
public class UnitTests extends AbstractAriadneTest{


    public void testSum() throws AriadneException {
        for(int i = 0; i <10; i++)
            evalTest(6000);


    }
    private void evalTest(int count) throws AriadneException {
        KnowledgeBase kb = KnowledgeBase.getInstance(); // default, in memory, empty but for operators
        kb.load();
        kb.validate();
        Random rand = new Random(1);
        Equation last = kb.getEquation(0d, "+", 0d);
        long memstart = currentMem();
        System.out.println("Evaluating " + count);

        for (double i = 1; i <= count; i++) {
            last = kb.getEquation(last, "+", (double) rand.nextInt(count));
        }
        kb.validate();
        long now = System.nanoTime();
        Double sum = (Double) last.evaluate(null);
        long end = System.nanoTime();
        System.out.println("Time to evaluate using ariadne: " + ((end - now) / 1000d));
        sum = 0.0d;
        now = System.nanoTime();
        for (double i = 1; i <= count; i++) {
            sum += (double) rand.nextInt(count);
        }
        end = System.nanoTime();
        System.out.println("Time to evaluate natively: " + ((end - now) / 1000d));
    }
    private long currentMem() {
        Runtime runtime = Runtime.getRuntime();
        long memStart = runtime.totalMemory() - runtime.freeMemory();
        return ((memStart / 1024) / 1024);
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
