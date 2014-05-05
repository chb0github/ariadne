package org.bongiorno.ariadne.operandowners;

import java.util.HashMap;
import java.util.Map;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.KnowledgeBase;


/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 22, 2007
 * Time: 5:33:30 PM
 *
 * An OperandOwner that always returns a Boolean. RUN_T is the runtime type for evaluation, but it will be ignored.
 *
 * This object simply wraps an immutable constant and always returns it. This is so you can compare an object that
 * requires runtime operand expression (the purpose of OperandOwner) with a constant Boolean value like: 'true'
 *
 * implements LogicalOperandOwner<RUN_T> is semi redundant through AbstractOperandOwner, but it does apply a sub interface
 * consistency. This might change
 */
public class BooleanOperandOwner<RUN_T> extends ConstantOperandOwner<Boolean,RUN_T> implements LogicalOperandOwner<RUN_T> {

    private static final Map<String,Boolean> TRANSLATE_MAP = new HashMap<String,Boolean>();
    // the translation features build into Boolean are quite simple: == "true" everything else is false
    // but that doesn't work for us since a DB may decide to store a boolean value as "1" or "0"
    static {
        TRANSLATE_MAP.put("1",Boolean.TRUE);
        TRANSLATE_MAP.put("true",Boolean.TRUE);
        TRANSLATE_MAP.put("y",Boolean.TRUE);
        TRANSLATE_MAP.put("yes",Boolean.TRUE);

        TRANSLATE_MAP.put("0",Boolean.FALSE);
        TRANSLATE_MAP.put("false",Boolean.FALSE);
        TRANSLATE_MAP.put("n",Boolean.FALSE);
        TRANSLATE_MAP.put("no",Boolean.FALSE);
    }
    private Object initValue = null;
    /**
     *
     * @param engine not used. This operand owner operates indepent of
     * any engine context because it's a constant. However, if must maintain
     * a consistent constructor with the other OperandOwners
     * @param bool the string representation of your constant. Supported values are:
     * {1,true,y,yes} => true  {0,false,n,no} => false
     * @param id the id that this OperandOwner will hold
     */
    public BooleanOperandOwner(Integer id,KnowledgeBase engine,String bool) {
        this(id, engine, TRANSLATE_MAP.get(bool));
        // this is saved for debug only
        initValue = bool;
    }

    public BooleanOperandOwner(Integer id, KnowledgeBase engine,Boolean bool) {
        // yes, this is an intentional NPE! I want it to fail here and now
        super(id,bool);
    }

    /**
     * @return Boolean.class always
     */
    public Class<Boolean> getOperandType() {
        return Boolean.class;
    }

    @Override
    public void validate() throws AriadneException {
        // a null input means that the lookup failed
        if(getInput() == null) {
            String message = "Attempted to create BooleanOperandOwner with an unrecognized value of " + initValue;
            message += " supported input types are " + TRANSLATE_MAP.keySet();
            throw new AriadneException(message);
        }
    }
}