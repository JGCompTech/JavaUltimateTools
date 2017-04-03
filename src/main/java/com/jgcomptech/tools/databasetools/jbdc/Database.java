package com.jgcomptech.tools.databasetools.jbdc;

import com.jgcomptech.tools.SecurityTools;
import com.jgcomptech.tools.dialogs.DialogResult;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxButtons;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import org.apache.commons.dbcp2.BasicDataSource;

import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Database object that allows communication with a SQL database
 */
public class Database implements AutoCloseable {
    private java.sql.Connection conn;
    private String connString;
    final private String username;
    final private String password;
    final private String dbName;
    final private DatabaseType dbType;
    private String dbDriver;
    private Info info;
    private Connection connection;
    private Tasks tasks;

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
                dbDriver = "org.h2.Driver";
                break;
            case SQLite:
                connString = "jdbc:sqlite:" + dbFilePath;
                dbDriver = "org.sqlite.JDBC";
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

    /**
     * Allows management of the connection to the database
     * @return instance of the Connection class
     */
    public Connection getConnection() {
        if(connection == null) {
            connection = new Connection();
        }
        return connection;
    }
    /**
     * The object that stores tasks related to the connection,
     * use {@code getConnection} to access methods
     */
    public class Connection {
        /**
         * Returns the raw {@code java.sql.Connection} object
         * @return the Connection object
         */
        public java.sql.Connection getObject() { return conn; }

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
        public void connect(boolean showStatusAlert) throws SQLException {
            try(final BasicDataSource ds = new BasicDataSource()) {
                ds.setDriverClassName(dbDriver);
                ds.setUrl(connString);
                ds.setUsername(username);
                ds.setPassword(password);

                conn = ds.getConnection();

                if(showStatusAlert) MessageBox.show("Connection to database has been established.", "Database Alert",
                        "Database Alert", MessageBoxIcon.INFORMATION);
            } catch (SQLException e) {
                if(e.getMessage().contains("Database may be already in use")) {
                    final DialogResult result = MessageBox.show("\"" + dbName + "\" is currently in use!"
                                    + System.lineSeparator() + "Please Close any open connections!",
                            "Error!", "Database Error", MessageBoxButtons.RetryCancel, MessageBoxIcon.ERROR);
                    if(result.equals(DialogResult.RETRY)) connect(showStatusAlert);
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
         * resources, shows message box on error
         * @throws SQLException if error occurs
         */
        public void disconnect() throws SQLException {
            if(conn != null) if(!conn.isClosed()) conn.close();
        }

        /**
         * Checks if a connection is open to the database
         * @return true if connected
         * @throws SQLException if error occurs
         */
        public boolean isConnected() throws SQLException {
            return !(conn == null && conn.isClosed());
        }
    }

    /**
     * Returns info about the database
     * @return instance of the Info class
     */
    public Info getInfo() {
        if(info == null) {
            info = new Info();
        }
        return info;
    }
    /**
     * The object that stores the database info,
     * use {@code getInfo} to access methods
     */
    public class Info {

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
         * Returns an ArrayList of all tables in the database
         * @return ArrayList of tables
         * @throws SQLException if error occurs
         */
        public ArrayList getTablesList() throws SQLException {

            final ArrayList<String> listOfTables = new ArrayList<>();

            final DatabaseMetaData md = conn.getMetaData();

            try(final ResultSet rs = md.getTables(null, null, "%", null)) {
                while (rs.next()) {
                    if (rs.getString(4).equalsIgnoreCase("TABLE")) {
                        listOfTables.add(rs.getString(3));
                    }
                }
            }

            return listOfTables;
        }
    }

    /**
     * Runs tasks against the database
     * @return instance of the Tasks class
     */
    public Tasks getTasks() {
        if(tasks == null) {
            tasks = new Tasks();
        }
        return tasks;
    }
    /**
     * The object that stores methods to complete database tasks,
     * use {@code getTasks} to access methods
     */
    public class Tasks {
        /**
         * Executes the given SQL statement, which returns a single
         * {@code ResultSet} object, auto-closes statement object when ResultSet object is closed
         * @param sql an SQL statement to be sent to the database, typically a
         *        static SQL {@code SELECT} statement
         * @return a {@code ResultSet} object that contains the data produced
         *         by the given query; never {@code null}
         * @throws SQLException if a database access error occurs or the given
         * SQL statement produces anything other than a single
         * {@code ResultSet} object
         */
        public ResultSet executeQuery(String sql) throws SQLException {
            final Statement stmt;

            int resultsetType = dbType == DatabaseType.SQLite ? ResultSet.TYPE_FORWARD_ONLY
                    : ResultSet.TYPE_SCROLL_INSENSITIVE;

            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.closeOnCompletion();
            return stmt.executeQuery(sql);
        }

        /**
         * Executes the given SQL statement, which may be an {@code INSERT},
         * {@code UPDATE}, or {@code DELETE} statement or an
         * SQL statement that returns nothing, such as an SQL DDL statement
         * @param sql an SQL Data Manipulation Language (DML) statement, such as {@code INSERT}, {@code UPDATE} or
         * {@code DELETE}; or an SQL statement that returns nothing,
         * such as a DDL statement.
         * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
         *         or (2) 0 for SQL statements that return nothing
         * @throws SQLException if a database access error occurs or the given
         * SQL statement produces a {@code ResultSet} object
         */
        public int executeUpdate(String sql) throws SQLException {
            final Statement stmt = conn.createStatement();
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
        public boolean execute(String sql) throws SQLException {
            final Statement stmt = conn.createStatement();
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
            if(getInfo().TableExists(tableName)) {
                if(!suppressExistsError)
                    MessageBox.show("\"" + tableName + "\" Table Already Exists!", "Error!",
                            "Database Error", MessageBoxIcon.ERROR);
                return false;
            } else {
                try(Statement stmt = conn.createStatement()) {
                    // create a new table
                    stmt.execute(query);
                    return true;
                }
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
                    if(!suppressExistsError) MessageBox.show("\"" + indexName + "\" Index Already Exists!",
                            "Error!", "Database Error", MessageBoxIcon.ERROR);
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
        public int getResultRows(ResultSet res){
            int totalRows = 0;
            try { while(res.next()) totalRows++; }
            catch(SQLException ex) { return 0; }
            return totalRows ;
        }
    }

    /**
     * Allows for management of user accounts in a database
     */
    public static class Users {
        /**
         * Creates a Users table in the specified database
         * @param db the database to create the table in
         * @param suppressExistsError if true suppresses error if the table already exists
         * @throws SQLException if error occurs
         */
        public static void createTable(Database db, boolean suppressExistsError) throws SQLException {
            final String TABLE_NAME = "Users";
            final String INDEX_NAME = "login_index";
            final String ID_FIELD = "Id";
            final String USERNAME_FIELD = "Username";
            final String PASSWORD_FIELD = "Password";
            final String SALT_FIELD = "Salt";
            final String TYPE_FIELD = "Type";
            String USE_AUTO_INCREMENT = "AUTO_INCREMENT";

            if(db.getInfo().getDbType() == DatabaseType.SQLite) { USE_AUTO_INCREMENT = ""; }

            //The password field is 150 chars in length due to the length of a SHA-512 hash
            final String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ( "
                    + ID_FIELD + " INTEGER NOT NULL PRIMARY KEY " + USE_AUTO_INCREMENT +
                    ", " + USERNAME_FIELD + " nvarchar(100) NOT NULL" +
                    ", " + PASSWORD_FIELD + " nvarchar(150) NOT NULL" +
                    ", " + SALT_FIELD + " nvarchar(100) NOT NULL" +
                    ", " + TYPE_FIELD + " nvarchar(100) NOT NULL)";

            final String createLoginIndexQuery = "CREATE INDEX " + INDEX_NAME + " ON " + TABLE_NAME
                    + "(" + USERNAME_FIELD + "," + PASSWORD_FIELD + "," + SALT_FIELD + ")";

            db.getTasks().createTable(TABLE_NAME, createTableQuery, suppressExistsError);

            db.getTasks().createIndex(INDEX_NAME, createLoginIndexQuery, suppressExistsError);
        }

        /**
         * Creates a new user in the database using SHA-512 password hashing
         * @param db database that contains the users table
         * @param username the username to add
         * @param password the password for the new user
         * @param type the user type for the new user
         * @return true if user creation is successful
         * @throws SQLException if error occurs
         * @throws GeneralSecurityException if error occurs
         */
        public static boolean create(Database db, String username, String password, String type) throws SQLException, GeneralSecurityException {
            final String TABLE_NAME = "Users";

            if(db.getInfo().TableExists(TABLE_NAME)) {
                if(!exists(db, username)) {
                    final String salt = SecurityTools.PasswordHashes.createSaltString(16);
                    password = SecurityTools.PasswordHashes.createHash(password, salt);

                    final String UsersSeedQuery = "INSERT INTO " + TABLE_NAME + " (username, password, salt, type)" +
                            System.lineSeparator() + "VALUES (\'" + username + "\', \'" + password + "\', \'" + salt + "\', \'" + type + "\')";
                    db.getTasks().executeUpdate(UsersSeedQuery);
                    return true;
                }
            } else MessageBox.show("\"" + TABLE_NAME + "\" Table Not Found!", "Database Alert",
                        "Database Alert", MessageBoxIcon.ERROR);
            return false;
        }

        /**
         * Checks if the specified username exists in the database
         * @param db database that contains the users table
         * @param username the username to check
         * @return true if the user exists
         * @throws SQLException if error occurs
         */
        public static boolean exists(Database db, String username) throws SQLException {
            final String TABLE_NAME = "Users";
            String USERNAME_COLUMN_NAME = "Username";

            if(db.getInfo().getDbType() == DatabaseType.H2) USERNAME_COLUMN_NAME = "USERNAME";

            if(db.getInfo().TableExists(TABLE_NAME)) {
                try (ResultSet result = db.getTasks().executeQuery("SELECT * FROM " + TABLE_NAME)) {
                    while(result.next()) {
                        if(result.getString(USERNAME_COLUMN_NAME).toLowerCase().equals(username)) return true;
                    }
                }
            } else MessageBox.show("\"" + TABLE_NAME + "\" Table Not Found!", "Database Alert",
                        "Database Alert", MessageBoxIcon.ERROR);
            return false;
        }

        /**
         * Sets a new password for an existing user using SHA-512 password hashing
         * @param db database that contains the users table
         * @param username the username to change
         * @param password the new password
         * @return true if password is changed successfully
         * @throws SQLException if error occurs
         * @throws GeneralSecurityException if error occurs
         */
        public static boolean setPassword(Database db, String username, String password) throws SQLException, GeneralSecurityException {
            final String TABLE_NAME = "Users";
            final String USERNAME_FIELD = "Username";
            final String PASSWORD_FIELD = "Password";
            final String SALT_FIELD = "Salt";

            if(db.getInfo().TableExists(TABLE_NAME)) {
                if(exists(db, username)) {
                    final String salt = SecurityTools.PasswordHashes.createSaltString(16);
                    password = SecurityTools.PasswordHashes.createHash(password, salt);

                    final String query = "UPDATE " + TABLE_NAME + " " +
                            "SET " + PASSWORD_FIELD + " = '" + password + "', "
                            + SALT_FIELD + " = '" + salt + "' " +
                            "WHERE " + USERNAME_FIELD + " = '" + username + "'";
                    db.getTasks().executeUpdate(query);
                    return true;
                } else MessageBox.show("\"" + username + "\" User Not Found!", "Database Alert",
                            "Database Alert", MessageBoxIcon.ERROR);
            } else MessageBox.show("\"" + TABLE_NAME + "\" Table Not Found!", "Database Alert",
                        "Database Alert", MessageBoxIcon.ERROR);
            return false;
        }

        /**
         * Checks to see if the specified password matches the stored password in the database
         * @param db database that contains the users table
         * @param username the username to check against
         * @param password the password to check against
         * @return true if the passwords match
         * @throws SQLException if error occurs
         * @throws GeneralSecurityException if error occurs
         */
        public static boolean checkPasswordMatches(Database db, String username, String password) throws SQLException, GeneralSecurityException {
            final String TABLE_NAME = "Users";
            final String USERNAME_FIELD = "Username";
            final String PASSWORD_FIELD = "Password";
            final String SALT_FIELD = "Salt";

            if(db.getInfo().TableExists(TABLE_NAME)) {
                if(exists(db, username)) {
                    final String query = "SELECT * FROM " + TABLE_NAME +
                            " WHERE " + USERNAME_FIELD + " = '" + username + "'";

                    final String salt;
                    final String databasePass;
                    try(ResultSet result = db.getTasks().executeQuery(query)) {

                        result.next();
                        salt = result.getString(SALT_FIELD);
                        databasePass = result.getString(PASSWORD_FIELD);
                    }
                    return SecurityTools.PasswordHashes.checkHashesMatch(password, databasePass, salt);
                }
            } else MessageBox.show("\"" + TABLE_NAME + "\" Table Not Found!", "Database Alert",
                        "Database Alert", MessageBoxIcon.ERROR);
            return false;
        }

        /**
         * Returns userType for the specified username
         * @param db database that contains the users table
         * @param username the username to retrieve info from
         * @return the userType
         * @throws SQLException if error occurs
         */
        public static String getUserType(Database db, String username) throws SQLException {
            final String TABLE_NAME = "Users";
            final String USERNAME_FIELD = "Username";
            final String TYPE_FIELD = "Type";

            if(db.getInfo().TableExists(TABLE_NAME)) {
                if(exists(db, username)) {
                    final String query = "SELECT " + TYPE_FIELD + " FROM " + TABLE_NAME +
                            " WHERE " + USERNAME_FIELD + " = '" + username + "'";

                    try(ResultSet result = db.getTasks().executeQuery(query)) {
                        result.next();
                        return result.getString(TYPE_FIELD);
                    }
                }
            } else MessageBox.show("\"" + TABLE_NAME + "\" Table Not Found!", "Database Alert",
                        "Database Alert", MessageBoxIcon.ERROR);
            return "ERROR";
        }

        // This class should only be called statically
        private Users() { super(); }
    }

    /**
     * Allows setting and retrieval of settings values from a database
     */
    public static class Settings {
        /**
         * Creates a Settings table in the specified database
         * @param db the database to create the table in
         * @param suppressExistsError if true suppresses error if the table already exists
         * @throws SQLException if error occurs
         */
        public static void createTable(Database db, boolean suppressExistsError) throws SQLException {
            final String TABLE_NAME = "Settings";
            final String ID_FIELD = "Id";
            final String NAME_FIELD = "Name";
            final String VALUE_FIELD = "Value";
            String USE_AUTO_INCREMENT = "AUTO_INCREMENT";

            if(db.getInfo().getDbType() == DatabaseType.SQLite) { USE_AUTO_INCREMENT = ""; }

            final String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ( "
                    + ID_FIELD + " INTEGER NOT NULL PRIMARY KEY " + USE_AUTO_INCREMENT +
                    ", " + NAME_FIELD + " nvarchar(100) NOT NULL" +
                    ", " + VALUE_FIELD + " nvarchar(100) NOT NULL)";

            db.getTasks().createTable(TABLE_NAME, createTableQuery, suppressExistsError);
        }

        /**
         * Returns a setting from the settings table, if setting doesn't exist, returns an empty string
         * @param db database that contains the settings table
         * @param settingName name of the setting to return a value
         * @return value of the specified setting
         * @throws SQLException if error occurs
         */
        public static String getValue(Database db, String settingName) throws SQLException {
            final String TABLE_NAME = "Settings";
            final String NAME_FIELD = "Name";
            final String VALUE_FIELD = "Value";

            settingName = settingName.toLowerCase();

            String value = "";

            if(exists(db, settingName)) {
                final String searchTableQuery = "SELECT " + VALUE_FIELD + " FROM " + TABLE_NAME +
                        " WHERE " + NAME_FIELD + " = '" + settingName + "'";

                try(ResultSet result = db.getTasks().executeQuery(searchTableQuery)) {
                    result.next();
                    value = result.getString(VALUE_FIELD);
                }
            }

            return value;
        }

        /**
         * Sets a setting in the settings table, if setting doesn't exist, it is created
         * @param db database that contains the settings table
         * @param settingName name of the setting to set its value
         * @param settingValue value to set
         * @return true if setting is set successfully
         * @throws SQLException if error occurs
         */
        public static boolean setValue(Database db, String settingName, String settingValue) throws SQLException {
            final String TABLE_NAME = "Settings";
            final String NAME_FIELD = "Name";
            final String VALUE_FIELD = "Value";

            settingName = settingName.toLowerCase();

            final String updateSettingQuery;

            updateSettingQuery = exists(db, settingName) ? "UPDATE " + TABLE_NAME + " " +
                    "SET " + VALUE_FIELD + " = '" + settingValue + "' " +
                    "WHERE " + NAME_FIELD + " = '" + settingName + "'"
                    :
                    "INSERT INTO " + TABLE_NAME + " (" + NAME_FIELD + ", " + VALUE_FIELD + ") " +
                    "VALUES ('" + settingName + "', '" + settingValue + "')";

            final int result = db.getTasks().executeUpdate(updateSettingQuery);
            return result == 1;
        }

        /**
         * Checks if a setting exists in the settings table
         * @param db database that contains the settings table
         * @param settingName setting to check for
         * @return true if setting exists
         * @throws IllegalStateException if a setting is defined multiple times
         * @throws SQLException if error occurs
         */
        public static boolean exists(Database db, String settingName) throws SQLException {
            final String TABLE_NAME = "Settings";
            final String NAME_FIELD = "Name";
            final String VALUE_FIELD = "Value";

            settingName = settingName.toLowerCase();

            final String searchTableQuery = "SELECT " + VALUE_FIELD + " FROM " + TABLE_NAME +
                    " WHERE " + NAME_FIELD + " = '" + settingName + "'";

            final int rows = db.getTasks().getResultRows(db.getTasks().executeQuery(searchTableQuery));

            if(rows == 1) {
                return true;
            } else if (rows > 1) {
                throw new IllegalStateException("Duplicate Settings Rows Found For \"" + settingName + "\"");
            } else {
                return false;
            }
        }

        // This class should only be called statically
        private Settings() { super(); }
    }

    /**
     * Releases this database and JDBC resources immediately
     * instead of waiting for this to happen when it is automatically closed,
     * It is generally good practice to release resources as soon as
     * you are finished with them to avoid tying up database
     * resources
     */
    @Override
    public void close() throws SQLException {
        if(conn != null && !conn.isClosed()) conn.close();
    }
}