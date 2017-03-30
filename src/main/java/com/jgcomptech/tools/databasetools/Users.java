package com.jgcomptech.tools.databasetools;

import com.jgcomptech.tools.SecurityTools;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Allows for management of user accounts in a database
 */
public class Users {
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

        if(db.getDbType() == DatabaseType.SQLite) { USE_AUTO_INCREMENT = ""; }

        //The password field is 150 chars in length due to the length of a SHA-512 hash
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ( "
                + ID_FIELD + " INTEGER NOT NULL PRIMARY KEY " + USE_AUTO_INCREMENT +
                ", " + USERNAME_FIELD + " nvarchar(100) NOT NULL" +
                ", " + PASSWORD_FIELD + " nvarchar(150) NOT NULL" +
                ", " + SALT_FIELD + " nvarchar(100) NOT NULL" +
                ", " + TYPE_FIELD + " nvarchar(100) NOT NULL)";

        String createLoginIndexQuery = "CREATE INDEX " + INDEX_NAME + " ON " + TABLE_NAME
                + "(" + USERNAME_FIELD + "," + PASSWORD_FIELD + "," + SALT_FIELD + ")";

        db.createTable(TABLE_NAME, createTableQuery, suppressExistsError);

        db.createIndex(INDEX_NAME, createLoginIndexQuery, suppressExistsError);
    }

    /**
     * Creates a new user in the database using SHA-512 password hashing
     * @param db database that contains the users table
     * @param username the username to add
     * @param password the password for the new user
     * @param type the user type for the new user
     * @return true if user creation is successful
     * @throws SQLException if error occurs
     */
    public static boolean create(Database db, String username, String password, String type) throws SQLException {
        final String TABLE_NAME = "Users";

        if(db.TableExists(TABLE_NAME)) {
            if(!exists(db, username)) {
                String salt = SecurityTools.PasswordHashes.createSaltString(16);
                password = SecurityTools.PasswordHashes.createHash(password, salt);

                String UsersSeedQuery = "INSERT INTO " + TABLE_NAME + " (username, password, salt, type)\n" +
                        "VALUES (\'" + username + "\', \'" + password + "\', \'" + salt + "\', \'" + type + "\')";
                db.executeUpdate(UsersSeedQuery);
                return true;
            }
        } else {
            MessageBox.show("\"" + TABLE_NAME + "\" Table Not Found!", "Database Alert",
                    "Database Alert", MessageBoxIcon.ERROR);
        }

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

        if(db.getDbType() == DatabaseType.H2) USERNAME_COLUMN_NAME = "USERNAME";

        if(db.TableExists(TABLE_NAME)) {
            try (ResultSet result = db.executeQuery("SELECT * FROM " + TABLE_NAME)) {
                while(result.next()) {
                    if(result.getString(USERNAME_COLUMN_NAME).toLowerCase().equals(username)) return true;
                }
            }
        } else {
            MessageBox.show("\"" + TABLE_NAME + "\" Table Not Found!", "Database Alert",
                    "Database Alert", MessageBoxIcon.ERROR);
        }

        return false;
    }

    /**
     * Sets a new password for an existing user using SHA-512 password hashing
     * @param db database that contains the users table
     * @param username the username to change
     * @param password the new password
     * @return true if password is changed successfully
     * @throws SQLException if error occurs
     */
    public static boolean setPassword(Database db, String username, String password) throws SQLException {
        final String TABLE_NAME = "Users";
        final String USERNAME_FIELD = "Username";
        final String PASSWORD_FIELD = "Password";
        final String SALT_FIELD = "Salt";

        if(db.TableExists(TABLE_NAME)) {
            if(exists(db, username)) {
                String salt = SecurityTools.PasswordHashes.createSaltString(16);
                password = SecurityTools.PasswordHashes.createHash(password, salt);

                String query = "UPDATE " + TABLE_NAME + " " +
                        "SET " + PASSWORD_FIELD + " = '" + password + "', "
                        + SALT_FIELD + " = '" + salt + "' " +
                        "WHERE " + USERNAME_FIELD + " = '" + username + "'";
                db.executeUpdate(query);
                return true;
            } else {
                MessageBox.show("\"" + username + "\" User Not Found!", "Database Alert",
                        "Database Alert", MessageBoxIcon.ERROR);
            }
        } else {
            MessageBox.show("\"" + TABLE_NAME + "\" Table Not Found!", "Database Alert",
                    "Database Alert", MessageBoxIcon.ERROR);
        }

        return false;
    }

    /**
     * Checks to see if the specified password matches the stored password in the database
     * @param db database that contains the users table
     * @param username the username to check against
     * @param password the password to check against
     * @return true if the passwords match
     * @throws SQLException if error occurs
     */
    public static boolean checkPasswordMatches(Database db, String username, String password) throws SQLException {
        final String TABLE_NAME = "Users";
        final String USERNAME_FIELD = "Username";
        final String PASSWORD_FIELD = "Password";
        final String SALT_FIELD = "Salt";

        if(db.TableExists(TABLE_NAME)) {
            if(exists(db, username)) {
                String query = "SELECT * FROM " + TABLE_NAME +
                        " WHERE " + USERNAME_FIELD + " = '" + username + "'";

                ResultSet result = db.executeQuery(query);

                result.next();
                String salt = result.getString(SALT_FIELD);
                String databasePass = result.getString(PASSWORD_FIELD);
                return SecurityTools.PasswordHashes.checkHashesMatch(password, databasePass, salt);
            }
        } else {
            MessageBox.show("\"" + TABLE_NAME + "\" Table Not Found!", "Database Alert",
                    "Database Alert", MessageBoxIcon.ERROR);
        }

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

        if(db.TableExists(TABLE_NAME)) {
            if(exists(db, username)) {
                String query = "SELECT " + TYPE_FIELD + " FROM " + TABLE_NAME +
                        " WHERE " + USERNAME_FIELD + " = '" + username + "'";

                ResultSet result = db.executeQuery(query);

                result.next();
                return result.getString(TYPE_FIELD);
            }
        } else {
            MessageBox.show("\"" + TABLE_NAME + "\" Table Not Found!", "Database Alert",
                    "Database Alert", MessageBoxIcon.ERROR);
        }

        return "ERROR";
    }

    // This class should only be called statically
    private Users() { super(); }
}
