package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.SecurityTools;
import com.jgcomptech.tools.databasetools.jdbc.DataTypes;
import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.TableNotFoundException;
import com.jgcomptech.tools.databasetools.jdbc.TypedStatement;
import com.jgcomptech.tools.databasetools.jdbc.builders.ColumnBuilder;
import com.jgcomptech.tools.databasetools.jdbc.builders.QueryBuilder;
import org.jetbrains.annotations.Contract;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;

/**
 * Manages all user accounts in the database.
 * @since 1.4.0
 */
public final class UserManager {
    private static final String ID_FIELD = "Id";
    private static final String USERNAME_FIELD = "Username";
    private static final String PASSWORD_FIELD = "Password";
    private static final String SALT_FIELD = "Salt";
    private static final String TYPE_FIELD = "Type";
    private static final String ACCOUNT_CREATION_DATE_FIELD = "Account_Creation_Date";
    private static final String PASSWORD_EXPIRATION_DATE_FIELD = "Password_Expiration_Date";
    private static final String PASSWORD_SET_TO_EXPIRE_FIELD = "Password_Set_To_Expire";
    private static final String ACCOUNT_LOCKED_FIELD = "Account_Locked";
    private static final String TABLE_NAME = "Users";
    private static final String INDEX_NAME = "login_index";
    private final Database db;
    private SessionManager sessionManager;
    private final UserRoleManager userRoleManager = UserRoleManager.getInstance();
    private String appIconPath;
    private String programName;

    /**
     * Creates a new instance of the User Manager.
     * @param db the database to use
     * @throws IllegalArgumentException if db is null
     * @throws UserManagerException if an error occurs while creating the users table
     */
    public UserManager(final Database db) { this(db, null); }

    /**
     * Creates a new instance of the User Manager.
     * @param db the database to use
     * @param appIconPath the path to the icon to use for all dialogs
     * @throws IllegalArgumentException if db is null
     * @throws UserManagerException if an error occurs while creating the users table
     */
    public UserManager(final Database db, final String appIconPath) { this(db, appIconPath, null); }

    /**
     * Creates a new instance of the User Manager.
     * @param db the database to use
     * @param appIconPath the path to the icon to use for all dialogs
     * @param programName the program name to use for all dialogs
     * @throws IllegalArgumentException if db is null
     * @throws UserManagerException if an error occurs while creating the users table
     */
    public UserManager(final Database db, final String appIconPath, final String programName) {
        this.db = db;
        this.appIconPath = appIconPath;
        this.programName = programName;

        if(db == null) throw new IllegalArgumentException("Database Cannot Be Null!");

        try {
            if(!db.getInfo().tableExists(TABLE_NAME)) {
                //The password field is 150 chars in length due to the length of a SHA-512 hash
                //TODO check if 150 chars is too excessive since changing to BCrypt
                TypedStatement.newTable()
                        .CREATE(TABLE_NAME, db)
                        .addColumn(new ColumnBuilder(ID_FIELD, DataTypes.INTEGER).notNull().primaryKey().autoIncrement())
                        .addColumn(new ColumnBuilder(USERNAME_FIELD, DataTypes.NVARCHAR, 100).notNull())
                        .addColumn(new ColumnBuilder(PASSWORD_FIELD, DataTypes.NVARCHAR, 150).notNull())
                        .addColumn(new ColumnBuilder(SALT_FIELD, DataTypes.NVARCHAR, 100).notNull())
                        .addColumn(new ColumnBuilder(TYPE_FIELD, DataTypes.NVARCHAR, 100).notNull())
                        .addColumn(new ColumnBuilder(ACCOUNT_CREATION_DATE_FIELD, DataTypes.DATETIME).notNull())
                        .addColumn(new ColumnBuilder(ACCOUNT_LOCKED_FIELD, DataTypes.BOOLEAN).notNull())
                        .addColumn(new ColumnBuilder(PASSWORD_SET_TO_EXPIRE_FIELD, DataTypes.BOOLEAN).notNull())
                        .addColumn(new ColumnBuilder(PASSWORD_EXPIRATION_DATE_FIELD, DataTypes.DATETIME))
                        .buildAndCreate();
            }

            TypedStatement.newIndex()
                    .CREATE_UNIQUE(INDEX_NAME, TABLE_NAME, db)
                    .addColumn(USERNAME_FIELD, PASSWORD_FIELD, SALT_FIELD)
                    .buildAndCreate();
        } catch (final SQLException e) { throw new UserManagerException(e); }
    }

    /**
     * Creates a new user in the database using BCrypt password hashing.
     * @param username the username to add
     * @param password the password for the new user
     * @param userRole the system user role for the new user
     * @return true if user creation is successful
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs while creating the user
     * @throws PasswordHashingFailedException if an error occurs while hashing the password
     */
    public boolean createUser(final String username,
                              final String password,
                              final UserRoleManager.SystemUserRoles userRole) {
        if(username == null || username.trim().isEmpty()) {
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
     * Creates a new user in the database using BCrypt password hashing.
     * @param username the username to add
     * @param password the password for the new user
     * @param userRole the name of the user role for the new user
     * @return true if user creation is successful
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs while creating the user
     * @throws PasswordHashingFailedException if an error occurs while hashing the password
     */
    public boolean createUser(final String username, final String password, final String userRole) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(password == null) {
            throw new IllegalArgumentException("Password Cannot Be Null!");
        }
        if(userRole == null || userRole.trim().isEmpty()) {
            throw new IllegalArgumentException("User Role Cannot Be Empty!");
        }
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                if(!userExists(username.toLowerCase(Locale.ENGLISH))) {
                    final var salt = SecurityTools.PasswordHashes.createBCryptSaltString(12);
                    final var hashedPassword = SecurityTools.PasswordHashes.createBCryptHash(password, salt);
                    return TypedStatement.newInsert()
                            .INSERT_INTO(TABLE_NAME, USERNAME_FIELD, PASSWORD_FIELD, SALT_FIELD, TYPE_FIELD,
                                    ACCOUNT_CREATION_DATE_FIELD, PASSWORD_SET_TO_EXPIRE_FIELD,
                                    ACCOUNT_LOCKED_FIELD, PASSWORD_EXPIRATION_DATE_FIELD)
                            .VALUES(username.toLowerCase(Locale.ENGLISH), hashedPassword, salt, userRole,
                                    LocalDateTime.now().toString(), "false", "false",
                                    LocalDateTime.now().plusYears(1000).toString())
                            .buildAndInsert(db) != 0;
                }
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) {
            throw new UserManagerException(e);
        }
        return false;
    }

    /**
     * Deletes the specified user.
     * @param username the username of the user to delete
     * @return true if no errors occur
     * @throws IllegalArgumentException if username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs while deleting user
     */
    public boolean deleteUser(final String username) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                return TypedStatement.newDelete()
                        .DELETE_FROM(TABLE_NAME)
                        .WHERE(USERNAME_FIELD, username)
                        .buildAndDelete(db) == 1;
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) {
            throw new UserManagerException(e);
        }
    }

    /**
     * Returns a UserAccount object representing the specified username.
     * @param username the username to lookup
     * @return a UserAccount object representing the specified username
     * @throws IllegalArgumentException if username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public UserAccount getUser(final String username) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        final UserAccount account;
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                if (userExists(username)) {
                    final var statement = new QueryBuilder()
                            .SELECT(USERNAME_FIELD, PASSWORD_FIELD, TYPE_FIELD, SALT_FIELD,
                                    ACCOUNT_CREATION_DATE_FIELD, ACCOUNT_LOCKED_FIELD,
                                    PASSWORD_SET_TO_EXPIRE_FIELD, PASSWORD_EXPIRATION_DATE_FIELD)
                            .FROM(TABLE_NAME)
                            .WHERE(USERNAME_FIELD, username)
                            .build(db);

                    try (final var rs = statement.executeQuery()) {
                        rs.next();
                        account = new UserAccount(rs.getString(USERNAME_FIELD),
                                rs.getTimestamp(ACCOUNT_CREATION_DATE_FIELD).toLocalDateTime(),
                                rs.getBoolean(ACCOUNT_LOCKED_FIELD), rs.getBoolean(PASSWORD_SET_TO_EXPIRE_FIELD),
                                rs.getTimestamp(PASSWORD_EXPIRATION_DATE_FIELD).toLocalDateTime());
                    }
                } else return null;
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) {
            throw new UserManagerException(e);
        }
        return account;
    }

    /**
     * Checks if the specified username exists in the database.
     * @param username the username to check
     * @return true if the user exists
     * @throws IllegalArgumentException if username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public boolean userExists(final String username) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                try (final var rs = TypedStatement.newQuery().SELECT_ALL().FROM(TABLE_NAME).buildAndExecute(db)) {
                    while(rs.next()) {
                        if (rs.getString(USERNAME_FIELD).toLowerCase().equals(username)) return true;
                    }
                }
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) { throw new UserManagerException(e); }
        return false;
    }

    /**
     * Returns user role for the specified username.
     * @param username the username to lookup
     * @return the user role
     * @throws IllegalArgumentException if username is null or empty or if username does not exist
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if error occurs during lookup
     */
    public UserRole getUserRole(final String username) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                if(userExists(username)) {
                    final var statement = new QueryBuilder()
                            .SELECT(TYPE_FIELD)
                            .FROM(TABLE_NAME)
                            .WHERE(USERNAME_FIELD, username)
                            .build(db);

                    try(final var rs = statement.executeQuery()) {
                        rs.next();
                        return userRoleManager.getUserRole(rs.getString(TYPE_FIELD).toLowerCase(Locale.ENGLISH));
                    }
                } else throw new IllegalArgumentException('"' + username + "\" Not Found!");
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) { throw new UserManagerException(e); }
    }

    /**
     * Sets the user role of the specified user.
     * @param username the username of the user to update
     * @param userRole the system user role to change to
     * @return true if no errors occurred
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs while changing user role
     */
    public boolean setUserRole(final String username,
                               final UserRoleManager.SystemUserRoles userRole) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(userRole == null) {
            throw new IllegalArgumentException("User Role Cannot Be Null!");
        }
        return setUserRole(username, userRole.getName());
    }

    /**
     * Sets the user role of the specified user.
     * @param username the username of the user to update
     * @param userRole the name of the user role to change to
     * @return true if no errors occurred
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs while changing user role
     */
    public boolean setUserRole(final String username, final String userRole) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(userRole == null || userRole.trim().isEmpty()) {
            throw new IllegalArgumentException("User Role Cannot Be Empty!");
        }
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                return TypedStatement.newUpdate()
                        .UPDATE(TABLE_NAME)
                        .SET(TYPE_FIELD, userRole)
                        .WHERE(USERNAME_FIELD, username)
                        .buildAndUpdate(db) != 0;
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) {
            throw new UserManagerException(e);
        }
    }

    /**
     * Sets a new password for an existing user using BCrypt password hashing.
     * @param username the username to change
     * @param password the new password
     * @return true if password is changed successfully
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs while changing password
     * @throws PasswordHashingFailedException if an error occurs while hashing the password
     */
    public boolean setPassword(final String username, final String password) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(password == null) {
            throw new IllegalArgumentException("Password Cannot Be Null!");
        }
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                final var salt = SecurityTools.PasswordHashes.createBCryptSaltString(12);
                final var hashedPassword = SecurityTools.PasswordHashes.createBCryptHash(password, salt);
                return TypedStatement.newUpdate()
                        .UPDATE(TABLE_NAME)
                        .SET(PASSWORD_FIELD, hashedPassword)
                        .SET(SALT_FIELD, salt)
                        .WHERE(USERNAME_FIELD, username)
                        .buildAndUpdate(db) != 0;
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) {
            throw new UserManagerException(e);
        }
    }

    /**
     * Sets the lock status for the specified user.
     * @param username the username to change
     * @param status the status to set
     * @return true if the status is changed successfully
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs while changing password
     * @throws PasswordHashingFailedException if an error occurs while hashing the password
     * @since 1.5.0
     */
    public boolean setLockStatus(final String username, final Boolean status) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                return TypedStatement.newUpdate()
                        .UPDATE(TABLE_NAME)
                        .SET(ACCOUNT_LOCKED_FIELD, status.toString())
                        .WHERE(USERNAME_FIELD, username)
                        .buildAndUpdate(db) != 0;
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) {
            throw new UserManagerException(e);
        }
    }

    /**
     * Disables the specified user's password expiration.
     * @param username the username to change
     * @return true if password expiration disabled successfully
     * @throws IllegalArgumentException if the username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs while changing password
     * @throws PasswordHashingFailedException if an error occurs while hashing the password
     * @since 1.5.0
     */
    public boolean disablePasswordExpiration(final String username) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                return TypedStatement.newUpdate()
                        .UPDATE(TABLE_NAME)
                        .SET(PASSWORD_SET_TO_EXPIRE_FIELD, "false")
                        .SET(PASSWORD_EXPIRATION_DATE_FIELD, LocalDateTime.now().plusYears(1000).toString())
                        .WHERE(USERNAME_FIELD, username)
                        .buildAndUpdate(db) != 0;
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) {
            throw new UserManagerException(e);
        }
    }

    /**
     * Sets the specified user's password expiration date.
     * @param username the username to change
     * @param date the date to set
     * @return true if the date is changed successfully
     * @throws IllegalArgumentException if values are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs while changing password
     * @throws PasswordHashingFailedException if an error occurs while hashing the password
     * @since 1.5.0
     */
    public boolean setPasswordExpirationDate(final String username, final LocalDateTime date) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                return TypedStatement.newUpdate()
                        .UPDATE(TABLE_NAME)
                        .SET(PASSWORD_SET_TO_EXPIRE_FIELD, "true")
                        .SET(PASSWORD_EXPIRATION_DATE_FIELD, date.toString())
                        .WHERE(USERNAME_FIELD, username)
                        .buildAndUpdate(db) != 0;
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) {
            throw new UserManagerException(e);
        }
    }

    /**
     * Checks to see if the specified password matches the stored password in the database.
     * @param username the username to check against
     * @param password the password to check against
     * @return true if the passwords match
     * @throws IllegalArgumentException if values are null or empty or if username does not exist
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     * @throws PasswordHashingFailedException if an error occurs while hashing the password
     */
    public boolean checkPasswordMatches(final String username, final String password) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty!");
        }
        if(password == null) {
            throw new IllegalArgumentException("Password Cannot Be Null!");
        }
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                if(userExists(username)) {
                    final var statement = new QueryBuilder()
                            .SELECT(SALT_FIELD, PASSWORD_FIELD)
                            .FROM(TABLE_NAME)
                            .WHERE(USERNAME_FIELD, username)
                            .build(db);

                    final String salt;
                    final String databasePass;
                    try(final var rs = statement.executeQuery()) {
                        rs.next();
                        salt = rs.getString(SALT_FIELD);
                        databasePass = rs.getString(PASSWORD_FIELD);
                    }
                    return SecurityTools.PasswordHashes.checkBCryptHashesMatch(password, databasePass, salt);
                } else throw new IllegalArgumentException('"' + username + "\" Not Found!");
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) {
            throw new UserManagerException(e);
        }
    }

    /**
     * Returns a list of user accounts.
     * @return a HashSet of UserAccount objects representing the users in the users table
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public HashSet<UserAccount> getUsersList() {
        final HashSet<UserAccount> accounts = new HashSet<>();
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                final var statement = new QueryBuilder()
                        .SELECT(USERNAME_FIELD, PASSWORD_FIELD, TYPE_FIELD, SALT_FIELD,
                                ACCOUNT_CREATION_DATE_FIELD, ACCOUNT_LOCKED_FIELD,
                                PASSWORD_SET_TO_EXPIRE_FIELD, PASSWORD_EXPIRATION_DATE_FIELD)
                        .FROM(TABLE_NAME)
                        .build(db);

                try (final var rs = statement.executeQuery()) {
                    while(rs.next()) {
                        final var account = new UserAccount(rs.getString(USERNAME_FIELD),
                                rs.getTimestamp(ACCOUNT_CREATION_DATE_FIELD).toLocalDateTime(),
                                rs.getBoolean(ACCOUNT_LOCKED_FIELD), rs.getBoolean(PASSWORD_SET_TO_EXPIRE_FIELD),
                                rs.getTimestamp(PASSWORD_EXPIRATION_DATE_FIELD).toLocalDateTime());
                        accounts.add(account);
                    }
                }
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) { throw new UserManagerException(e); }
        return accounts;
    }

    /**
     * Returns a list of the user names in the database.
     * @return a list of the user names in the database
     * @throws TableNotFoundException if users table is missing
     */
    public HashSet<String> getUsernameList() {
        final HashSet<String> accounts = new HashSet<>();
        try {
            if(db.getInfo().tableExists(TABLE_NAME)) {
                final var statement = new QueryBuilder()
                        .SELECT(USERNAME_FIELD, PASSWORD_FIELD, TYPE_FIELD)
                        .FROM(TABLE_NAME)
                        .build(db);

                try (final var rs = statement.executeQuery()) {
                    while(rs.next()) {
                        accounts.add(rs.getString(USERNAME_FIELD));
                    }
                }
            } else throw new TableNotFoundException(TABLE_NAME);
        } catch (final SQLException e) {
            throw new UserManagerException(e);
        }
        return accounts;
    }

    /**
     * Returns an instance of the SessionManager for login use.
     * @return an instance of the SessionManager
     */
    public SessionManager getSessionManager() {
        if(sessionManager == null) sessionManager = new SessionManager(this, appIconPath, programName);
        return sessionManager;
    }

    /**
     * Returns the path to the icon to use for all dialogs.
     * @return the path to the icon to use for all dialogs
     */
    public String getAppIconPath() { return appIconPath; }

    /**
     * Sets the path to the icon to use for all dialogs.
     * @param appIconPath the path to set
     */
    public void setAppIconPath(final String appIconPath) { this.appIconPath = appIconPath; }

    /**
     * Returns the program name to use for all dialogs.
     * @return the program name to use for all dialogs
     */
    public String getProgramName() { return programName; }

    /**
     * Sets the program name to use for all dialogs.
     * @param programName the program name to set
     */
    public void setProgramName(final String programName) { this.programName = programName; }

    @Contract(" -> fail")
    @Override public Object clone() throws CloneNotSupportedException { throw new CloneNotSupportedException(); }
}
