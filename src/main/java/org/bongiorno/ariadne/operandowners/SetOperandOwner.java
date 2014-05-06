package org.bongiorno.ariadne.operandowners;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bongiorno.ariadne.KnowledgeBase;

/**
 *
 * @author chbo
 *
 * This class represents a constant series of elements in Set fashion: That is, there are no duplicates.
 * Only basic types are supported with their java Analogs (and also, their OperandOwner analogs). They are:
 * String
 * Date (Zulu time yyyy-MM-dd'T'HH:mm:ss'Z' -- no offset)
 * Boolean
 * Double
 */
public class SetOperandOwner<T,RUN_T> extends ConstantOperandOwner<Set<T>,RUN_T> {

    private static Map<String,SetTypeParser> PARSERS = new HashMap<String,SetTypeParser>();
    static {
        PARSERS.put("String",new StringTypeParser());
        PARSERS.put("Date",new DateTypeParser());
        PARSERS.put("Boolean",new BooleanTypeParser());
        PARSERS.put("Double",new DoubleTypeParser());
    }

    /**
     * Creates this object and delegates it's return type to num
     * @param data the value that will always be returned by { #getOperandOwner(Object)}
     * @param id the id of the operand owner
     */
    public SetOperandOwner(Integer id, Set<T> data) {
        super(id, data);
    }

    /**
     * Delegates to a date using the default locale format
     * @param engine not used
     * @param data the data as a string. It must be of the form: {Date|String|Double|Boolean}:v1,v2,...
     * @throws Exception if the string was formatted improperly
     * @param id the id of the operand owner
     */
    public SetOperandOwner(Integer id, KnowledgeBase engine,String data) throws Exception {
        this(id, parseData(data)); // the data type is intentionally fudged
    }

    public SetOperandOwner(Integer id, KnowledgeBase engine,Set<T> data) {
        this(id, data);
    }

    private static Set parseData(String data) throws Exception {
        String[] parts = data.split("\\|");
        if(parts.length != 2) {
            throw new IllegalArgumentException("Data for SetOperandOwner must be of the " +
                                               "form {Date|String|Double|Boolean}|v1,v2,... was " + data);
        }
        SetTypeParser p = PARSERS.get(parts[0]);
        if(p == null) {
            throw new IllegalArgumentException("Could not find a parser for type " + parts[0] +
                                               " supported types: " + PARSERS.keySet());
        }
        return p.parse(parts[1]);
    }

    private static interface SetTypeParser<T> {
        Set<T> parse(String data) throws Exception;
    }

    private static abstract class AbstractTypeParser<T> implements SetTypeParser<T>{
        public Set<T> parse(String data) throws Exception {

            String[] d = data.split(",");
            Set<T> retVal = new HashSet<T>();

            for (String s : d)
                retVal.add(convert(s));

            return retVal;

        }
        public abstract T convert(String s) throws Exception;
    }

    private static class StringTypeParser extends AbstractTypeParser<String> {
        public String convert(String s) {
            return s;
        }
    }

    private static class DateTypeParser extends AbstractTypeParser<Date> {
        public Date convert(String s) throws ParseException {
            DateFormat UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

            return UTC.parse(s);
        }
    }

    private static class BooleanTypeParser extends AbstractTypeParser<Boolean> {
        public Boolean convert(String s) throws Exception {
            return Boolean.valueOf(s);
        }
    }

    private static class DoubleTypeParser extends AbstractTypeParser<Double> {
        public Double convert(String s) throws Exception {
            return Double.valueOf(s);
        }
    }
}
