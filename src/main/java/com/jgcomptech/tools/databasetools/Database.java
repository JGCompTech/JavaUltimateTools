package com.jgcomptech.tools.databasetools;

import com.jgcomptech.tools.dialogs.DialogResult;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxButtons;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;

import java.sql.*;

/**
 * Database object that allows communication with a SQL database
 */
public class Database implements AutoCloseable {
    private Connection conn = null;
    private String connString = "";
    final private String username;
    final private String password;
    final private String dbName;
    final private DatabaseType dbType;

    /**
     * Creates a database object with the specified parameters
     * @param dbFilePath File path to the database to connect to
     * @param username Username to use to connect to the database, ignored if blank
     * @param password Password to use to connect to the database, ignored if blank
     * @param type The database type
     */
    public Database(String dbFilePath, String username, String password, DatabaseType type) {
        dbType = type;
        switch(dbType) {

            case H2:
                connString = "jdbc:h2:" + dbFilePath;
                break;
            case SQLite:
                connString = "jdbc:sqlite:" + dbFilePath;
                break;
        }

        this.username = username;
        this.password = password;

        dbName = connString.substring(connString.lastIndexOf("/") + 1);
    }

    /**
     * Creates a database object with the specified parameters
     * @param dbFilePath File path to the database to connect to
     * @param type The database type
     */
    public Database(String dbFilePath, DatabaseType type) {
        this(dbFilePath, "", "", type);
    }

    /**
     * Creates a database object with the specified parameters
     * @param info An object containing the database path, username, password and database type
     */
    public Database(DatabaseConnectionInfo info) {
        this(info.Path, info.Username, info.Password, info.DBType);
    }

    public Connection getConnection() { return conn; }

    /**
     * Returns the name of the database
     * @return the name of the database
     */
    public String getName() { return dbName; }

    /**
     * Returns the DatabaseType of the database
     * @return the DatabaseType of the database
     */
    public DatabaseType getDbType() { return dbType; }

    /**
     * Connects to the database and shows a message box if the connection succeeds
     * @throws SQLException if error occurs
     */
    public void connect() throws SQLException { connect(false); }

    /**
     * Connects to the database and if parameter is true, shows a message box if the connection succeeds
     * @param showStatusAlert Specifies if message box should be shown
     * @throws SQLException if error occurs
     */
    public void connect(boolean showStatusAlert) throws SQLException{
        try {
            conn = DriverManager.getConnection(connString, username, password);

            if(showStatusAlert) MessageBox.show("Connection to database has been established.", "Database Alert",
                    "Database Alert", MessageBoxIcon.INFORMATION);
        } catch (SQLException e) {
            if(showStatusAlert) {
                if(e.getMessage().contains("Database may be already in use")) {
                    DialogResult result = MessageBox.show("\"" + dbName + "\" is currently in use!\nPlease Close any open connections!",
                            "Error!", "Database Error", MessageBoxButtons.RetryCancel, MessageBoxIcon.ERROR);
                    if(result.equals(DialogResult.RETRY)) connect(showStatusAlert);
                    if(result.equals(DialogResult.CANCEL)) throw e;
                }
                else throw e;
            }
            else throw e;
        }
    }

    /**
     * Releases this database and JDBC resources immediately
     * instead of waiting for this to happen when it is automatically closed,
     * It is generally good practice to release resources as soon as
     * you are finished with them to avoid tying up database
     * resources, shows message box on error
     * @throws SQLException if error occurs
     */
    public void disconnect() throws SQLException {
        if(conn != null) if(!conn.isClosed()) conn.close();
    }

    /**
     * Executes the given SQL statement, which returns a single
     * <code>ResultSet</code> object, auto-closes statement object when ResultSet object is closed
     * @param sql an SQL statement to be sent to the database, typically a
     *        static SQL <code>SELECT</code> statement
     * @return a <code>ResultSet</code> object that contains the data produced
     *         by the given query; never <code>null</code>
     * @throws SQLException if a database access error occurs or the given
     * SQL statement produces anything other than a single
     * <code>ResultSet</code> object
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        Statement stmt;

        if(dbType == DatabaseType.SQLite) {
            stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        } else {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        }
        stmt.closeOnCompletion();
        return stmt.executeQuery(sql);
    }

    /**
     * Executes the given SQL statement, which may be an <code>INSERT</code>,
     * <code>UPDATE</code>, or <code>DELETE</code> statement or an
     * SQL statement that returns nothing, such as an SQL DDL statement
     * @param sql an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code>; or an SQL statement that returns nothing,
     * such as a DDL statement.
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     *         or (2) 0 for SQL statements that return nothing
     * @throws SQLException if a database access error occurs or the given
     * SQL statement produces a <code>ResultSet</code> object
     */
    public int executeUpdate(String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.closeOnCompletion();
        return stmt.executeUpdate(sql);
    }

    /**
     * Executes the given SQL statement, which may return multiple results.
     * In some (uncommon) situations, a single SQL statement may return
     * multiple result sets and/or update counts.  Normally you can ignore
     * this unless you are (1) executing a stored procedure that you know may
     * return multiple results or (2) you are dynamically executing an
     * unknown SQL string.
     * <P>
     * The <code>execute</code> method executes an SQL statement and indicates the
     * form of the first result.  You must then use the methods
     * <code>getResultSet</code> or <code>getUpdateCount</code>
     * to retrieve the result, and <code>getMoreResults</code> to
     * move to any subsequent result(s).
     * <p>
     * @param sql any SQL statement
     * @return <code>true</code> if the first result is a <code>ResultSet</code>
     *         object; <code>false</code> if it is an update count or there are
     *         no results
     * @throws SQLException if a database access error occurs
     */
    public boolean execute(String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.closeOnCompletion();
        return stmt.execute(sql);
    }

    /**
     * Checks to see if a table exists and if not, creates it using the specified sql code
     * @param tableName table name to be created
     * @param query sql query to be used to create the table
     * @param suppressExistsError if true suppresses error if the table already exists
     * @return true if created successfully
     * @throws SQLException if error occurs
     */
    public boolean createTable(String tableName, String query, boolean suppressExistsError) throws SQLException {
        if(!TableExists(tableName)) {
            try (Statement stmt = conn.createStatement()) {
                // create a new table
                stmt.execute(query);
                return true;
            }
        } else {
            if(!suppressExistsError) MessageBox.show("\"" + tableName + "\" Table Already Exists!", "Error!", "Database Error", MessageBoxIcon.ERROR);
            return false;
        }
    }

    /**
     * Checks to see if a table index exists and if not, creates it using the specified sql code
     * @param indexName index name to be created
     * @param query sql query to be used to create the table index
     * @param suppressExistsError if true suppresses error if the table index already exists
     * @return true if created successfully
     * @throws SQLException if error occurs
     */
    public boolean createIndex(String indexName, String query, boolean suppressExistsError) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(query);
            return true;
        } catch(SQLException e) {
            if(e.getMessage().contains(indexName) &&  e.getMessage().contains("already exists")) {
                if(!suppressExistsError) MessageBox.show("\"" + indexName + "\" Index Already Exists!", "Error!", "Database Error", MessageBoxIcon.ERROR);
            } else {
                throw e;
            }
            return false;
        }
    }

    /**
     * Returns number of rows that were returned in a ResultSet object
     * @param res the ResultSet object to count
     * @return number of rows in specified ResultSet
     */
    public static int getResultRows(ResultSet res){
        int totalRows = 0;
        try {
            while(res.next())
            {
                totalRows++;
            }
        }
        catch(Exception ex)  {
            return 0;
        }
        return totalRows ;
    }

    /**
     * Checks if a connection is open to the database
     * @return true if connected
     * @throws SQLException if error occurs
     */
    public boolean isConnected() throws SQLException {
        return !(conn == null && conn.isClosed());
    }

    /**
     * Checks to see if the specified table exists and is not a system table
     * @param tableName table name to check
     * @return true if exists
     * @throws SQLException if error occurs
     */
    public boolean TableExists(String tableName) throws SQLException {
        boolean tExists = false;
        if(dbType == DatabaseType.H2) tableName = tableName.toUpperCase();
        final String[] TYPES = {"TABLE"};
        try (ResultSet rs = conn.getMetaData().getTables(null, null, null, TYPES)) {
            while (rs.next()) {
                String tName = rs.getString("TABLE_NAME");
                if (tName != null && tName.equalsIgnoreCase(tableName)) {
                    tExists = true;
                    break;
                }
            }
        }
        return tExists;
    }

    /**
     * Releases this database and JDBC resources immediately
     * instead of waiting for this to happen when it is automatically closed,
     * It is generally good practice to release resources as soon as
     * you are finished with them to avoid tying up database
     * resources
     */
    @Override
    public void close() throws Exception {
        if(conn != null && !conn.isClosed()) conn.close();
    }
}