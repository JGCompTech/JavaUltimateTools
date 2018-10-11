package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.Contract;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Manages all tasks related to the currently logged in user account.
 * @since 1.5.0
 */
public final class Subject {
    private final AuthManager authManager;
    private String username;
    private boolean remembered;
    private UsernamePasswordToken token;
    private Duration lastSessionDuration;

    public Subject(final AuthManager authManager) {
        this.authManager = authManager;
        lastSessionDuration = Duration.ZERO;
    }

    /**
     * Attempts to login the specified user account from the specified token under the single-session context.
     * @param token the username and password token to use for login
     * @return true if login succeeds, false if login fails
     */
    @Contract("null -> fail")
    public boolean login(final UsernamePasswordToken token) { return login(token, false); }

    /**
     * Attempts to login the specified user account from the previously saved token under the single-session context.
     * The token is only saved if the previously supplied token had rememberMe set to true.
     * @return true if login succeeds, false if login fails
     * @throws CredentialsException if no token was previously saved
     */
    public boolean login() {
        if(token == null || token.getUsername() == null || token.getPassword() == null) {
            throw new CredentialsException("Subject Credential Token Not Saved!");
        }
        return login(token, false);
    }

    /**
     * Attempts to login the specified user account from the specified token.
     * @param token the username and password token to use for login
     * @param multiSession if true, logs in the user under the multi-session context,
     *                     or if false, under the single-session context
     * @return true if login succeeds, false if login fails
     */
    @Contract("null, _ -> fail")
    public boolean login(final UsernamePasswordToken token, final boolean multiSession) {
        if(token == null) throw new CredentialsException("Login Token cannot be null!");
        if(token.getUsername() == null || token.getPassword() == null) {
            throw new CredentialsException("Login Token Username and Password cannot be null!");
        }
        if(authManager.userExists(token.getUsername())) {
            final var account = authManager.getUser(token.getUsername());
            username = token.getUsername();
            if (getSession(multiSession) == null) {
                if (authManager.checkPasswordMatches(token.getUsername(), new String(token.getPassword()))) {
                    if(authManager.loginUser(token.getUsername())) {
                        if(token.isRememberMe()) {
                            remembered = true;
                            this.token = new UsernamePasswordToken(token.getUsername(), token.getPassword());
                        }
                        token.clear();
                        return true;
                    }
                }
                authManager.getSessionManager().getEvents().getEventLoginFailure().fireEvent(this, account);
                token.clear();
                return false;
            } else {
                token.clear();
                throw new ConcurrentAccessException("User Account Already Logged In!");
            }
        }
        authManager.getSessionManager().getEvents().getEventLoginFailure().fireEvent(this);
        token.clear();
        return false;
    }

    /**
     * Attempts to logout the currently logged in user under the single-session context.
     * @return true if logout succeeds, false if logout fails
     */
    public boolean logout() { return logout(false); }

    /**
     * Attempts to logout the currently logged in user.
     * @param multiSession if true, logs out the user under the multi-session context,
     *      *                     or if false, under the single-session context
     * @return true if logout succeeds, false if logout fails
     */
    public boolean logout(final boolean multiSession) {
        assertNotAnonymous();
        if(!isAuthenticated(multiSession)) return false;
        else {
            lastSessionDuration = authManager.getSession(username, multiSession).getDuration();
            if(authManager.logoutUser(username, multiSession)) {
                if(!remembered) {
                    username = null;
                    if(token != null) {
                        token.clear();
                        token = null;
                    }
                }
                return true;
            } else {
                //This shouldn't ever happen since we already checked above
                return false;
            }
        }
    }

    /**
     * Returns a Duration object containing the elapsed time the last session was open.
     * @return a Duration object containing the elapsed time the last session was open
     */
    @Contract(pure = true)
    public Duration getLastSessionDuration() { return lastSessionDuration; }

    /**
     * Returns a string of the elapsed time the last session was open in the format 00:00:00.
     * @return a string of the elapsed time the last session was open in the format 00:00:00
     */
    public String getLastSessionDurationString() {
        final var millis = lastSessionDuration.toMillis();

        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    /**
     * Returns a string of the elapsed time the last session was open in the format 00:00:00.0000.
     * @return a string of the elapsed time the last session was open in the format 00:00:00.0000
     */
    public String getLastSessionDurationStringFull() {
        final var millis = lastSessionDuration.toMillis();

        return String.format("%02d:%02d:%02d.%04d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                TimeUnit.MILLISECONDS.toMillis(millis) -
                        TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)));
    }

    /**
     * Checks if the subject has an assigned username to manage.
     * @return true if the subject has an assigned username to manage
     */
    public boolean isAnonymous() { return username == null || username.trim().isEmpty(); }

    /**
     * Checks if the subject's assigned username is currently logged in.
     * @return true if the subject's assigned username is currently logged in
     */
    public boolean isAuthenticated() { return username != null && getSession() != null; }

    /**
     * Checks if the subject's assigned username is currently logged in.
     * @param multiSession if true, checks under the multi-session context,
     *                     or if false, under the single-session context
     * @return true if the subject's assigned username is currently logged in
     */
    public boolean isAuthenticated(final boolean multiSession) {
        return username != null && getSession(multiSession) != null;
    }

    /**
     * Returns the Session object of the current logged in session.
     * @return the Session object of the current logged in session
     */
    public Session getSession() { return getSession(false); }

    /**
     * Returns the Session object of the current logged in session.
     * @param multiSession if true, checks under the multi-session context,
     *                     or if false, under the single-session context
     * @return the Session object of the current logged in session
     */
    public Session getSession(final boolean multiSession) {
        assertNotAnonymous();
        return authManager.getSession(username, multiSession);
    }

    /**
     * Checks if the current username and token are set to be saved after logout.
     * @return true if the current username and token are set to be saved after logout
     */
    @Contract(pure = true)
    public boolean isRemembered() { return remembered; }

    /**
     * Returns subject's the currently assigned username.
     * @return subject's the currently assigned username
     */
    @Contract(pure = true)
    public String getUsername() { return username; }

    /**
     * Sets a new password for the user using BCrypt password hashing.
     * @param password the new password
     * @return true if password is changed successfully
     */
    public boolean setPassword(final String password) {
        assertAuthenticated();
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null!");
        }
        return authManager.setPassword(username, password);
    }

    /**
     * Returns the user role of the currently assigned username.
     * @return the user role of the currently assigned username
     */
    public UserRole getUserRole() {
        assertNotAnonymous();
        return authManager.getUserRole(username);
    }

    /**
     * Checks if the currently assigned username has the specified permission.
     * @param permissionName the name of the permission to check
     * @return true if the currently assigned username has the specified permission
     */
    public boolean hasPermission(final String permissionName) {
        assertNotAnonymous();
        return authManager.userHasPermission(username, permissionName);
    }

    /**
     * Checks if the currently assigned username has ALL the specified permissions.
     * @param permissionNames a list of all the names of the permissions to check
     * @return true if the currently assigned username has ALL the specified permissions
     */
    public boolean hasPermissions(final HashSet<String> permissionNames) {
        return authManager.userHasPermissions(username, permissionNames);
    }

    /**
     * Sets the user role  of the currently assigned username.
     * @param userRole the system user role to change to
     * @return true if no errors occurred
     */
    public boolean setUserRole(final UserRoleManager.SystemUserRoles userRole) {
        assertNotAnonymous();
        return authManager.setUserRole(username, userRole);
    }

    /**
     * Sets the user role  of the currently assigned username.
     * @param userRole the name of the user role to change to
     * @return true if no errors occurred
     */
    public boolean setUserRole(final String userRole) {
        assertNotAnonymous();
        return authManager.setUserRole(username, userRole);
    }

    /**
     * Returns the date and time the user was initially created.
     * @return the date and time the user was initially created as a LocalDateTime object
     */
    public LocalDateTime getUserCreationDate() {
        assertNotAnonymous();
        return authManager.getUserCreationDate(username);
    }

    /**
     * Returns the date and time the user was initially created.
     * @param format the pattern to use to format the timestamp
     * @return the date and time the user was initially created as a formatted string
     */
    public String getUserCreationDate(final String format) {
        assertNotAnonymous();
        return authManager.getUserCreationDate(username, format);
    }

    /**
     * Returns {@code true} if the user is locked and thus cannot be used to login.
     * @return {@code true} if the specified user is locked and thus cannot be used to login
     */
    public boolean isUserLocked() {
        assertNotAnonymous();
        return authManager.isUserLocked(username);
    }

    /**
     * Locks the user preventing use in login.
     * @return {@code true} if no errors occurred
     */
    public boolean lockUser() {
        assertNotAnonymous();
        return authManager.lockUser(username);
    }

    /**
     * Unlocks the user allowing use in login.
     * @return {@code true} if no errors occurred
     */
    public boolean unlockUser() {
        assertNotAnonymous();
        return authManager.unlockUser(username);
    }

    /**
     * Returns {@code true} if the user's password is expired and thus cannot be used to login.
     * @return {@code true} if the user's password is expired and thus cannot be used to login
     */
    public boolean isPasswordExpired() {
        assertNotAnonymous();
        return authManager.isPasswordExpired(username);
    }

    /**
     * Returns {@code true} if the user's password has a set expiration date.
     * @return {@code true} if the user's password has a set expiration date.
     */
    public boolean isPasswordSetToExpire() {
        assertNotAnonymous();
        return authManager.isPasswordSetToExpire(username);
    }

    /**
     * Returns the user's password expiration date as a LocalDateTime object.
     * @return the user's password expiration date as a LocalDateTime object
     */
    public LocalDateTime getPasswordExpirationDate() {
        assertNotAnonymous();
        return authManager.getPasswordExpirationDate(username);
    }

    /**
     * Returns the user's password expiration date as a formatted string.
     * @param format the pattern to use to format the timestamp
     * @return the user's password expiration date as a formatted string
     */
    public String getPasswordExpirationDate(final String format) {
        assertNotAnonymous();
        return authManager.getPasswordExpirationDate(username, format);
    }

    /**
     * Sets the user's password to expire preventing login after the specified date and time.
     * @param dateTime the expiration date and time to set
     * @return true if no errors occur
     */
    public boolean setPasswordExpirationDate(final LocalDateTime dateTime) {
        assertNotAnonymous();
        return authManager.setPasswordExpirationDate(username, dateTime);
    }

    /**
     * Sets the user's password to never expire.
     * @return true if no errors occur
     */
    public boolean disablePasswordExpiration() {
        assertNotAnonymous();
        return authManager.disablePasswordExpiration(username);
    }

    /**
     * Checks to see if the specified password matches the stored password in the database.
     * @param password the password to check against
     * @return true if the passwords match
     */
    public boolean checkPasswordMatches(final String password)  {
        assertNotAnonymous();
        return authManager.checkPasswordMatches(username, password);
    }

    /** Throws an UnknownAccountException if the Subject is anonymous. */
    private void assertNotAnonymous() { if(isAnonymous()) throw new UnknownAccountException("Subject is anonymous!"); }

    /** Throws an UnauthenticatedException if the Subject is not authenticated. */
    private void assertAuthenticated() {
        if(!isAuthenticated(false) && !isAuthenticated(true)) {
            throw new UnauthenticatedException("Subject is not authenticated!");
        }
    }
}
