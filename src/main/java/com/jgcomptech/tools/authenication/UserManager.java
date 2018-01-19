package com.jgcomptech.tools.authenication;

import com.jgcomptech.tools.SecurityTools;
import com.jgcomptech.tools.databasetools.jbdc.*;

import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;

/**
 * Manages all user accounts in the database.
 * @since 1.4.0
 */
public class UserManager {
    private static final String ID_FIELD = "Id";
    private static final String USERNAME_FIELD = "Username";
    private static final String PASSWORD_FIELD = "Password";
    private static final String SALT_FIELD = "Salt";
    private static final String TYPE_FIELD = "Type";
    private static final String TABLE_NAME = "Users";
    private static final String INDEX_NAME = "login_index";
    private final Database db;
    private SessionManager sessionManager;
    private final UserRoleManager userRoleManager = UserRoleManager.getInstance();

    /**
     * Creates a new instance of the User Manager.
     * @param db the database to use
     * @throws IllegalArgumentException if db is null
     * @throws SQLException if an error occurs while creating the users table
     */
    public UserManager(final Database db) throws SQLException {
        this.db = db;

        if(db == null) throw new IllegalArgumentException("Database Cannot Be Null!");

        if(!db.getInfo().tableExists(TABLE_NAME)) {
            //The password field is 150 chars in length due to the length of a SHA-512 hash
            TypedStatement.newTable()
                    .CREATE(TABLE_NAME, db)
                    .addColumn(new ColumnBuilder(ID_FIELD, DataTypes.INTEGER).notNull().primaryKey().autoIncrement())
                    .addColumn(new ColumnBuilder(USERNAME_FIELD, DataTypes.NVARCHAR, 100).notNull())
                    .addColumn(new ColumnBuilder(PASSWORD_FIELD, DataTypes.NVARCHAR, 150).notNull())
                    .addColumn(new ColumnBuilder(SALT_FIELD, DataTypes.NVARCHAR, 100).notNull())
                    .addColumn(new ColumnBuilder(TYPE_FIELD, DataTypes.NVARCHAR, 100).notNull())
                    .buildAndCreate();
        }

        TypedStatement.newIndex()
                .CREATE_UNIQUE(INDEX_NAME, TABLE_NAME, db)
                .addColumn(USERNAME_FIELD, PASSWORD_FIELD, SALT_FIELD).buildAndCreate();
    }

    /**
     * Creates a new user in the database using SHA-512 password hashing.
     * @param username the username to add
     * @param password the password for the new user
     * @param userRole the system user role for the new user
     * @return true if user creation is successful
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if an error occurs while creating the user
     * @throws GeneralSecurityException if an error occurs while hashing the password
     */
    public boolean createUser(final String username,
                              final String password,
                              final UserRoleManager.SystemUserRoles userRole)
            throws GeneralSecurityException, SQLException {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(password == null) {
            throw new IllegalArgumentException("Password Cannot Be Null!");
        }
        if(userRole == null) {
            throw new IllegalArgumentException("User Role Cannot Be Null!");
        }
        return createUser(username, password, userRole.getName());
    }

    /**
     * Creates a new user in the database using SHA-512 password hashing.
     * @param username the username to add
     * @param password the password for the new user
     * @param userRole the name of the user role for the new user
     * @return true if user creation is successful
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if an error occurs while creating the user
     * @throws GeneralSecurityException if an error occurs while hashing the password
     */
    public boolean createUser(final String username, final String password, final String userRole)
            throws SQLException, GeneralSecurityException {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(password == null) {
            throw new IllegalArgumentException("Password Cannot Be Null!");
        }
        if(userRole == null || userRole.isEmpty()) {
            throw new IllegalArgumentException("User Role Cannot Be Empty!");
        }
        if(db.getInfo().tableExists(TABLE_NAME)) {
            if(!userExists(username.toLowerCase(Locale.ENGLISH))) {
                final String salt = SecurityTools.PasswordHashes.createSaltString(16);
                final String hashedPassword = SecurityTools.PasswordHashes.createHash(password, salt);
                return TypedStatement.newInsert()
                        .INSERT_INTO(TABLE_NAME, USERNAME_FIELD, PASSWORD_FIELD,
                                SALT_FIELD, TYPE_FIELD)
                        .VALUES(username.toLowerCase(Locale.ENGLISH), hashedPassword, salt, userRole)
                        .buildAndInsert(db) != 0;
            }
        } else throw new TableNotFoundException(TABLE_NAME);
        return false;
    }

    /**
     * Deletes the specified user.
     * @param username the username of the user to delete
     * @return true if no errors occur
     * @throws IllegalArgumentException if username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if an error occurs while deleting user
     */
    public boolean deleteUser(final String username) throws SQLException {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(db.getInfo().tableExists(TABLE_NAME)) {
            return TypedStatement.newDelete()
                    .DELETE_FROM(TABLE_NAME)
                    .WHERE(USERNAME_FIELD, username)
                    .buildAndDelete(db) == 1;
        } else throw new TableNotFoundException(TABLE_NAME);
    }

    /**
     * Returns a UserAccount object representing the specified username.
     * @param username the username to lookup
     * @return a UserAccount object representing the specified username
     * @throws IllegalArgumentException if username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if an error occurs during lookup
     */
    public UserAccount getUser(final String username) throws SQLException {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        UserAccount account;
        if(db.getInfo().tableExists(TABLE_NAME)) {
            if (userExists(username)) {
                TypedStatement statement = new QueryBuilder()
                        .SELECT(USERNAME_FIELD, PASSWORD_FIELD, TYPE_FIELD)
                        .FROM(TABLE_NAME)
                        .WHERE(USERNAME_FIELD, username)
                        .build(db);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    account = new UserAccount(this, rs.getString(USERNAME_FIELD));
                }
            } else throw new IllegalArgumentException('"' + username + "\" Not Found!");
        } else throw new TableNotFoundException(TABLE_NAME);
        return account;
    }

    /**
     * Checks if the specified username exists in the database.
     * @param username the username to check
     * @return true if the user exists
     * @throws IllegalArgumentException if username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if an error occurs during lookup
     */
    public boolean userExists(final String username) throws SQLException {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(db.getInfo().tableExists(TABLE_NAME)) {
            try (ResultSet rs = TypedStatement.newQuery().SELECT_ALL().FROM(TABLE_NAME).buildAndExecute(db)) {
                while(rs.next()) {
                    if (rs.getString(USERNAME_FIELD).toLowerCase().equals(username)) return true;
                }
            }
        } else throw new TableNotFoundException(TABLE_NAME);
        return false;
    }

    /**
     * Returns userType for the specified username.
     * @param username the username to lookup
     * @return the userType
     * @throws IllegalArgumentException if username is null or empty or if username does not exist
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if error occurs during lookup
     */
    public UserRole getUserRole(final String username) throws SQLException {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(db.getInfo().tableExists(TABLE_NAME)) {
            if(userExists(username)) {
                TypedStatement statement = new QueryBuilder()
                        .SELECT(TYPE_FIELD)
                        .FROM(TABLE_NAME)
                        .WHERE(USERNAME_FIELD, username)
                        .build(db);

                try(ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    return userRoleManager.getUserRole(rs.getString(TYPE_FIELD).toLowerCase(Locale.ENGLISH));
                }
            } else throw new IllegalArgumentException('"' + username + "\" Not Found!");
        } else throw new TableNotFoundException(TABLE_NAME);
    }

    /**
     * Sets the user type of the specified user.
     * @param username the username of the user to update
     * @param userRole the system user role to change to
     * @return true if no errors occurred
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if an error occurs while changing user type
     */
    public boolean setUserRole(final String username,
                               final UserRoleManager.SystemUserRoles userRole) throws SQLException {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(userRole == null) {
            throw new IllegalArgumentException("User Role Cannot Be Null!");
        }
        return setUserRole(username, userRole.getName());
    }

    /**
     * Sets the user type of the specified user.
     * @param username the username of the user to update
     * @param userRole the name of the user role to change to
     * @return true if no errors occurred
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if an error occurs while changing user type
     */
    public boolean setUserRole(final String username, final String userRole) throws SQLException {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(userRole == null || userRole.isEmpty()) {
            throw new IllegalArgumentException("User Role Cannot Be Empty!");
        }
        if(db.getInfo().tableExists(TABLE_NAME)) {
            return TypedStatement.newUpdate()
                    .UPDATE(TABLE_NAME)
                    .SET(TYPE_FIELD, userRole)
                    .WHERE(USERNAME_FIELD, username)
                    .buildAndUpdate(db) != 0;
        } else throw new TableNotFoundException(TABLE_NAME);
    }

    /**
     * Sets a new password for an existing user using SHA-512 password hashing.
     * @param username the username to change
     * @param password the new password
     * @return true if password is changed successfully
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if an error occurs while changing password
     * @throws GeneralSecurityException if an error occurs while hashing the password
     */
    public boolean setPassword(final String username, final String password)
            throws SQLException, GeneralSecurityException {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(password == null) {
            throw new IllegalArgumentException("Password Cannot Be Null!");
        }
        if(db.getInfo().tableExists(TABLE_NAME)) {
            final String salt = SecurityTools.PasswordHashes.createSaltString(16);
            final String hashedPassword = SecurityTools.PasswordHashes.createHash(password, salt);
            return TypedStatement.newUpdate()
                    .UPDATE(TABLE_NAME)
                    .SET(PASSWORD_FIELD, hashedPassword)
                    .SET(SALT_FIELD, salt)
                    .WHERE(USERNAME_FIELD, username)
                    .buildAndUpdate(db) != 0;
        } else throw new TableNotFoundException(TABLE_NAME);
    }

    /**
     * Checks to see if the specified password matches the stored password in the database.
     * @param username the username to check against
     * @param password the password to check against
     * @return true if the passwords match
     * @throws IllegalArgumentException if values are null or empty or if username does not exist
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if an error occurs during lookup
     * @throws GeneralSecurityException if an error occurs while hashing the password
     */
    public boolean checkPasswordMatches(final String username, final String password)
            throws SQLException, GeneralSecurityException {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(password == null) {
            throw new IllegalArgumentException("Password Cannot Be Null!");
        }
        if(db.getInfo().tableExists(TABLE_NAME)) {
            if(userExists(username)) {
                TypedStatement statement = new QueryBuilder()
                        .SELECT(SALT_FIELD, PASSWORD_FIELD)
                        .FROM(TABLE_NAME)
                        .WHERE(USERNAME_FIELD, username)
                        .build(db);

                final String salt;
                final String databasePass;
                try(ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    salt = rs.getString(SALT_FIELD);
                    databasePass = rs.getString(PASSWORD_FIELD);
                }
                return SecurityTools.PasswordHashes.checkHashesMatch(password, databasePass, salt);
            } else throw new IllegalArgumentException('"' + username + "\" Not Found!");
        } else throw new TableNotFoundException(TABLE_NAME);
    }

    /**
     * Returns a list of user accounts.
     * @return a HashSet of UserAccount objects representing the users in the users table
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if an error occurs during lookup
     */
    public HashSet<UserAccount> getUsersList() throws SQLException {
        HashSet<UserAccount> accounts = new HashSet<>();
        if(db.getInfo().tableExists(TABLE_NAME)) {
            TypedStatement statement = new QueryBuilder()
                    .SELECT(USERNAME_FIELD, PASSWORD_FIELD, TYPE_FIELD)
                    .FROM(TABLE_NAME)
                    .build(db);

            try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()) {
                    UserAccount account = new UserAccount(this, rs.getString(USERNAME_FIELD));
                    accounts.add(account);
                }
            }
        } else throw new TableNotFoundException(TABLE_NAME);
        return accounts;
    }

    /**
     * Returns a list of the user names in the database.
     * @return a list of the user names in the database
     * @throws TableNotFoundException if users table is missing
     * @throws SQLException if an error occurs during lookup
     */
    public HashSet<String> getUsernameList() throws SQLException {
        HashSet<String> accounts = new HashSet<>();
        if(db.getInfo().tableExists(TABLE_NAME)) {
            TypedStatement statement = new QueryBuilder()
                    .SELECT(USERNAME_FIELD, PASSWORD_FIELD, TYPE_FIELD)
                    .FROM(TABLE_NAME)
                    .build(db);

            try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()) {
                    accounts.add(rs.getString(USERNAME_FIELD));
                }
            }
        } else throw new TableNotFoundException(TABLE_NAME);
        return accounts;
    }

    /**
     * Returns an instance of the SessionManager for login use.
     * @return an instance of the SessionManager
     */
    public SessionManager getSessionManager() {
        if(sessionManager == null) sessionManager = new SessionManager(this);
        return sessionManager;
    }
}
