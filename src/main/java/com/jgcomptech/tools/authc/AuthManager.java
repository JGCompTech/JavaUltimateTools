package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.authz.PermissionManager;
import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.TableNotFoundException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

//TODO Create resetInstance() method to allow a new instance to be initialized.

/**
 * Manages all tasks related to a user account including sessions and user roles.
 * @since 1.5.0
 */
public final class AuthManager {
    private UserManager userManager;
    private SessionManager sessionManager;
    private UserRoleManager userRoleManager;
    private static AuthManager instance;

    private AuthManager() { }

    /**
     * Retrieves the current instance of the AuthManager.
     * @return the current instance of the AuthManager
     * @throws IllegalStateException if the getNewInstance method has not been called.
     * This is required to initialize the database.
     */
    public static AuthManager getInstance() {
        if(instance == null) throw new IllegalStateException(
                "Auth Manager Has Not Been Initialized! The method getNewInstance must be called at least once!");
        return instance;
    }

    /**
     * Creates and retrieves a new instance of the AuthManager with the specified database.
     * @param db the database for storing all user account information
     * @return a new instance of the AuthManager
     */
    public static AuthManager getNewInstance(final Database db) {
        return getNewInstance(db, null);
    }

    /**
     * Creates and retrieves a new instance of the AuthManager with the specified parameters.
     * @param db the database for storing all user account information
     * @param appIconPath the path to the icon to use in all login dialogs
     * @return a new instance of the AuthManager
     */
    public static AuthManager getNewInstance(final Database db, final String appIconPath) {
        return getNewInstance(db, appIconPath, null);
    }

    /**
     * Creates and retrieves a new instance of the AuthManager with the specified parameters.
     * @param db the database for storing all user account information
     * @param appIconPath the path to the icon to use in all login dialogs
     * @param programName the name of the program to use in the title bar on all login dialogs
     * @return a new instance of the AuthManager
     * @throws UserManagerException if an error occurs while creating the users table
     */
    public static AuthManager getNewInstance(final Database db, final String appIconPath, final String programName) {
        if(instance == null) {
            instance = new AuthManager();
            instance.userRoleManager = UserRoleManager.getInstance();
            instance.userManager = new UserManager(db, appIconPath, programName);
            instance.sessionManager = instance.userManager.getSessionManager();
        }
        return instance;
    }

    /**
     * Returns the current instance of the Subject object for user session/roles/permissions management.
     * @return the current instance of the Subject object
     */
    public Subject getSubject() { return new Subject(getInstance()); }

    //region User Manager Methods
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
        return userManager.createUser(username, password, userRole);
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
        return userManager.createUser(username, password, userRole);
    }

    /**
     * Deletes the specified user.
     * @param username the username of the user to delete
     * @return true if no errors occur
     * @throws IllegalArgumentException if username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs while deleting user
     */
    public boolean deleteUser(final String username) { return userManager.deleteUser(username); }

    /**
     * Returns a UserAccount object representing the specified username.
     * @param username the username to lookup
     * @return a UserAccount object representing the specified username
     * @throws IllegalArgumentException if username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public UserAccount getUser(final String username) { return userManager.getUser(username); }

    /**
     * Returns the date and time the user was initially created.
     * @param username the username to lookup
     * @return the date and time the user was initially created as a LocalDateTime object
     * @throws IllegalArgumentException if username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public LocalDateTime getUserCreationDate(final String username) { return getUser(username).getCreationDate(); }

    /**
     * Returns the date and time the user was initially created as a formatted string.
     * @param username the username to lookup
     * @param format the pattern to use to format the timestamp
     * @return the date and time the user was initially created as a formatted string
     * @throws IllegalArgumentException if parameters are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public String getUserCreationDate(final String username, final String format) {
        if(format == null || format.trim().isEmpty()) {
            throw new IllegalArgumentException("Format Cannot Be Null Or Empty!");
        }
        return getUser(username).getCreationDate().format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Returns {@code true} if the specified user is locked and thus cannot be used to login.
     * @param username the username to lookup
     * @return {@code true} if the specified user is locked and thus cannot be used to login
     * @throws IllegalArgumentException if the username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    //TODO create UserManager implementation to reduce database activity
    public boolean isUserLocked(final String username) { return getUser(username).isLocked(); }

    /**
     * Locks the specified user preventing use in login.
     * @param username the username to lookup
     * @return true if no errors occur
     * @throws IllegalArgumentException if the username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public boolean lockUser(final String username) { return setLocked(username, true); }

    /**
     * Unlocks the specified user allowing use in login.
     * @param username the username to lookup
     * @return true if no errors occur
     * @throws IllegalArgumentException if the username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public boolean unlockUser(final String username) { return setLocked(username, false); }

    /**
     * Sets whether or not the specified user is locked and can be used to login.
     * @param username the username to lookup
     * @param status {@code true} if the specified user is locked and thus cannot be used to login
     * @return true if no errors occur
     * @throws IllegalArgumentException if parameters are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    private boolean setLocked(final String username, final boolean status) {
        return userManager.setLockStatus(username, status);
    }

    /**
     * Returns {@code true} if the specified user's password is expired and thus cannot be used to login.
     * @param username the username to lookup
     * @return {@code true} if the specified user's password is expired and thus cannot be used to login
     * @throws IllegalArgumentException if parameters are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    //TODO create UserManager implementation to reduce database activity
    public boolean isPasswordExpired(final String username) { return getUser(username).isPasswordExpired(); }

    /**
     * Returns {@code true} if the specified user's password has a set expiration date.
     * @param username the username to lookup
     * @return {@code true} if the specified user's password has a set expiration date
     * @throws IllegalArgumentException if parameters are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    //TODO create UserManager implementation to reduce database activity
    public boolean isPasswordSetToExpire(final String username) { return getUser(username).hasPasswordExpiration(); }

    /**
     * Returns the specified user's password expiration date as a LocalDateTime object.
     * @param username the username to lookup
     * @return the specified user's password expiration date as a LocalDateTime object
     * @throws IllegalArgumentException if parameters are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    //TODO create UserManager implementation to reduce database activity
    public LocalDateTime getPasswordExpirationDate(final String username) {
        return getUser(username).getPasswordExpirationDate();
    }

    /**
     * Returns the specified user's password expiration date as a formatted string.
     * @param username the username to lookup
     * @param format the pattern to use to format the timestamp
     * @return the specified user's password expiration date as a formatted string
     * @throws IllegalArgumentException if parameters are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    //TODO create UserManager implementation to reduce database activity
    public String getPasswordExpirationDate(final String username, final String format) {
        if(format == null || format.trim().isEmpty()) {
            throw new IllegalArgumentException("Format Cannot Be Null Or Empty!");
        }
        return getUser(username).getPasswordExpirationDate().format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Sets the specified user's password to expire preventing login after the specified date and time.
     * @param username the username to lookup
     * @param dateTime the expiration date and time to set
     * @return true if no errors occur
     * @throws IllegalArgumentException if parameters are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public boolean setPasswordExpirationDate(final String username, final LocalDateTime dateTime) {
        return userManager.setPasswordExpirationDate(username, dateTime);
    }

    /**
     * Sets the specified user's password to never expire.
     * @param username the username to lookup
     * @return true if no errors occur
     * @throws IllegalArgumentException if parameters are null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public boolean disablePasswordExpiration(final String username) {
        return userManager.disablePasswordExpiration(username);
    }

    /**
     * Checks if the specified username exists in the database.
     * @param username the username to check
     * @return true if the user exists
     * @throws IllegalArgumentException if username is null or empty
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public boolean userExists(final String username) { return userManager.userExists(username); }

    /**
     * Returns user role for the specified username.
     * @param username the username to lookup
     * @return the user role
     * @throws IllegalArgumentException if username is null or empty or if username does not exist
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if error occurs during lookup
     */
    public UserRole getUserRole(final String username) { return userManager.getUserRole(username); }

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
        return userManager.setUserRole(username, userRole);
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
        return userManager.setUserRole(username, userRole);
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
        return userManager.setPassword(username, password);
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
        return userManager.checkPasswordMatches(username, password);
    }

    /**
     * Returns a list of user accounts.
     * @return a HashSet of UserAccount objects representing the users in the users table
     * @throws TableNotFoundException if users table is missing
     * @throws UserManagerException if an error occurs during lookup
     */
    public HashSet<UserAccount> getUsersList() { return userManager.getUsersList(); }

    /**
     * Returns a list of the usernames in the database.
     * @return a list of the usernames in the database
     * @throws TableNotFoundException if users table is missing
     */
    public HashSet<String> getUsernameList() { return userManager.getUsernameList(); }

    /**
     * Returns the instance of the User Manager.
     * Most application programmers won't need to interact with the User Manager directly
     * as all it's methods are included in the Auth Manager.
     * @return the instance of the User Manager
     */
    public UserManager getUserManager() { return userManager; }
    //endregion User Manager Methods
    //region Session Manager Methods
    /** Enables basic event handlers with System.Out logging for all session and permission events.*/
    public void enableDebugLogging() {
        sessionManager.enableDebugLogging();
        PermissionManager.getInstance().enableDebugLogging();
    }

    /** Disables basic event handlers with System.Out logging for all session and permission events.*/
    public void diableDebugLogging() {
        sessionManager.disableDebugLogging();
        PermissionManager.getInstance().disableDebugLogging();
    }

    /**
     * Returns the path to the icon to use for all login dialogs.
     * @return the path to the icon to use for all login dialogs
     */
    public String getAppIconPath() { return sessionManager.getAppIconPath(); }

    /**
     * Sets the path to the icon to use for all login dialogs.
     * @param appIconPath the icon path to set
     */
    public void setAppIconPath(final String appIconPath) { sessionManager.setAppIconPath(appIconPath); }

    /**
     * Returns the name of the program to use in all login dialogs for the window title.
     * @return the name of the program to use in all login dialogs for the window title
     */
    public String getProgramName() { return sessionManager.getProgramName(); }

    /**
     * Sets the name of the program to use in all login dialogs for the window title.
     * @param programName the name of the program to set
     */
    public void setProgramName(final String programName) { sessionManager.setProgramName(programName); }

    /**
     * Returns the text to use for the error message when errors occur during login.
     * @return the text to use for the error message when errors occur during login
     */
    public LoginErrorMessages getLoginErrorMessages() { return sessionManager.getLoginErrorMessages(); }

    /**
     * Sets the text to use for the error message when when errors occur during login.
     * @param loginErrorMessages the error text to set
     */
    public void setLoginErrorMessages(final LoginErrorMessages loginErrorMessages) {
        sessionManager.setLoginErrorMessages(loginErrorMessages);
    }

    /**
     * Returns a new instance of the EventListeners class that contains methods for all session related event handlers.
     * @return a new instance of the EventListeners class
     */
    public SessionManager.EventListeners getEventListeners() { return sessionManager.getEventListeners(); }

    /**
     * Sets the maximum number of allowed sessions, under the multi session context, before login is disabled.
     * The default of -1 removes limit and 0 blocks all new sessions.
     * @param maxSessions the maximum number of allowed sessions
     */
    public void setMaxSessions(final int maxSessions) { sessionManager.setMaxSessions(maxSessions); }

    /**
     * Returns the maximum number of allowed sessions, under the multi session context, before login is disabled.
     * @return the maximum number of allowed sessions
     */
    public int getMaxSessions() { return sessionManager.getMaxSessions(); }

    /**
     * Returns the current number of logged in sessions under the multi session context.
     * @return the current number of logged in sessions under the multi session context
     */
    public int getSessionsCount() { return sessionManager.getSessionsCount(); }

    /**
     * Returns the current logged in sessions under the multi session context.
     * @return the current logged in sessions under the multi session context
     */
    public Map<String, Session> getSessions() { return sessionManager.getSessions(); }

    /** Returns the username of the currently logged in user under the single session context.
     * @return the username of the currently logged in user under the single session context
     */
    @Nullable
    public String getLoggedInUsername() { return sessionManager.getLoggedInUsername(); }

    /**
     * Returns true if a username is logged in under the single session context.
     * @return true if a username is logged in under the single session context
     */
    public boolean isUserLoggedIn() { return sessionManager.isUserLoggedIn(); }

    /**
     * Returns true if the specified username is logged in under the single session context.
     * @param username the username to lookup
     * @return true if the specified username is logged in
     */
    public boolean isUserLoggedIn(final String username) { return sessionManager.isUserLoggedIn(username); }

    /**
     * Returns true if the specified username is logged in under the specified context.
     * @param username the username to lookup
     * @param multiSession if true uses the multi session context, otherwise the single session context
     * @return true if the specified username is logged in
     * @since 1.5.0
     */
    public boolean isUserLoggedIn(final String username, final boolean multiSession) {
        return sessionManager.isUserLoggedIn(username, multiSession);
    }

    /**
     * Returns the current session for the currently logged in username under the single session context.
     * @return the current session for the currently logged in username
     */
    public Session getSession() { return sessionManager.getSession(); }

    /**
     * Returns the current session for the specified username under the single session context.
     * @param username the username to lookup
     * @return the current session for the specified username
     */
    public Session getSession(final String username) { return sessionManager.getSession(username); }

    /**
     * Returns the current session for the specified username, under the specified context.
     * @param username the username to lookup
     * @param multiSession if true uses the multi session context, otherwise the single session context
     * @return the current session for the specified username
     * @since 1.5.0
     */
    public Session getSession(final String username, final boolean multiSession) {
        return sessionManager.getSession(username, multiSession);
    }

    /**
     * Logs in a user under the single session context with the specified username,
     * no password checking is used.
     * @param username the username of the user
     * @return false if username does not exist or if session already exists
     */
    public boolean loginUser(final String username) { return sessionManager.loginUser(username); }

    /**
     * Logs in a user, under the specified context, with the specified username, no password checking is used.
     * @param username the username of the user
     * @param multiSession if true uses the multi session context, otherwise the single session context
     * @return false if username does not exist or if session already exists
     * or if multi user and if max sessions is reached or equals 0
     * @throws IllegalStateException if the user role is disabled
     * @throws ExpiredCredentialsException if the user credentials are expired
     * @throws LockedAccountException if the user account is locked
     * @since 1.5.0
     */
    public boolean loginUser(final String username, final boolean multiSession) {
        return sessionManager.loginUser(username, multiSession);
    }

    /**
     * Logs out the currently logged in user from the single session context and clears any set permissions.
     * NOTE: if user was deleted from the database while logged in, the getUser event method will return null.
     * @return false if a session does not exist
     */
    public boolean logoutUser() { return sessionManager.logoutUser(); }

    /**
     * Logs out the specified user from the single session context and clears any set permissions.
     * NOTE: if user was deleted from the database while logged in, the getUser event method will return null.
     * @param username the username of the user
     * @return false if the username does not exist or if a session does not exist for the username
     */
    public boolean logoutUser(final String username) { return sessionManager.logoutUser(username); }

    /**
     * Logs out the specified user, under the specified context, and,
     * if in single session context, clears any set permissions.
     * NOTE: if user was deleted from the database while logged in, the getUser event method will return null.
     * @param username the username of the user
     * @param multiSession if true uses the multi session context, otherwise the single session context
     * @return false if the username does not exist or if a session does not exist for the username
     * @since 1.5.0
     */
    public boolean logoutUser(final String username, final boolean multiSession) {
        return sessionManager.logoutUser(username, multiSession);
    }

    /**
     * Shows the login dialog window to log a user into the single session context.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if user logged in successfully and false if cancel button was pressed or login failed
     * @since 1.4.1
     */
    public boolean showLoginWindow(final boolean retryLoginOnFailure) {
        return sessionManager.showLoginWindow(retryLoginOnFailure);
    }

    /**
     * Returns the user role of the currently logged in user under the single user context.
     * @return the user role of the currently logged in user under the single user context
     */
    @Nullable
    public UserRole getLoggedInUserRole() { return sessionManager.getLoggedInUserRole(); }

    /**
     * Returns true if an admin user is currently logged in under the single user context.
     * @return true if an admin user is currently logged in under the single user context
     */
    public boolean isAdminLoggedIn() { return sessionManager.isAdminLoggedIn(); }

    /**
     * Requests an admin user to authenticate to override permissions using the single session context.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if admin override succeeded and false if cancel button was pressed or override failed
     * @since 1.4.1
     */
    public boolean getAdminOverride(final boolean retryLoginOnFailure) {
        return sessionManager.getAdminOverride(retryLoginOnFailure);
    }

    /**
     * Requests that the currently logged in username to re-login using the single session context.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if user verified successfully and false if cancel button was pressed or verification failed
     * @since 1.4.1
     */
    public boolean getUserVerification(final boolean retryLoginOnFailure) {
        return sessionManager.getUserVerification(retryLoginOnFailure);
    }

    /**
     * Checks that the logged in user, under the single session context,
     * is an admin and if false, requests an admin override.
     * @return true if admin permissions retrieved successfully
     * @since 1.4.1
     */
    public boolean requireAdmin() { return sessionManager.requireAdmin(); }

    /**
     * Checks that the logged in user, under the single session context, is an admin,
     * if true, prompts the admin to re-login and
     * if false, requests an admin override.
     * @return true if admin permissions retrieved successfully and admin verification succeeded
     * @since 1.4.1
     */
    public boolean requireAndVerifyAdmin() { return sessionManager.requireAndVerifyAdmin(); }

    /**
     * Returns the instance of the Session Manager.
     * Most application programmers won't need to interact with the Session Manager directly
     * as all it's methods are included in the Auth Manager.
     * @return the instance of the Session Manager
     */
    public SessionManager getSessionManager() { return sessionManager; }
    //endregion Session Manager Methods
    //region User Role Methods
    /**
     * Creates a new role and adds it to the list.
     * @param name the name of the new role
     * @return the new role as a UserRole object
     */
    public UserRole createRole(final String name) { return userRoleManager.createUserRole(name); }

    /**
     * Adds an existing role to the list.
     * @param role the role to be added
     */
    public void addExistingRole(final UserRole role) { userRoleManager.addExistingUserRole(role); }

    /**
     * Returns a list of the current existing roles.
     * @return a list of the current existing roles
     */
    public HashMap<String, UserRole> getRoles() { return userRoleManager.getUserRoles(); }

    /**
     * Returns the specified role.
     * @param name the name of the user role to return
     * @return the specified user role
     */
    public UserRole getRole(final String name) { return userRoleManager.getUserRole(name); }

    /**
     * Returns the instance of the User Role Manager.
     * Most application programmers won't need to interact with the User Role Manager directly
     * as all it's methods are included in the Auth Manager.
     * @return the instance of the User Role Manager
     */
    public UserRoleManager getUserRoleManager() { return userRoleManager; }

    /**
     * Checks if the specified username has the specified permission.
     * @param username the username to check
     * @param permissionName the name of the permission to check
     * @return true if the currently assigned username has the specified permission
     */
    public boolean userHasPermission(final String username, final String permissionName) {
        return getUserRole(username).hasPermission(permissionName);
    }

    /**
     * Checks if the specified username has ALL the specified permissions.
     * @param username the username to check
     * @param permissionNames a list of all the names of the permissions to check
     * @return true if the currently assigned username has ALL the specified permissions
     */
    public boolean userHasPermissions(final String username, final HashSet<String> permissionNames) {
        return permissionNames.stream().allMatch(permissionName -> userHasPermission(username, permissionName));
    }
    //endregion User Role Methods

    @Contract(" -> fail")
    @Override public Object clone() throws CloneNotSupportedException { throw new CloneNotSupportedException(); }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof AuthManager)) return false;

        final var authManager = (AuthManager) o;

        return new EqualsBuilder()
                .append(userManager, authManager.userManager)
                .append(sessionManager, authManager.sessionManager)
                .append(userRoleManager, authManager.userRoleManager)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userManager)
                .append(sessionManager)
                .append(userRoleManager)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userManager", userManager)
                .append("sessionManager", sessionManager)
                .append("userRoleManager", userRoleManager)
                .toString();
    }
}
