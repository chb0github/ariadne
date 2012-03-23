package org.bongiorno.ariadne;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.bongiorno.ariadne.implementations.JdbcKnowledgeBase;

/**
 * Created by IntelliJ IDEA.
 * @author chbo
 * Date: Jan 10, 2009
 * Time: 1:27:24 PM
 */
public class IntegrationTests extends FuncTests {
    public static final String DRIVER_KEY = "driver";
    public static final String CONNECT_KEY = "url";
    public static final String CONFIG_KEY = "jdbc.config";
    private static String owners_table;
    private static String opers_table;
    private static Properties props = new Properties();

    static {
        Properties properties = System.getProperties();
        // set it back to default. the super test has it's own agenda and we need to reverse it
        properties.put("engines.jdbc", JdbcKnowledgeBase.class.getName());


        try {
            props.loadFromXML(new FileInputStream(System.getProperty(CONFIG_KEY)));
            String driver = props.getProperty(DRIVER_KEY);
            Class.forName(driver);
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString(),e);
        }

        opers_table = props.getProperty("operations.table");
        owners_table = props.getProperty("operand_owners.table");
    }

    public IntegrationTests() throws Exception {
        connection = getConnection();
    }

    @Override
    protected Connection getConnection() throws Exception {

        String dbCon = props.getProperty(CONNECT_KEY);

        Connection c = DriverManager.getConnection(dbCon, props);
        c.setAutoCommit(false);
        return c;
    }

    @Override
    protected void setUp() throws Exception {
        // do nothing. Assume the DB is create and the structure is clean
        // at a minimum, there must be operators installed. 
    }

    @Override
    protected void tearDown() throws Exception {
        connection.createStatement().execute("delete from " + opers_table);
        connection.createStatement().execute("delete from " + owners_table);
        connection.commit();
    }
}
