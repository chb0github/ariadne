package org.bongiorno.ariadne.operators.set;

import org.bongiorno.ariadne.AbstractAriadneTest;
import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Operation;
import org.bongiorno.ariadne.interfaces.Operator;
import org.bongiorno.ariadne.operandowners.SetOperandOwner;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;


/**
 * @author chribong
 */
public class SetOperatorTests extends AbstractAriadneTest {

    @Test
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


    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void testComplexSetOperation() {
        Set<String> a = new HashSet<String>(Arrays.asList("Christian", "Nimish", "Todor"));
        Set<String> b = new HashSet<String>(Arrays.asList("Christian", "Kevin", "Charles"));

    }

}
