package org.bongiorno.ariadne.implementations;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Operation;
import org.bongiorno.ariadne.misc.DBDataLoader;
import org.bongiorno.ariadne.operandowners.OperandOwnerFactory;
import org.bongiorno.ariadne.operations.OperationFactory;
import org.bongiorno.ariadne.operators.OperatorFactory;
import org.bongiorno.ariadne.interfaces.Operator;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Apr 14, 2008
 * Time: 5:30:03 PM
 * <p/>
 * This class provides an implementation for interfacing with the most basic DB form necessary to
 * make a KnowledgeBase work.
 * <p/>
 * There is a table for:
 * <p/>
 * Operations
 * OperandOwners
 * Operators
 * <p/>
 * Their names and the columns that represent the respective portions of each type are configurable
 * by supplying a System property: -Djdbc.config=(path to proprties.xml).
 * <p/>
 * For an example, look at jdbc_config.xml in the functional tests directory.
 * <p/>
 * The following needs to be defined in that config file
 * <p/>
 * <pre>
 * <properties>
 * <comment>Config for JDBC Knowledge Base</comment>
 * <entry key="driver">oracle.jdbc.driver.OracleDriver</entry>
 * <entry key="url">jdbc:oracle:thin:@grdco1-orasvr.db.org.bongiorno.com:58219:grdco1</entry>
 * <entry key="password">pass</entry>
 * <entry key="user">user</entry>
 * <p/>
 * <entry key="operations.table">LCE_OPERATIONS</entry>
 * <entry key="operations.id">OPERATION_ID</entry>
 * <entry key="operations.lho">LH_OPERAND_OWNER_ID</entry>
 * <entry key="operations.op">OPERATOR_ID</entry>
 * <entry key="operations.rho">RH_OPERAND_OWNER_ID</entry>
 * <entry key="operations.desc">DESCRIPTION</entry>
 * <p/>
 * <p/>
 * <entry key="operand_owners.table">LCE_OPERAND_OWNERS</entry>
 * <entry key="operand_owners.id">OPERAND_OWNER_ID</entry>
 * <entry key="operand_owners.class_name">OPERAND_OWNER_CLASS_NAME</entry>
 * <entry key="operand_owners.input">OPERAND_OWNER_INPUT</entry>
 * <p/>
 * <p/>
 * <entry key="operators.table">LCE_OPERATORS</entry>
 * <entry key="operators.id">OPERATOR_ID</entry>
 * <entry key="operators.class_name">OPERATOR_CLASS_NAME</entry>
 * <entry key="operators.desc">DESCRIPTION</entry>
 * <p/>
 * </properties>
 * </pre>
 */
public class JdbcKnowledgeBase extends KnowledgeBase {
    public static final String DRIVER_KEY = "driver";
    public static final String CONNECT_KEY = "url";
    public static final String CONFIG_KEY = "jdbc.config";
    private final Properties PROPS;

    private JdbcOperatorFactory opFact;
    private JdbcOperandOwnerFactory opOwnFact;
    private JdbcOperationFactory operationFactory;
    private Connection connection;


    public JdbcKnowledgeBase() throws AriadneException {

        PROPS = getJdbcProperties();
        connection = getConnection();
        opFact = new JdbcOperatorFactory();
        opOwnFact = new JdbcOperandOwnerFactory();
        operationFactory = new JdbcOperationFactory();

    }

    protected OperationFactory getOperationFactory() {
        return operationFactory;
    }

    protected OperandOwnerFactory getOperandOwnerFactory() {
        return opOwnFact;
    }

    protected OperatorFactory getOperatorFactory() {
        return opFact;
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        String newLine = System.getProperty("line.separator");

        buff.append(PROPS.getProperty(CONNECT_KEY)).append(newLine);
        buff.append(PROPS.getProperty(DRIVER_KEY)).append(newLine);

        buff.append(operationFactory).append(newLine);
        buff.append(opFact).append(newLine);
        buff.append(opOwnFact).append(newLine);

        for (Operation operation : operationFactory.getOperations())
            buff.append(operation.getId()).append(' ').append(operation).append(newLine);

        return buff.toString();
    }

    /**
     * Stores this knowledge base then closes the connection to the DB. Subclasses may override {#closeConnection()} if they
     * don't want the connection to be automagically closed. If method is atomic. If the store does not fully succeed,
     * a rollback is attempted.
     * <p/>
     * I have strong confidence that rollback works but, for reasons due to HSQLDB, I cannot verify it with a functional
     * test. It's one line of code after all.
     *
     * @throws AriadneException if there was a problem storing to the DB or there was a problem closing the connection
     */
    public void store() throws AriadneException {

        try {
            super.store();
            connection.commit();
        }
        catch (Exception e) {
            try {
                connection.rollback();
                if(e instanceof AriadneException)
                    throw (AriadneException)e;
                else
                    throw new AriadneException(e);
            }
            catch (SQLException e1) {
                throw new AriadneException("Data storage failed and so did rollback, high chance of data corruption. " +
                                           "The connection may not have been closed!");
            }
        }
        try {
            closeConnection();
        }
        catch (SQLException e) {
            throw new AriadneException(e);
        }
    }


    /**
     * Closes the DB connection recieved from {#getConnection()}
     * Subclasses may override if they don't want the connection closed when store is complete.
     *
     * @throws Exception if there was an error closing the DB connection.
     */
    protected void closeConnection() throws SQLException {
        connection.close();
    }

    /**
     * This allows subclasses to define the how and where they load properties from()
     *
     * @return A fully populated properties class. This must include everything specified for this clas
     *         but may also include extra parameters to be passed to the jdbc connection. They may be properties specific
     *         to the jdbc connection. NOTE: Auto commit is hardcoded to false to allow for rollback
     * @throws AriadneException if a subclass needs to throw it
     */
    protected Properties getJdbcProperties() throws AriadneException {
        Properties props = new Properties();
        try {
            props.loadFromXML(new FileInputStream(System.getProperty(CONFIG_KEY)));
        }
        catch (IOException e) {
            throw new AriadneException("Unable to load jdbc config properties", e);
        }
        return props;
    }

    /**
     * subclasses have a chance to override this method and provide a connection in anyway they like
     *
     * @return Checks the properties from {#getJdbcProperties()} for connection information and returns the connection
     *         made. Any extra properties are passed to the DriverManager when getting the connection. The Connection
     *         has auto commit set to true.
     * @throws org.bongiorno.ariadne.AriadneException
     *          if there was an underlying Connection error or the DB driver couldn't
     *          be loaded
     */
    protected Connection getConnection() throws AriadneException {
        Connection c = connection;
        if (c == null) {
            String dbCon = PROPS.getProperty(CONNECT_KEY);
            String driver = PROPS.getProperty(DRIVER_KEY);
            try {
                if (driver != null)
                    Class.forName(driver);
                else
                    throw new IllegalArgumentException("No driver found please configure the key: " + DRIVER_KEY);

                c = DriverManager.getConnection(dbCon, PROPS);
                c.setAutoCommit(false); // allow for rollback
            }
            catch (SQLException e) {
                throw new AriadneException(e.toString(), e);
            }
            catch (ClassNotFoundException e) {
                throw new AriadneException("Unable to load DB driver " + driver);
            }
        }
        return c;
    }

    private class JdbcOperatorFactory extends OperatorFactory {

        private final String ID = PROPS.getProperty("operators.id");
        private final String CLASS_NAME = PROPS.getProperty("operators.class_name");
        private final String DESCR = PROPS.getProperty("operators.desc");
        private final String TABLE = PROPS.getProperty("operators.table");
        private DBDataLoader dbtool = new DBDataLoader(connection, TABLE);

        private Set<Operator> postLoadOperators = new HashSet<Operator>();
        private Observer opObs = new CommonObserver<Operator>(postLoadOperators);

        public void load() throws AriadneException {

            List<Map<String, Object>> records = dbtool.load();

            for (Map<String, Object> record : records)
                addOperator(((Number) record.get(ID)).intValue(), (String) record.get(CLASS_NAME));

            addObserver(opObs);
        }


        public void store() throws AriadneException {
            List<Map<String, Object>> output = new LinkedList<Map<String, Object>>();

            for (Operator operator : postLoadOperators) {
                Map<String, Object> row = new LinkedHashMap<String, Object>();
                row.put(ID, operator.getId());
                row.put(CLASS_NAME, operator.getClass().getName());
                row.put(DESCR, operator.toString());
                output.add(row);
            }
            dbtool.store(output);
        }

        @Override
        public String toString() {
            return "Operators: " + TABLE;
        }
    }

    private class JdbcOperandOwnerFactory extends OperandOwnerFactory {
        private final String TABLE = PROPS.getProperty("operand_owners.table");

        private final String OO_CLASS_NAME = PROPS.getProperty("operand_owners.class_name");
        private final String INPUT = PROPS.getProperty("operand_owners.input");
        private final String OWNER_ID = PROPS.getProperty("operand_owners.id");
        private DBDataLoader dbtool = new DBDataLoader(connection, TABLE);

        private Set<OperandOwner> postLoadOpOwners = new HashSet<OperandOwner>();
        private Observer opOwnObs = new CommonObserver<OperandOwner>(postLoadOpOwners);

        public JdbcOperandOwnerFactory() {
            super(JdbcKnowledgeBase.this);
        }

        /**
         * The assumption behind this method is that load will only ever be called once
         *
         * @throws AriadneException
         */
        public void load() throws AriadneException {

            List<Map<String, Object>> rs = dbtool.load();

            for (Map<String, Object> record : rs) {

                String fqcn = (String) record.get(OO_CLASS_NAME);
                Object val = record.get(INPUT);
                Integer key = ((Number) record.get(OWNER_ID)).intValue();
                instantiate(key, fqcn, val);
            }


            addObserver(opOwnObs);
        }


        public void store() throws AriadneException {
            List<Map<String, Object>> output = new LinkedList<Map<String, Object>>();
            for (OperandOwner oo : postLoadOpOwners) {
                Map<String, Object> row = new LinkedHashMap<String, Object>();
                row.put(OWNER_ID, oo.getId());
                row.put(OO_CLASS_NAME, oo.getClass().getName());
                row.put(INPUT, oo.getInput());
                output.add(row);
            }
            dbtool.store(output);
        }

        @Override
        public String toString() {
            return "OperandOwners: " + TABLE;
        }
    }

    private class JdbcOperationFactory extends OperationFactory {
        private final String TABLE = PROPS.getProperty("operations.table");

        private final String PID = PROPS.getProperty("operations.id");
        private final String LHO_ID = PROPS.getProperty("operations.lho");
        private final String OP_ID = PROPS.getProperty("operations.op");
        private final String RHO_ID = PROPS.getProperty("operations.rho");
        private final String DESC = PROPS.getProperty("operations.desc");
        private final Integer MAX_DESC_LEN;
        private DBDataLoader dbtool =
                new DBDataLoader(connection, TABLE, new HashSet<String>(Arrays.asList(PID, LHO_ID, OP_ID, RHO_ID)));

        private Set<Operation> postLoadOperations = new HashSet<Operation>();
        private Observer operObserver = new CommonObserver<Operation>(postLoadOperations);

        public JdbcOperationFactory() {
            super(JdbcKnowledgeBase.this);
            String temp = PROPS.getProperty("operations.desc.max.length");
            MAX_DESC_LEN = (temp != null ? new Integer(temp) : 256);
        }

        public void load() throws AriadneException {
            List<Map<String, Object>> rs = dbtool.load();
            for (Map<String, Object> record : rs) {

                Number lhoId = (Number) record.get(LHO_ID);
                Number opId = (Number) record.get(OP_ID);
                Number rhoId = (Number) record.get(RHO_ID);
                Number id = ((Number) record.get(PID));

                getOperation(id, lhoId, opId, rhoId);
            }
            addObserver(operObserver);
        }

        public void store() throws AriadneException {
            List<Map<String, Object>> output = new LinkedList<Map<String, Object>>();
            validate();
            for (Operation operation : postLoadOperations) {
                Map<String, Object> record = new LinkedHashMap<String, Object>();
                record.put(PID, operation.getId());

                record.put(LHO_ID, operation.getLho().getId());
                record.put(OP_ID, operation.getOperator().getId());
                record.put(RHO_ID, operation.getRho().getId());

                if (DESC != null) {// optional field
                    String descr = operation.toString();
                    // we must bind the description field to a user defined length
                    // to prevent overflow -- especially since this is a
                    // non-required field and only meant for debug
                    descr = descr.substring(0, Math.min(descr.length(), MAX_DESC_LEN));
                    record.put(DESC, descr);
                }
                output.add(record);
            }
            dbtool.store(output);
        }

        @Override
        public String toString() {
            return "Operations: " + TABLE;
        }
    }

    /**
     * Observes for them all and then adds to the set you passed in
     *
     * @param <T>
     */
    private static class CommonObserver<T> implements Observer {
        private Set<T> newStuff = null;

        private CommonObserver(Set<T> newStuff) {
            this.newStuff = newStuff;
        }

        public void update(Observable o, Object arg) {
            newStuff.add((T) arg);
        }
    }

}

