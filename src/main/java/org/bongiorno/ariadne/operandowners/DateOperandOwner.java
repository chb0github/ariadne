package org.bongiorno.ariadne.operandowners;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bongiorno.ariadne.KnowledgeBase;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 22, 2007
 * Time: 5:32:50 PM
 *
 * An OperandOwner that always returns a Date. RUN_T is the runtime type for evaluation, but it will be ignored.
 * This object simply wraps an immutable constant and always returns it. This is so you can compare an object that
 * requires runtime operand expression (the purpose of OperandOwner) with a constant of some Date like "12/2/81"
 */
public class DateOperandOwner<RUN_T> extends ConstantOperandOwner<Date,RUN_T> {
//    @Pattern("\\d{4,}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}(?:Z|(?:\\+|-)\\d{2}:?\\d{2})")

    private static final DateFormat UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    /**
     * Creates this object and delegates it's return type to num
     * @param date the value that will always be returned by { #getOperandOwner(Object)}
     * @param id
     */
    public DateOperandOwner(Integer id, Date date) {
        super(id, date);
    }

    /**
     * Delegates to a date using the default locale format
     * @param engine not used
     * @param date the date as a string using the default locale
     * @throws ParseException if the string was formatted improperly
     * @param id 
     */
    public DateOperandOwner(Integer id, KnowledgeBase engine,String date) throws ParseException {
        this(id, UTC.parse(date));
    }

    public DateOperandOwner(Integer id, KnowledgeBase engine,Date date) throws ParseException {
        this(id, date);
    }
}
