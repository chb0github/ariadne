package org.bongiorno.ariadne;

import org.bongiorno.ariadne.implementations.JdbcKnowledgeBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chbo
 *         Date: Jan 10, 2009
 *         Time: 1:27:24 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-context.xml"})
@Ignore
public class AbstractDBIntegrationTest extends AbstractIntegrationTest {

    @Value("${ariadne.operand_owners.table}")
    private String ownerTable;

    @Value("${ariadne.operators.table}")
    private String operatorsTable;

    @Value("${ariadne.operations.table}")
    private String operationsTable;


    @Autowired
    private DataSource dataSource;

    private Connection connection;


    @PostConstruct
    private void setupConnection() throws SQLException {
        connection = dataSource.getConnection();
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        empty(operationsTable);
        empty(ownerTable);
        empty(operatorsTable);
    }

    protected void drop(String table) throws SQLException {
        execute("drop table " + table + ";");
    }

    protected void empty(String table) throws SQLException {
        execute("delete from table " + table + ";");
    }


    protected void execute(String sql) throws SQLException {
        connection.createStatement().execute(sql);
    }


    static {
        Properties properties = System.getProperties();

        properties.put("engines.jdbc_fail", "com.org.bongiorno.ariadne.FuncTests$SharedConnectionFailJdbcKnowledgeBase");

    }

    /**
     * This test is to make sure that large description strings are correctly handled. The size can be configured
     *
     * @throws Exception if you don't change the test, it won't throw any exceptions
     */
    @Test
    public void testLargeInsert() throws Exception {
        TestStep rowCount = new TestStep() {
            public void perform() throws Exception {
                validateData(15, 335, 337);
            }
        };
        doLargeInsert("jdbc", NO_OP, NO_OP, rowCount);

    }

    @Test
    public void testStorable() throws Exception {
        TestStep rowCount = new TestStep() {
            public void perform() throws Exception {
                validateData(15, 17, 29);
            }
        };
        doStore("jdbc", NO_OP, NO_OP, rowCount);

    }

    @Test
    public void testHugeStoreOperation() throws Exception {
        TestStep rowCount = new TestStep() {
            public void perform() throws Exception {
                validateData(15, 10498, 30496);
            }
        };
        doHugeStore("jdbc", NO_OP, NO_OP, rowCount);
    }

    /*
     * The connection used in this functional test is a different instance from the one used in the knowledgebase.
     * This must be remedied.
     *
     * This rollback doesn't work with HSQLDB but works just fine with Oracle.
     */

    @Test
    public void testRollback() throws Exception {


        try {
            doStore("jdbc_fail", NO_OP, NO_OP, NO_OP);
            fail();
        } catch (AriadneException e) {
            validateData(15, 0, 0);

        }
    }

    @Test
    public void testNullValueOperandOwner() throws Exception {
        TestStep rowCount = new TestStep() {
            public void perform() throws Exception {

                validateData(15, 3, 6);
            }
        };
        nullOerandOwnerInputStore("jdbc", NO_OP, NO_OP, rowCount);
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

    private int getCount(String table) throws Exception {
        int retVal = -1;
        ResultSet rs = connection.createStatement().executeQuery("select count(*) from " + table);
        if (!rs.next())
            throw new RuntimeException("No count returned from DB Query");

        retVal = rs.getInt(1);
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

            retVal.put("ariadne.operations.table", "ARIADNE_OPERATIONS");
            retVal.put("ariadne.operations.id", "OPERATION_ID");
            retVal.put("ariadne.operations.lho", "LH_OPERAND_OWNER_ID");
            retVal.put("ariadne.operations.op", "OPERATOR_ID");
            retVal.put("ariadne.operations.rho", "RH_OPERAND_OWNER_ID");
            retVal.put("ariadne.operations.desc", "DESCRIPTION");
            retVal.put("ariadne.operations.desc.max.length", "4000");


            retVal.put("ariadne.operand_owners.table", "ARIADNE_OPERAND_OWNERS");
            retVal.put("ariadne.operand_owners.id", "OPERAND_OWNER_ID");
            retVal.put("ariadne.operand_owners.class_name", "OPERAND_OWNER_CLASS_NAME");
            retVal.put("ariadne.operand_owners.input", "OPERAND_OWNER_INPUT");


            retVal.put("ariadne.operators.table", "ARIADNE_OPERATORS");
            retVal.put("ariadne.operators.id", "OPERATOR_ID");
            retVal.put("ariadne.operators.class_name", "OPERATOR_CLASS_NAME");
            retVal.put("ariadne.operators.desc", "DESCRIPTION");
            return retVal;
        }
    }

}
