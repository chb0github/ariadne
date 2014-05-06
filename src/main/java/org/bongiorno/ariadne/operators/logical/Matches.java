package org.bongiorno.ariadne.operators.logical;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Feb 24, 2009
 * Time: 2:41:38 PM
 */
public class Matches extends LogicalOperator<String> {

    private static final Set<String> REGISTRATIONS =
            createRegistration("matches", "match","regex","MATCHES","MATCH","REGEX");

    /**
     *
     */
    public Matches(Integer id) {
        super(id);
    }

    public Boolean evaluate(String lho, String pattern) {
        Boolean retVal = null;
        if(lho != null && pattern != null)
            retVal = lho.matches(pattern);
        return retVal;
    }

    public Boolean canShortCircuit(String lho) {
        return lho == null;
    }

    public Boolean isCommutative() {
        return false;
    }

    public Boolean acceptsOperand(Class<String> clazz) {
        return clazz.equals(String.class);
    }

    public Set<String> getRegistrations() {
        return REGISTRATIONS;
    }

    @Override
    public String toString() {
        return "matches";
    }
}
