package com.jgcomptech.tools.databasetools.jbdc;

import com.jgcomptech.tools.dialogs.DialogResult;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxButtons;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Database object that allows communication with a SQL database.
 * @since 1.3.0
 */
public final class Database implements AutoCloseable {
    private String connString;
    private final String username;
    private final String password;
    private final String dbName;
    private final DatabaseType dbType;
    private String dbDriver;
    private Info info;
    private Connection connection;
    private Tasks tasks;
    private Settings settings;

    /**
     * Creates a database object with the specified parameters.
     * @param dbFilePath File path to the database to connect to
     * @param username Username to use to connect to the database, ignored if blank
     * @param password Password to use to connect to the database, ignored if blank
     * @param type The database type
     * @throws SQLException if initial connection fails
     * @since 1.4.0, constructor connects to database by running getConnection().connect().
     */
    public Database(final String dbFilePath, final String username,
                    final String password, final DatabaseType type) throws SQLException {
        if(dbFilePath == null || dbFilePath.isEmpty()) {
            throw new IllegalArgumentException("DB File Path Cannot Be Empty!");
        }
        dbType = type;
        switch(dbType) {
            case H2:
                connString = "jdbc:h2:" + dbFilePath;
                dbDriver = "org.h2.Driver";
                break;
            case HyperSQLDB:
                connString = "jdbc:hsqldb:" + dbFilePath;
                dbDriver = "org.hsqldb.jdbc.JDBCDriver";
                break;
            case SQLite:
                connString = "jdbc:sqlite:" + dbFilePath;
                dbDriver = "org.sqlite.JDBC";
                break;
        }

        this.username = username;
        this.password = password;

        dbName = connString.substring(connString.lastIndexOf("/") + 1);

        getConnection().connect();
    }

    /**
     * Creates a database object with the specified parameters.
     * @param dbFilePath File path to the database to connect to
     * @param type The database type
     * @throws SQLException if initial connection fails
     */
    public Database(final String dbFilePath, final DatabaseType type) throws SQLException {
        this(dbFilePath, "", "", type); }

    /**
     * Creates a database object with the specified parameters.
     * @param info An object containing the database path, username, password and database type
     * @throws SQLException if initial connection fails
     */
    public Database(final DatabaseConnectionInfo info) throws SQLException {
        this(info.getPath(), info.getUsername(), info.getPassword(), info.getDBType());
    }

    /**
     * Allows management of the connection to the database.
     * @return instance of the Connection class
     */
    public Connection getConnection() {
        if(connection == null) connection = new Connection();
        return connection;
    }

    /** The object that stores tasks related to the connection, use {@link #getConnection} to access methods. */
    public final class Connection {
        private java.sql.Connection conn;
        /**
         * Returns the raw {@code java.sql.Connection} object.
         * @return the Connection object
         */
        public java.sql.Connection getObject() { return conn; }

        /**
         * Connects to the database and shows a retry dialog message box if the connection fails.
         * @throws SQLException if error occurs
         */
        public void connect() throws SQLException { connect(false, true); }

        /**
         * Connects to the database and if parameter is true, shows a message box if the connection succeeds.
         * @param showStatusAlert Specifies if message box should be shown if connection succeeds
         * @param showRetryDialog Specifies if a retry dialog should be shown if the database is in use
         * @throws SQLException if error occurs
         */
        public void connect(final boolean showStatusAlert, final boolean showRetryDialog) throws SQLException {
            try(BasicDataSource ds = new BasicDataSource()) {
                ds.setDriverClassName(dbDriver);
                ds.setUrl(connString);
                ds.setUsername(username);
                ds.setPassword(password);

                conn = ds.getConnection();

                if(showStatusAlert) MessageBox.show("Connection to database has been established.",
                        "Database Alert", "Database Alert", MessageBoxIcon.INFORMATION);
            } catch (SQLException e) {
                if(e.getMessage().contains("Database may be already in use") && showRetryDialog) {
                    final DialogResult result = MessageBox.show('"' + dbName + "\" is currently in use!"
                                    + System.lineSeparator() + "Please Close any open connections!",
                            "Error!", "Database Error",
                            MessageBoxButtons.RetryCancel, MessageBoxIcon.ERROR);
                    if(result.equals(DialogResult.RETRY)) connect(showStatusAlert, true);
                    if(result.equals(DialogResult.CANCEL)) throw e;
                }
                else throw e;
            }
        }

        /**
         * Releases this database and JDBC resources immediately
         * instead of waiting for this to happen when it is automatically closed,
         * It is generally good practice to release resources as soon as
         * you are finished with them to avoid tying up database
         * resources, shows message box on error.
         * @throws SQLException if error occurs
         */
        public void disconnect() throws SQLException { if(isConnected()) conn.close(); }

        /**
         * Checks if a connection is open to the database.
         * @return true if connected
         * @throws SQLException if error occurs
         */
        public boolean isConnected() throws SQLException { return (conn != null && !conn.isClosed()); }
    }

    /**
     * Returns info about the database.
     * @return instance of the Info class
     */
    public Info getInfo() {
        if(info == null) info = new Info();
        return info;
    }

    /** The object that stores the database info, use {@link #getInfo} to access methods. */
    public final class Info {

        /**
         * Returns the name of the database.
         * @return the name of the database
         */
        public String getName() { return dbName; }

        /**
         * Returns the DatabaseType of the database.
         * @return the DatabaseType of the database
         */
        public DatabaseType getDbType() { return dbType; }

        /**
         * Returns the class name of the database driver.
         * @return the class name of the database driver
         */
        public String getDBDriverClass() { return dbDriver; }

        /**
         * Checks to see if the specified table exists and is not a system table.
         * @param tableName table name to check
         * @return true if exists
         * @throws SQLException if error occurs
         * @deprecated This method was renamed to follow naming conventions.
         * As of release 1.4.0, replaced by {@link #tableExists}, will be removed in next version.
         */
        @Deprecated()
        public boolean TableExists(final String tableName) throws SQLException { return tableExists(tableName); }

        /**
         * Checks to see if the specified table exists and is not a system table.
         * @param tableName table name to check
         * @return true if exists
         * @throws SQLException if error occurs
         */
        public boolean tableExists(final String tableName) throws SQLException {
            boolean tExists = false;

            final String[] types = {"TABLE", "Table", "table"};
            final DatabaseMetaData md = connection.getObject().getMetaData();
            try (ResultSet rs = md.getTables(null, null, null, types)) {
                while (rs.next()) {
                    final String tName = rs.getString("TABLE_NAME");
                    if (tName != null && tName.equalsIgnoreCase(tableName)) {
                        tExists = true;
                        break;
                    }
                }
            }
            return tExists;
        }

        /**
         * Returns an ArrayList of all tables in the database.
         * @return ArrayList of tables
         * @throws SQLException if error occurs
         */
        public ArrayList getTablesList() throws SQLException {
            final ArrayList<String> listOfTables = new ArrayList<>();

            final String[] types = {"TABLE", "Table", "table"};
            final DatabaseMetaData md = connection.getObject().getMetaData();
            try (ResultSet rs = md.getTables(null, null, null, types)) {
                while (rs.next()) {
                    listOfTables.add(rs.getString("TABLE_NAME"));
                }
            }
            return listOfTables;
        }
    }

    /**
     * Runs tasks against the database.
     * @return instance of the Tasks class
     */
    public Tasks getTasks() {
        if(tasks == null) tasks = new Tasks();
        return tasks;
    }

    /** The object that stores methods to complete database tasks, use {@link #getTasks} to access methods. */
    public final class Tasks {
        /**
         * Executes the given SQL statement, which returns a single
         * {@code ResultSet} object, auto-closes statement object when ResultSet object is closed.
         * @param sql an SQL statement to be sent to the database, typically a
         *        static SQL {@code SELECT} statement
         * @return a {@code ResultSet} object that contains the data produced
         *         by the given query; never {@code null}
         * @throws SQLException if a database access error occurs or the given
         * SQL statement produces anything other than a single
         * {@code ResultSet} object
         */
        public ResultSet executeQuery(final String sql) throws SQLException {
            try(PreparedStatement stmt = generatePreparedStatement(sql)) {
                stmt.closeOnCompletion();
                return stmt.executeQuery();
            }
        }

        /**
         * Executes the given SQL statement, which may be an {@code INSERT},
         * {@code UPDATE}, or {@code DELETE} statement or an
         * SQL statement that returns nothing, such as an SQL DDL statement.
         * @param sql an SQL Data Manipulation Language (DML) statement, such as {@code INSERT}, {@code UPDATE} or
         * {@code DELETE}; or an SQL statement that returns nothing,
         * such as a DDL statement.
         * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
         *         or (2) 0 for SQL statements that return nothing
         * @throws SQLException if a database access error occurs or the given
         * SQL statement produces a {@code ResultSet} object
         */
        public int executeUpdate(final String sql) throws SQLException {
            try(PreparedStatement stmt = generatePreparedStatement(sql)) {
                stmt.closeOnCompletion();
                return stmt.executeUpdate();
            }
        }

        /**
         * Executes the given SQL statement, which may return multiple results.
         * In some (uncommon) situations, a single SQL statement may return
         * multiple result sets and/or update counts.  Normally you can ignore
         * this unless you are (1) executing a stored procedure that you know may
         * return multiple results or (2) you are dynamically executing an
         * unknown SQL string.
         * <P>
         * The {@code execute} method executes an SQL statement and indicates the
         * form of the first result.  You must then use the methods
         * {@code getResultSet} or {@code getUpdateCount}
         * to retrieve the result, and {@code getMoreResults} to
         * move to any subsequent result(s).
         * <p>
         * @param sql any SQL statement
         * @return {@code true} if the first result is a {@code ResultSet}
         *         object; {@code false} if it is an update count or there are
         *         no results
         * @throws SQLException if a database access error occurs
         */
        public boolean execute(final String sql) throws SQLException {
            try(PreparedStatement stmt = generatePreparedStatement(sql)) {
                return stmt.execute();
            }
        }

        /**
         * Creates a {@code PreparedStatement} object that will generate
         * {@code ResultSet} objects for sending SQL statements to the database.
         * @param sql a {@code String} object that is the SQL statement to be sent to the database
         * @return a new {@code PreparedStatement} object containing the pre-compiled SQL statement
         * @throws SQLException if a database access error occurs
         */
        private PreparedStatement generatePreparedStatement(final String sql) throws SQLException {
            if(sql.isEmpty()) { throw new IllegalArgumentException("SQL String Not Set!"); }
            int resultSetType = getInfo().getDbType()
                    == DatabaseType.SQLite
                    ? ResultSet.TYPE_FORWARD_ONLY
                    : ResultSet.TYPE_SCROLL_INSENSITIVE;
            String sqlText = sql.trim() + ';';
            PreparedStatement stmt =
                        connection.getObject().prepareStatement(sqlText, resultSetType, ResultSet.CONCUR_READ_ONLY);
            stmt.closeOnCompletion();
            return stmt;
        }

        /**
         * Checks to see if a table exists and if not, creates it using the specified sql code.
         * @param tableName table name to be created
         * @param query sql query to be used to createUser the table
         * @param suppressExistsError if true suppresses error if the table already exists
         * @return true if created successfully
         * @throws SQLException if a database access error occurs
         */
        public boolean createTable(final String tableName, final String query, final boolean suppressExistsError)
                throws SQLException {
            if(getInfo().tableExists(tableName)) {
                if(!suppressExistsError) throw new SQLException('"' + tableName + "\" Table Already Exists!");
                return false;
            } else {
                try(Statement stmt = connection.getObject().createStatement()) {
                    // createUser a new table
                    stmt.execute(query);
                    return true;
                }
            }
        }

        /**
         * Checks to see if a table index exists and if not, creates it using the specified sql code.
         * @param indexName index name to be created
         * @param query sql query to be used to createUser the table index
         * @param suppressExistsError if true suppresses error if the table index already exists
         * @return true if created successfully
         * @throws SQLException if a database access error occurs
         */
        public boolean createIndex(final String indexName, final String query, final boolean suppressExistsError)
                throws SQLException {
            try (Statement stmt = connection.getObject().createStatement()) {
                // createUser a new table
                stmt.execute(query);
                return true;
            } catch(SQLException e) {
                if(e.getMessage().contains(indexName)
                        &&  e.getMessage().toLowerCase(Locale.ENGLISH).contains("already exists")) {
                    if(!suppressExistsError) throw new SQLException('"' + indexName + "\" Index Already Exists!");
                } else throw e;
                return false;
            }
        }

        /**
         * Returns number of rows that were returned in a ResultSet object.
         * @param rs the ResultSet object to count
         * @return number of rows in specified ResultSet
         */
        public int getResultRows(final ResultSet rs) {
            int totalRows = 0;
            try { while(rs.next()) totalRows++; }
            catch(SQLException ex) { return 0; }
            return totalRows;
        }
    }

    /**
     * Allows setting and retrieval of settings values from the database.
     * @return instance of the Settings class
     */
    public Settings getSettings() {
        if(settings == null) settings = new Settings(this);
        return settings;
    }

    /** Allows setting and retrieval of settings values from a database, use {@link #getSettings} to access methods. */
    public static final class Settings {
        private final Database db;
        private static final String TABLE_NAME = "Settings";
        private static final String ID_FIELD = "Id";
        private static final String NAME_FIELD = "Name";
        private static final String VALUE_FIELD = "Value";
        private Settings(final Database db) { this.db = db; }

        /**
         * Creates a Settings table in the specified database.
         * @return false if the table already exists
         * @throws SQLException if error occurs during table creation
         */
        public boolean createTable() throws SQLException {
            return !db.getInfo().tableExists(TABLE_NAME)
                    && new TableBuilder().CREATE(TABLE_NAME, db)
                    .addColumn(new ColumnBuilder(ID_FIELD, DataTypes.INTEGER).notNull().primaryKey().autoIncrement())
                    .addColumn(new ColumnBuilder(NAME_FIELD, DataTypes.NVARCHAR, 100).notNull())
                    .addColumn(new ColumnBuilder(VALUE_FIELD, DataTypes.NVARCHAR, 100).notNull())
                    .buildAndCreate();
        }

        /**
         * Returns a setting from the settings table, if setting doesn't exist, returns an empty string.
         * @param settingName name of the setting to return a value
         * @return value of the specified setting
         * @throws SQLException if error occurs during lookup
         */
        public String getValue(final String settingName) throws SQLException {
            final String newSettingName = settingName.toLowerCase();

            String value = "";

            if(exists(newSettingName)) {
                TypedStatement statement = new QueryBuilder().SELECT(VALUE_FIELD).FROM(TABLE_NAME)
                        .WHERE(NAME_FIELD, newSettingName).build(db);

                try(ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    value = rs.getString(VALUE_FIELD);
                }
            }

            return value;
        }

        /**
         * Sets a setting in the settings table, if setting doesn't exist, it is created.
         * @param settingName name of the setting to set its value
         * @param settingValue value to set
         * @return true if setting is set successfully
         * @throws SQLException if error occurs during lookup
         */
        public boolean setValue(final String settingName, final String settingValue)
                throws SQLException {
            final String newSettingName = settingName.toLowerCase();

            return (exists(newSettingName)
                    ? new UpdateBuilder().UPDATE(TABLE_NAME)
                    .SET(VALUE_FIELD, settingValue).WHERE(NAME_FIELD, newSettingName).buildAndUpdate(db)
                    : new InsertBuilder().INSERT_INTO(TABLE_NAME, NAME_FIELD, VALUE_FIELD)
                    .VALUES(newSettingName, settingValue).buildAndInsert(db)
            ) == 1;
        }

        /**
         * Checks if a setting exists in the settings table.
         * @param settingName setting to check for
         * @return true if setting exists
         * @throws IllegalStateException if a setting is defined multiple times
         * @throws SQLException if error occurs during lookup
         */
        public boolean exists(final String settingName) throws SQLException {
            final String newSettingName = settingName.toLowerCase();
            final int rows = new QueryBuilder().SELECT(VALUE_FIELD).FROM(TABLE_NAME)
                    .WHERE(NAME_FIELD, newSettingName).buildExecuteAndGetNumRows(db);

            if(rows == 1) return true;
            else if (rows > 1) {
                throw new IllegalStateException("Duplicate Settings Rows Found For \"" + newSettingName + '"');
            } else return false;
        }
    }

    /**
     * Releases this database and JDBC resources immediately
     * instead of waiting for this to happen when it is automatically closed,
     * It is generally good practice to release resources as soon as
     * you are finished with them to avoid tying up database resources.
     */
    @Override
    public void close() throws SQLException { connection.disconnect(); }
}
