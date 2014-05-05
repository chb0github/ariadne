package org.bongiorno.ariadne.misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bongiorno.ariadne.AriadneException;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Aug 15, 2008
 * Time: 2:52:11 PM
 */

public class DBDataLoader {
    private String table;
    private Connection connection;


    private Set<String> selectColumns = new HashSet<String>(Arrays.asList("*"));

    public DBDataLoader() {
    }

    public DBDataLoader(Connection c, String table) {
        this.table = table;
        this.connection = c;
    }

    public DBDataLoader(Connection c, String table, Set<String> selectCols) {
        this(c, table);
        this.selectColumns = selectCols;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getTableName() {
        return table;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Set<String> getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(Set<String> selectColumns) {
        this.selectColumns = selectColumns;
    }

    public List<Map<String, Object>> load() throws AriadneException {
        List<Map<String, Object>> retVal = new ArrayList<Map<String, Object>>();
        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                ResultSet results = statement.executeQuery(getSelect());

                ResultSetMetaData meta = results.getMetaData();

                while (results.next()) {
                    Map<String, Object> columns = new LinkedHashMap<String, Object>();
                    for (int i = 1; i <= meta.getColumnCount(); i++)
                        columns.put(meta.getColumnName(i), results.getObject(i));

                    retVal.add(columns);
                }
            }
        }
        catch (SQLException e) {
            throw new AriadneException("error while fetching rows for table " + table + " " + e.toString(), e);
        }
        return retVal;
    }

    private String getSelect() {
        StringBuffer buff = new StringBuffer("select ");
        for (String selectCol : selectColumns)
            buff.append(selectCol).append(',');

        buff.setLength(buff.length() - 1);
        buff.append(" from ").append(getTableName());

        return buff.toString();
    }

    public String store(List<Map<String, Object>> entries) throws AriadneException {
        StringBuffer results = new StringBuffer();
        try {
            for (Map<String, Object> entry : entries) {
                PreparedStatement stmt = getStatement(entry);

                populateSatement(stmt, entry);
                stmt.execute();
                stmt.close();
            }

        }
        catch (SQLException e) {

            throw new AriadneException(e.toString(), e);
        }
        return results.toString();
    }

    private void populateSatement(PreparedStatement stmt, Map<String, Object> entries) throws SQLException {
        int i = 1;
        // saving data to the DB in a safe and generic fashion is exceptionally tricky.
        // by using prepared statements and letting the JDBC driver sort out the type from the object
        // we relieve ourselves of knowing the DB schema apriori. However, in this same light, some drivers
        // *ahem, org.bongiorno.Secure* don't seem to support this mechamism as robustly as others. For example:
        // Taking a value of Double d; and calling stmt.setObject(i,d) when that column is a VARCHAR
        // works with Oracle Thin, MySQL, and Hypersonic
        // but does not work with SOME other drivers (not to be mentioned).
        // Reversing the scenario:
        // Double d;  stmt.setObject(i,d.toString() )
        // works will all drivers (presumedly because it must handle 'insert... values('123') and interpret 123
        // as a number
        // unless of course we come to the case of null which again, is not correctly handle by *ahem* some drivers
        // in that case, calling setNull(i,Type.NULL) almost works, except that you get IvalidColumtype. So,
        // I have discovered that using almost any other type (such as Type.VARCHAR or Types.INT)
        //  magically does the right thing.
        // bottom line: There is a reason there are 3 times as many lines of comment as code. 
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            Object entryVal = entry.getValue();
            if(entryVal == null)
                stmt.setNull(i++,java.sql.Types.VARCHAR);
            else
                stmt.setObject(i++, entryVal.toString());
        }

    }

    private PreparedStatement getStatement(Map<String, Object> entries) throws SQLException {
        StringBuffer buff = new StringBuffer("INSERT INTO ").append(getTableName()).append(" ");
        StringBuffer cols = new StringBuffer("(");
        StringBuffer vals = new StringBuffer("VALUES (");
        for (Map.Entry entry : entries.entrySet()) {
            cols.append(entry.getKey()).append(',');
            vals.append('?').append(',');
        }
        cols.setCharAt(cols.length() - 1, ')');
        vals.setCharAt(vals.length() - 1, ')');
        buff.append(cols).append(" ").append(vals);
        return connection.prepareStatement(buff.toString());
    }
}
