package org.bongiorno.ariadne;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import junit.framework.TestCase;

import org.bongiorno.ariadne.implementations.JdbcKnowledgeBase;
import org.bongiorno.ariadne.implementations.xml.jaxb.XmlKnowledgeBase;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.operandowners.MinimalOperandOwner;
import org.bongiorno.ariadne.operandowners.NumericOperandOwner;
import org.bongiorno.ariadne.implementations.xml.jaxb.KnowledegeBaseEntry;
import org.bongiorno.ariadne.implementations.xml.jaxb.OperatorEntry;
import org.bongiorno.ariadne.interfaces.Equation;
import org.bongiorno.ariadne.interfaces.Predicate;

/**
 * @author chbo
 *         Date: Jul 2, 2008
 *         Time: 2:50:49 PM
 */
public class FuncTests extends TestCase {
    protected Connection connection = null;
    private static ByteArrayOutputStream baos = new ByteArrayOutputStream();

    // each one of these private TestStep classes allows us to have, more or less, indentical tests for XML
    // database or any other functional component by abstracting out certain maintenance portions crucial
    // to maintaining the underlying data store. Really, this is most crucial to the XML test for teardown.
    // However, it is also used for validation steps like in a DB test where it veri
    private static TestStep NO_OP = new TestStep() {
        public void perform() {

        }
    };
    private final TestStep RSET_STREAM = new TestStep() {
        public void perform() throws Exception {
            baos.reset();
        }
    };
    private final TestStep INIT_XML = new TestStep() {
        public void perform() throws Exception {
            baos.reset();
            setupTestXml();
        }
    };

    static {
        Properties properties = System.getProperties();

        properties.put("engines.jdbc", "com.org.bongiorno.ariadne.FuncTests$PropertiesFromMemoryKnowledgeBase");
        properties.put("engines.jdbc_fail", "com.org.bongiorno.ariadne.FuncTests$SharedConnectionFailJdbcKnowledgeBase");
        properties.put("engines.xml", "com.org.bongiorno.ariadne.FuncTests$InMemoryStreamKnowledgeBase");

        try {
            Class.forName("org.hsqldb.jdbcDriver");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }

    public FuncTests() throws Exception {
        connection = getConnection();
    }

    protected Connection getConnection() throws Exception {

        return DriverManager.getConnection("jdbc:hsqldb:mem:functest", "sa", "");
    }

    protected void setUp() throws Exception {
        execute("CREATE TABLE ARIADNE_OPERATORS (OPERATOR_ID integer PRIMARY KEY, " +
                "OPERATOR_CLASS_NAME varchar(256), DESCRIPTION varchar(256))");

        execute("CREATE TABLE ARIADNE_OPERAND_OWNERS (OPERAND_OWNER_ID integer PRIMARY KEY, " +
                "OPERAND_OWNER_CLASS_NAME char(256), OPERAND_OWNER_INPUT char(4000))");

        execute("CREATE TABLE ARIADNE_OPERATIONS (OPERATION_ID integer, " +
                "LH_OPERAND_OWNER_ID integer, OPERATOR_ID integer, " +
                "RH_OPERAND_OWNER_ID integer, DESCRIPTION char(4000)," +
                "CONSTRAINT OPER_PK PRIMARY KEY(OPERATION_ID)," +

                "CONSTRAINT LHO_FK FOREIGN KEY(LH_OPERAND_OWNER_ID) REFERENCES " +
                "ARIADNE_OPERAND_OWNERS(OPERAND_OWNER_ID)," +

                "CONSTRAINT RHO_FK FOREIGN KEY(RH_OPERAND_OWNER_ID) REFERENCES " +
                "ARIADNE_OPERAND_OWNERS(OPERAND_OWNER_ID)," +

                "CONSTRAINT OP_ID_FK FOREIGN KEY(OPERATOR_ID) REFERENCES " +
                "ARIADNE_OPERATORS(OPERATOR_ID))");

        insertOperator(4, "NotEqual", "!=");
        insertOperator(9, "Add", "+");
        insertOperator(12, "Divide", "/");
        insertOperator(3, "LessThanEqual", "<=");
        insertOperator(1, "Equal", "==");
        insertOperator(15, "Power", "pow");
        insertOperator(11, "Multiply", "*");
        insertOperator(14, "Max", "max");
        insertOperator(13, "Min", "min");
        insertOperator(8, "And", "&&");
        insertOperator(6, "GreaterThanEqual", ">=");
        insertOperator(2, "LessThan", "<");
        insertOperator(7, "Or", "||");
        insertOperator(5, "GreaterThan", ">");
        insertOperator(10, "Subtract", "-");
    }

    private void execute(String sql) throws SQLException {
        connection.createStatement().execute(sql);
    }


    private void insertOperator(int i, String fqcn, String description) throws SQLException {
        execute("Insert into ARIADNE_OPERATORS (OPERATOR_ID,OPERATOR_CLASS_NAME,DESCRIPTION) values ('" + i + "','" + fqcn + "','" + description + "');");
    }

    /**
     * After every test this method is called and clears out portions of the DB that could potentially conflict between
     * runs.
     */
    @Override
    protected void tearDown() throws Exception {
        execute("drop table ARIADNE_OPERATIONS;");
        execute("drop table ARIADNE_OPERAND_OWNERS;");
        execute("drop table ARIADNE_OPERATORS;");

    }

    /**
     * This test is to make sure that large description strings are correctly handled. The size can be configured
     *
     * @throws Exception if you don't change the test, it won't throw any exceptions
     */
    public void testLargeInsert() throws Exception {
        TestStep rowCount = new TestStep() {
            public void perform() throws Exception {
                validateData(15, 335, 337);
            }
        };
        doLargeInsert("jdbc", NO_OP, NO_OP, rowCount);
        doLargeInsert("xml", INIT_XML, RSET_STREAM, NO_OP);

    }

    public void testStorable() throws Exception {
        TestStep rowCount = new TestStep() {
            public void perform() throws Exception {
                validateData(15, 17, 29);
            }
        };
        doStore("jdbc", NO_OP, NO_OP, rowCount);
        doStore("xml", INIT_XML, RSET_STREAM, NO_OP);

    }

    public void testHugeStoreOperation() throws Exception {
        TestStep rowCount = new TestStep() {
            public void perform() throws Exception {
                validateData(15, 10498, 30496);
            }
        };
        doHugeStore("jdbc", NO_OP, NO_OP, rowCount);
        doHugeStore("xml", INIT_XML, RSET_STREAM, NO_OP);
    }

    /*
     * The connection used in this functional test is a different instance from the one used in the knowledgebase.
     * This must be remedied.
     *
     * This rollback doesn't work with HSQLDB but works just fine with Oracle.
     */

    public void testRollback() throws Exception {


        try {
            doStore("jdbc_fail", NO_OP, NO_OP, NO_OP);
            fail();
        }
        catch (AriadneException e) {
            validateData(15, 0, 0);

        }
    }

    public void testNullValueOperandOwner() throws Exception {
        TestStep rowCount = new TestStep() {
            public void perform() throws Exception {

                validateData(15, 3, 6);
            }
        };
        nullOerandOwnerInputStore("jdbc", NO_OP, NO_OP, rowCount);
        nullOerandOwnerInputStore("xml", INIT_XML, RSET_STREAM, NO_OP);
    }

    private void nullOerandOwnerInputStore(String kbSource, TestStep initStep, TestStep preStore, TestStep postStore) throws Exception {
        initStep.perform();
        KnowledgeBase kb = KnowledgeBase.getInstance(kbSource);
        kb.load();
        OperandOwner noo = kb.getOperandOwner(NullInputValueOperandOwner.class);
        OperandOwner nooo = kb.getOperandOwner(NullInputValueOperandOwner.class);

        OperandOwner ten = kb.getOperandOwner(NumericOperandOwner.class, 10.0d);
        OperandOwner eleven = kb.getOperandOwner(NumericOperandOwner.class, 11.0d);

        Equation<Double, Object> test = kb.getEquation(noo, "+", ten);
        kb.getEquation(ten, "+", eleven); // just to make sure null and non-null input work ok
        Double d = test.evaluate(null);
        assertEquals(d, 20d);

        test = kb.getEquation(nooo, "+", noo);
        d = test.evaluate(null);
        assertEquals(d, 20d);

        kb.validate();
        preStore.perform();
        kb.store();
        postStore.perform();

        KnowledgeBase kb2 = KnowledgeBase.getInstance(kbSource);
        kb2.load();

        assertEquals(kb, kb2);
    }

    private void doLargeInsert(String kbSource, TestStep init, TestStep preStore, TestStep postStore) throws Exception {
        init.perform();
        KnowledgeBase kb = KnowledgeBase.getInstance(kbSource);
        kb.load();
        kb.validate();
        Predicate p = kb.getPredicate(Boolean.TRUE, "||", Boolean.FALSE); // (true || False) 15 chars
        Predicate pPrime = p;
        for (int length = 0; length < 5000; length += 15)
            pPrime = kb.getPredicate(pPrime, "&&", p); // continue to build this tree until the string expression is huge!

        preStore.perform();
        kb.store();
        postStore.perform();
    }


    private void doStore(String kbSource, TestStep init, TestStep preStore, TestStep postStore) throws Exception {
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

    private void doHugeStore(String kbSource, TestStep init, TestStep preStore, TestStep postStore) throws Exception {
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


    private static void setupTestXml() throws Exception {

        String s = KnowledegeBaseEntry.class.getPackage().getName();

        JAXBContext jc = JAXBContext.newInstance(s);
        KnowledegeBaseEntry kbout = new KnowledegeBaseEntry();

        kbout.addOperatorEntry(new OperatorEntry(1, "Equal", "=="));
        kbout.addOperatorEntry(new OperatorEntry(2, "LessThan", "<"));
        kbout.addOperatorEntry(new OperatorEntry(3, "LessThanEqual", "<="));
        kbout.addOperatorEntry(new OperatorEntry(4, "NotEqual", "!="));
        kbout.addOperatorEntry(new OperatorEntry(5, "GreaterThan", ">"));
        kbout.addOperatorEntry(new OperatorEntry(6, "GreaterThanEqual", ">="));
        kbout.addOperatorEntry(new OperatorEntry(7, "Or", "||"));
        kbout.addOperatorEntry(new OperatorEntry(8, "And", "&&"));
        kbout.addOperatorEntry(new OperatorEntry(9, "Add", "+"));
        kbout.addOperatorEntry(new OperatorEntry(10, "Subtract", "-"));
        kbout.addOperatorEntry(new OperatorEntry(11, "Multiply", "*"));
        kbout.addOperatorEntry(new OperatorEntry(12, "Divide", "/"));
        kbout.addOperatorEntry(new OperatorEntry(13, "Min", "min"));
        kbout.addOperatorEntry(new OperatorEntry(14, "Max", "max"));
        kbout.addOperatorEntry(new OperatorEntry(15, "Power", "pow"));

        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(kbout, baos);
    }

    /**
     * This method allows us to verify the exact number of rows in each table. In the event that, by chance, the other
     * tests simply didn't stumble upon errored data, this method helps to catch changes that may have corrupted that data
     *
     * @param operatorCount     self explanatory
     * @param operationCount    self explanatory
     * @param operandOwnerCount self explanatory
     * @throws Exception if there was an SQLException
     */
    private void validateData(int operatorCount, int operationCount, int operandOwnerCount) throws Exception {
        assertEquals(operatorCount, getCount("ARIADNE_OPERATORS"));
        assertEquals(operationCount, getCount("ARIADNE_OPERATIONS"));
        assertEquals(operandOwnerCount, getCount("ARIADNE_OPERAND_OWNERS"));
    }

    /**
     * create a whole lot of random equations to populate our table to make sure sclability isn't a problem
     *
     * @param kb
     * @throws AriadneException
     */
    private void randomizeKnowledgeBase(KnowledgeBase kb) throws AriadneException {
        Random rand = new Random();

        String[] ops = {"+", "-", "/", "*", "min", "max", "pow"};
        for (Double i = 1d; i < 10000d; i++)
            kb.getEquation(Math.random(), ops[rand.nextInt(ops.length)], Math.random());


        int opsCount = kb.getOperations().size();
        for (int i = 1; i < 500; i++) {
            Equation l = kb.getEquation(rand.nextInt(opsCount));
            Equation r = kb.getEquation(rand.nextInt(opsCount));
            kb.getEquation(l, ops[rand.nextInt(ops.length)], r);
        }
    }

    private int getCount(String table) throws Exception {
        int retVal = -1;
        ResultSet rs = connection.createStatement().executeQuery("select count(*) from " + table);
        if (rs.next())
            retVal = rs.getInt(1);
        else
            throw new RuntimeException("No count returned from DB Query");
        return retVal;
    }

    public static class PropertiesFromMemoryKnowledgeBase extends JdbcKnowledgeBase {

        public PropertiesFromMemoryKnowledgeBase() throws AriadneException {
        }

        /**
         * Since this is a unit-like functional test we hard code these properties so that we can test DB functionality
         * without concern for an actual DB connection
         *
         * @return properties suitably contructed for use with JdbcKnowledgeBase
         * @throws AriadneException it actually throws nothing
         */
        @Override
        protected Properties getJdbcProperties() throws AriadneException {
            Properties retVal = new Properties();
            retVal.put("driver", "org.hsqldb.jdbcDriver");
            retVal.put("url", "jdbc:hsqldb:mem:functest");
            retVal.put("user", "sa");
            retVal.put("password", "");

            retVal.put("operations.table", "ARIADNE_OPERATIONS");
            retVal.put("operations.id", "OPERATION_ID");
            retVal.put("operations.lho", "LH_OPERAND_OWNER_ID");
            retVal.put("operations.op", "OPERATOR_ID");
            retVal.put("operations.rho", "RH_OPERAND_OWNER_ID");
            retVal.put("operations.desc", "DESCRIPTION");
            retVal.put("operations.desc.max.length", "4000");


            retVal.put("operand_owners.table", "ARIADNE_OPERAND_OWNERS");
            retVal.put("operand_owners.id", "OPERAND_OWNER_ID");
            retVal.put("operand_owners.class_name", "OPERAND_OWNER_CLASS_NAME");
            retVal.put("operand_owners.input", "OPERAND_OWNER_INPUT");


            retVal.put("operators.table", "ARIADNE_OPERATORS");
            retVal.put("operators.id", "OPERATOR_ID");
            retVal.put("operators.class_name", "OPERATOR_CLASS_NAME");
            retVal.put("operators.desc", "DESCRIPTION");
            return retVal;
        }
    }

    // this class exists because, for the rollback feature, we need to 1) share the connection between the test and
    // the knowledge base and 

    public static class FailOnCommitJdbcKnowledgeBase extends PropertiesFromMemoryKnowledgeBase {

        public FailOnCommitJdbcKnowledgeBase() throws AriadneException {
        }


        @Override
        protected Connection getConnection() throws AriadneException {
            return new FailureConnection(super.getConnection());
        }
    }

    public static class InMemoryStreamKnowledgeBase extends XmlKnowledgeBase {


        @Override
        public InputStream getXmlInputStream() throws IOException {
            return new ByteArrayInputStream(baos.toByteArray());
        }

        @Override
        public OutputStream getXmlOutputStream() throws IOException {
            return baos;
        }
    }

    private static interface TestStep {
        void perform() throws Exception;
    }

    public static class NullInputValueOperandOwner extends MinimalOperandOwner<Double, Object> {
        public NullInputValueOperandOwner(Integer id, KnowledgeBase kb) {
            super(id);
        }

        public Double getOperand(Object anyArg) throws AriadneException {
            return 10d;
        }

        public Class getOperandType() {
            return Double.class;
        }

        public Class getRuntimeType() {
            return Object.class;
        }

        @Override
        public String toString() {
            return "NullInput:10.0";

        }
    }


    // with this class I need to guaranteed failure on commit. So, this class is a delegate that implements
    // only the basic set of functions necessary to pass the test. 

    private static class FailureConnection implements Connection {

        private Connection delegate = null;

        private FailureConnection(Connection delegate) {
            this.delegate = delegate;
        }

        public void clearWarnings() throws SQLException {

        }

        public Statement createStatement() throws SQLException {
            return delegate.createStatement();
        }

        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return new FailOnExcutePreparedStatement(delegate.prepareStatement(sql));
        }

        public CallableStatement prepareCall(String sql) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public String nativeSQL(String sql) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public void setAutoCommit(boolean autoCommit) throws SQLException {

        }

        public boolean getAutoCommit() throws SQLException {
            return delegate.getAutoCommit();
        }

        public void commit() throws SQLException {
            throw new SQLException("This is a test class. Commit should fail");
        }

        public void rollback() throws SQLException {
            delegate.rollback();
        }

        public void close() throws SQLException {
            delegate.close();
        }

        public boolean isClosed() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }

        public DatabaseMetaData getMetaData() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public void setReadOnly(boolean readOnly) throws SQLException {

        }

        public boolean isReadOnly() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }

        public void setCatalog(String catalog) throws SQLException {

        }

        public String getCatalog() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public void setTransactionIsolation(int level) throws SQLException {

        }

        public int getTransactionIsolation() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public SQLWarning getWarnings() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public Map<String, Class<?>> getTypeMap() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

        }

        public void setHoldability(int holdability) throws SQLException {

        }

        public int getHoldability() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public Savepoint setSavepoint() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public Savepoint setSavepoint(String name) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public void rollback(Savepoint savepoint) throws SQLException {

        }

        public void releaseSavepoint(Savepoint savepoint) throws SQLException {

        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public Clob createClob() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public Blob createBlob() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public NClob createNClob() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public SQLXML createSQLXML() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public boolean isValid(int timeout) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }

        public void setClientInfo(String name, String value) throws SQLClientInfoException {

        }

        public void setClientInfo(Properties properties) throws SQLClientInfoException {

        }

        public String getClientInfo(String name) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public Properties getClientInfo() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public <T> T unwrap(Class<T> iface) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }
    }

    private static class FailOnExcutePreparedStatement implements PreparedStatement {
        private PreparedStatement delegate = null;

        private FailOnExcutePreparedStatement(PreparedStatement delegate) {
            this.delegate = delegate;
        }

        public void addBatch() throws SQLException {

        }

        public ResultSet executeQuery() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public int executeUpdate() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public void setNull(int parameterIndex, int sqlType) throws SQLException {
            delegate.setNull(parameterIndex, sqlType);
        }

        public void setBoolean(int parameterIndex, boolean x) throws SQLException {

        }

        public void setByte(int parameterIndex, byte x) throws SQLException {

        }

        public void setShort(int parameterIndex, short x) throws SQLException {

        }

        public void setInt(int parameterIndex, int x) throws SQLException {

        }

        public void setLong(int parameterIndex, long x) throws SQLException {

        }

        public void setFloat(int parameterIndex, float x) throws SQLException {

        }

        public void setDouble(int parameterIndex, double x) throws SQLException {

        }

        public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {

        }

        public void setString(int parameterIndex, String x) throws SQLException {

        }

        public void setBytes(int parameterIndex, byte[] x) throws SQLException {

        }

        public void setDate(int parameterIndex, Date x) throws SQLException {

        }

        public void setTime(int parameterIndex, Time x) throws SQLException {

        }

        public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {

        }

        public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {

        }

        public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {

        }

        public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {

        }

        public void clearParameters() throws SQLException {

        }

        public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {

        }

        public void setObject(int parameterIndex, Object x) throws SQLException {
            delegate.setObject(parameterIndex, x);
        }

        public boolean execute() throws SQLException {
            if (this != null)
                throw new SQLException("Test class meant to fail");
            return false;
        }

        public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {

        }

        public void setRef(int parameterIndex, Ref x) throws SQLException {

        }

        public void setBlob(int parameterIndex, Blob x) throws SQLException {

        }

        public void setClob(int parameterIndex, Clob x) throws SQLException {

        }

        public void setArray(int parameterIndex, Array x) throws SQLException {

        }

        public ResultSetMetaData getMetaData() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {

        }

        public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {

        }

        public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {

        }

        public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        }

        public void setURL(int parameterIndex, URL x) throws SQLException {

        }

        public ParameterMetaData getParameterMetaData() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public void setRowId(int parameterIndex, RowId x) throws SQLException {

        }

        public void setNString(int parameterIndex, String value) throws SQLException {

        }

        public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {

        }

        public void setNClob(int parameterIndex, NClob value) throws SQLException {

        }

        public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

        }

        public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

        }

        public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {

        }

        public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {

        }

        public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {

        }

        public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {

        }

        public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {

        }

        public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

        }

        public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {

        }

        public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {

        }

        public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

        }

        public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {

        }

        public void setClob(int parameterIndex, Reader reader) throws SQLException {

        }

        public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

        }

        public void setNClob(int parameterIndex, Reader reader) throws SQLException {

        }

        public ResultSet executeQuery(String sql) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public int executeUpdate(String sql) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public void close() throws SQLException {

        }

        public int getMaxFieldSize() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public void setMaxFieldSize(int max) throws SQLException {

        }

        public int getMaxRows() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public void setMaxRows(int max) throws SQLException {

        }

        public void setEscapeProcessing(boolean enable) throws SQLException {

        }

        public int getQueryTimeout() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public void setQueryTimeout(int seconds) throws SQLException {

        }

        public void cancel() throws SQLException {

        }

        public SQLWarning getWarnings() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public void clearWarnings() throws SQLException {

        }

        public void setCursorName(String name) throws SQLException {

        }

        public boolean execute(String sql) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }

        public ResultSet getResultSet() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public int getUpdateCount() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public boolean getMoreResults() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }

        public void setFetchDirection(int direction) throws SQLException {

        }

        public int getFetchDirection() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public void setFetchSize(int rows) throws SQLException {

        }

        public int getFetchSize() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public int getResultSetConcurrency() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public int getResultSetType() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public void addBatch(String sql) throws SQLException {

        }

        public void clearBatch() throws SQLException {

        }

        public int[] executeBatch() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return new int[0];
        }

        public Connection getConnection() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public boolean getMoreResults(int current) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }

        public ResultSet getGeneratedKeys() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public int executeUpdate(String sql, String[] columnNames) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }

        public boolean execute(String sql, int[] columnIndexes) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }

        public boolean execute(String sql, String[] columnNames) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }

        public int getResultSetHoldability() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return 0;
        }

        public boolean isClosed() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }

        public void setPoolable(boolean poolable) throws SQLException {

        }

        public boolean isPoolable() throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }

        public <T> T unwrap(Class<T> iface) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return null;
        }

        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            if (this != null) throw new UnsupportedOperationException("Method not yet implemented");
            return false;
        }
    }

}
