package org.bongiorno.ariadne.operandowners;

import org.bongiorno.ariadne.KnowledgeBase;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Sep 21, 2007
 * Time: 1:01:32 PM
 */
public class StringOperandOwner<RUN_T> extends ConstantOperandOwner<String,RUN_T> {

    /**
     *
     * @param engine not used
     * @param s the String constant
     * @param id
     */
    public StringOperandOwner(Integer id, KnowledgeBase engine,String s) {
        super(id,s);
    }
}
