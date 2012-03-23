package org.bongiorno.ariadne.misc;

import java.util.StringTokenizer;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.operandowners.NumericOperandOwner;
import org.bongiorno.ariadne.operations.OperationFactory;
import org.bongiorno.ariadne.operators.OperatorFactory;
import org.bongiorno.ariadne.interfaces.Operator;
import org.bongiorno.ariadne.interfaces.Predicate;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Feb 5, 2008
 * Time: 2:05:03 PM
 */
public class CLIEvaluator {
//    private final String preds = EmptyDataLoader.class.getName();
//    private final String owners = PrimitiveOpOwnerLoader.class.getName();
//    private final DefaultRuleEngine engine = new DefaultRuleEngine(owners, ops, preds);
    private OperationFactory pFact = null;
    private OperatorFactory opFact = null;

    // this class is total work in progress

    public static void main(String[] args) throws Exception {


        new CLIEvaluator();

        Predicate prior = null;
//        for (String arg : args) {
//            Predicate a = pFact.getPredicate(NumericOperandOwner.class, args[0], args[1], args[2]);   // true
//            if (prior != null) {
//                Predicate next = pFact.getPredicate(prior, )
//            }
//        }
//
//        Predicate b = pFact.getPredicate(NumericOperandOwner.class, args[1], "<=", "100"); //false
//
//        engine.validate();
//        Predicate fin = pFact.getPredicate(finEq, , hundred); //false
//        System.out.println(fin + " = " + fin.evaluate(null));
    }

    private CLIEvaluator() throws AriadneException {
//        engine.load();
//        pFact = engine.getPredicateFactory();
//        opFact = engine.getOperatorFactory();
    }

    private Predicate parse(String input) throws AriadneException {
        Predicate result = null;
        OperandOwner lho = null;
        Operator op = null;
        OperandOwner rho = null;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!Character.isWhitespace(c)) {
                if (c == '(')
                    result = parse(input.substring(i));
                String sLho = getToken(input.substring(i));

                StringTokenizer toks = new StringTokenizer("() ", input.substring(i));
                Predicate temp = pFact.getPredicate(NumericOperandOwner.class,toks.nextToken(),toks.nextToken(),toks.nextToken());
                

            }


        }
        return result;
    }

    private String getToken(String in) {
        int i = 0;

        // skip leading whitespace
        while(Character.isWhitespace(in.charAt(i)))
            i++;

        while(!Character.isWhitespace(in.charAt(i)))
            i++;

        return in.substring(0,i);
    }
    
}
