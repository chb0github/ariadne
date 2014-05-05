package org.bongiorno.ariadne;

import org.bongiorno.ariadne.interfaces.Equation;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Predicate;
import org.bongiorno.ariadne.operandowners.NumericOperandOwner;

import static org.junit.Assert.assertEquals;

/**
 * @author chribong
 */
public abstract class AbstractIntegrationTest extends AbstractAriadneTest {


    protected void doStore(String kbSource, TestStep init, TestStep preStore, TestStep postStore) throws Exception {
        init.perform();
        KnowledgeBase kb = KnowledgeBase.getInstance(kbSource);
        kb.load();
        kb.validate();

        Predicate alpha = kb.getPredicate(Boolean.TRUE, "&&", Boolean.FALSE);
        Predicate a = kb.getPredicate(NumericOperandOwner.class, 123.12d, ">", 100.0d);   // true
        Predicate b = kb.getPredicate(NumericOperandOwner.class, 400.0d, "<=", 100.0d); //false

        Predicate aANDb = kb.getPredicate(a, "&&", b);            // false
        Predicate c = kb.getPredicate(NumericOperandOwner.class, 24.0d, ">=", 87.0d); // false
        Predicate d = kb.getPredicate(NumericOperandOwner.class, 1.0d, "<", 3.14159d); //true

        Predicate cORd = kb.getPredicate(c, "||", d); // true


        Equation e = kb.getEquation(123.12d, "+", 100.0d);   // 223.12
        Equation f = kb.getEquation(400.0d, "/", 100.0d); //4

        Equation eMINf = kb.getEquation(e, "min", f); // 4
        Equation g = kb.getEquation(2.110d, "*", 3.0d); // 6.33
        Equation h = kb.getEquation(1.0d, "-", 3.14159d); //-2.14159

        Equation gMAXh = kb.getEquation(g, "max", h); // 6.33

        Equation<Double, Double> equation = kb.getEquation(eMINf, "pow", gMAXh); // 4 ^ 6 = 6472.018426784786d


        Predicate<Boolean, Object> p = kb.getPredicate(aANDb, "&&", cORd); // false
        OperandOwner oo = kb.getOperandOwner(NumericOperandOwner.class, 10000.0d);
        Predicate<Boolean, Object> q = kb.getPredicate(oo, ">", equation);
        Predicate<Boolean, Object> last = kb.getPredicate(p, "||", q);
        last.evaluate(null);
        kb.validate();

        preStore.perform();
        kb.store();

        postStore.perform();
        KnowledgeBase kb2 = KnowledgeBase.getInstance(kbSource);
        kb2.load();
        kb2.validate();
        assertEquals(kb, kb2);
    }

    protected void doHugeStore(String kbSource, TestStep init, TestStep preStore, TestStep postStore) throws Exception {
        init.perform();
        KnowledgeBase kb = KnowledgeBase.getInstance(kbSource);
        kb.load();
        kb.validate();
        randomizeKnowledgeBase(kb);
        kb.validate();

        preStore.perform();
        kb.store();

        postStore.perform();

        KnowledgeBase kb2 = KnowledgeBase.getInstance(kbSource);
        kb2.load();
        kb2.validate();
        assertEquals(kb, kb2);

    }
}
