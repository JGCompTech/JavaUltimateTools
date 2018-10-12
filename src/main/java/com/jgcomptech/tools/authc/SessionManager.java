package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.authz.PermissionManager;
import com.jgcomptech.tools.events.EventHandler;
import com.jgcomptech.tools.events.EventManager;
import com.jgcomptech.tools.events.EventTarget;
import com.jgcomptech.tools.events.SessionEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Manages a login sessions to allow a users to login to your application.
 * @since 1.4.0
 * @since 1.5.0 merged with MultiSessionManager
 */
public final class SessionManager extends EventTarget<SessionEvent> {
    private String appIconPath;
    private String programName;
    private final EventManager eventManager;
    private final UserManager userManager;
    private Session currentSession;
    private final Map<String, Session> multiUserSessions = new HashMap<>();
    private int maxSessions = -1;
    private LoginErrorMessages loginErrorMessages;
    private SessionEvent eventLoginSuccess,
            eventLoginFailure,
            eventAdminOverrideStarted,
            eventAdminOverrideSuccess,
            eventAdminOverrideFailure,
            eventUserVerifyStarted,
            eventUserVerifySuccess,
            eventUserVerifyFailure,
            eventSessionOpened,
            eventSessionClosed,
            eventMultiSessionOpened,
            eventMultiSessionClosed;

    /**
     * Creates an instance of the SessionManager.
     * Also creates the sessionLoginSuccess, sessionLoginFailure, multiSessionOpened and multiSessionClosed events.
     * @param userManager the User Manager to use for the session
     */
    public SessionManager(final UserManager userManager) { this(userManager, null); }

    /**
     * Creates an instance of the SessionManager.
     * Also creates the sessionLoginSuccess, sessionLoginFailure, multiSessionOpened and multiSessionClosed events.
     * @param userManager the User Manager to use for the session
     * @param appIconPath the path to the icon to use for all login dialogs
     */
    public SessionManager(final UserManager userManager, final String appIconPath) {
        this(userManager, appIconPath, null);
    }

    /**
     * Creates an instance of the SessionManager.
     * Also creates the sessionLoginSuccess, sessionLoginFailure, multiSessionOpened and multiSessionClosed events.
     * @param userManager the User Manager to use for the session
     * @param appIconPath the path to the icon to use for all login dialogs
     * @param programName the name of the program to use in all login dialogs for the window title
     */
    public SessionManager(final UserManager userManager, final String appIconPath, final String programName) {
        if(userManager == null) { throw new IllegalArgumentException("User Manager Cannot Be Null!"); }
        this.userManager = userManager;
        eventManager = EventManager.getInstance();
        this.appIconPath = appIconPath;
        this.programName = programName;
        this.loginErrorMessages = new LoginErrorMessages();
        createSessionEvents();
    }

    /**
     * Returns the path to the icon to use for all login dialogs.
     * @return the path to the icon to use for all login dialogs
     */
    public String getAppIconPath() { return appIconPath; }

    /**
     * Sets the path to the icon to use for all login dialogs.
     * @param appIconPath the icon path to set
     */
    public void setAppIconPath(final String appIconPath) { this.appIconPath = appIconPath; }

    /**
     * Returns the name of the program to use in all login dialogs for the window title.
     * @return the name of the program to use in all login dialogs for the window title
     */
    public String getProgramName() { return programName; }

    /**
     * Sets the name of the program to use in all login dialogs for the window title.
     * @param programName the name of the program to set
     */
    public void setProgramName(final String programName) { this.programName = programName; }

    /**
     * Enables basic default error messages to be used for the login dialog.
     * @since 1.5.0
     */
    public void enableDefaultErrorMessages() {
        this.loginErrorMessages = new LoginErrorMessages()
                .setIncorrectCredentialsError("Invalid Username Or Password, Please Try Again!")
                .setLockedAccountError("Unable To Complete Login, Account Is Locked!")
                .setExpiredCredentialsError("Unable To Complete Login, Password Is Expired!")
                .setExcessiveAttemptsError("Account Is Locked Due To Too Many Invalid Login Attempts!");
    }

    /**
     * Returns the text to use for the error message when errors occur during login.
     * @return the text to use for the error message when errors occur during login
     */
    public LoginErrorMessages getLoginErrorMessages() { return loginErrorMessages; }

    /**
     * Sets the text to use for the error message when errors occur during login
     * @param loginErrorMessages the error messages to set
     */
    public void setLoginErrorMessages(final LoginErrorMessages loginErrorMessages) {
        this.loginErrorMessages = loginErrorMessages;
    }

    /** Creates all the session related events. */
    private void createSessionEvents() {
        try {
            eventSessionOpened = eventManager.registerNewEvent(
                    "sessionOpened",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_OPENED);
        } catch(final Exception e) {
            throw new IllegalStateException("sessionOpened Event Failed To Load!");
        }
        try {
            eventSessionClosed = eventManager.registerNewEvent(
                    "sessionClosed",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_CLOSED);
        } catch(final Exception e) {
            throw new IllegalStateException("sessionClosed Event Failed To Load!");
        }
        try {
            eventMultiSessionOpened = eventManager.registerNewEvent(
                    "multiSessionOpened",
                    SessionEvent.class,
                    this,
                    SessionEvent.MULTI_SESSION_OPENED);
        } catch(final Exception e) {
            throw new IllegalStateException("multiSessionOpened Event Failed To Load!");
        }
        try {
            eventMultiSessionClosed = eventManager.registerNewEvent(
                    "multiSessionClosed",
                    SessionEvent.class,
                    this,
                    SessionEvent.MULTI_SESSION_CLOSED);
        } catch(final Exception e) {
            throw new IllegalStateException("multiSessionClosed Event Failed To Load!");
        }
        try {
            eventLoginSuccess = eventManager.registerNewEvent("sessionLoginSuccess",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_LOGIN_SUCCESS);
        } catch(final Exception e) {
            throw new IllegalStateException("sessionLoginSuccess Event Failed To Load!");
        }
        try {
            eventLoginFailure = eventManager.registerNewEvent("sessionLoginFailure",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_LOGIN_FAILURE);
        } catch(final Exception e) {
            throw new IllegalStateException("sessionLoginFailure Event Failed To Load!");
        }
        try {
            eventAdminOverrideStarted = eventManager.registerNewEvent(
                    "adminOverrideStarted",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_ADMIN_OVERRIDE_STARTED);
        } catch (final Exception e) {
            throw new IllegalStateException("adminOverrideStarted Event Failed To Load!");
        }
        try {
            eventAdminOverrideSuccess = eventManager.registerNewEvent(
                    "adminOverrideSuccess",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_ADMIN_OVERRIDE_SUCCESS);
        } catch (final Exception e) {
            throw new IllegalStateException("adminOverrideSuccess Event Failed To Load!");
        }
        try {
            eventAdminOverrideFailure = eventManager.registerNewEvent(
                    "adminOverrideFailure",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_ADMIN_OVERRIDE_FAILURE);
        } catch (final Exception e) {
            throw new IllegalStateException("adminOverrideFailure Event Failed To Load!");
        }
        try {
            eventUserVerifyStarted = eventManager.registerNewEvent(
                    "userVerifyStarted",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_USER_VERIFY_STARTED);
        } catch (final Exception e) {
            throw new IllegalStateException("userVerifyStarted Event Failed To Load!");
        }
        try {
            eventUserVerifySuccess = eventManager.registerNewEvent(
                    "userVerifySuccess",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_USER_VERIFY_SUCCESS);
        } catch (final Exception e) {
            throw new IllegalStateException("userVerifySuccess Event Failed To Load!");
        }
        try {
            eventUserVerifyFailure = eventManager.registerNewEvent(
                    "userVerifyFailure",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_USER_VERIFY_FAILURE);
        } catch (final Exception e) {
            throw new IllegalStateException("userVerifyFailure Event Failed To Load!");
        }
    }

    /**
     * Enables basic event handlers with System.Out logging for all events.
     * @since 1.5.0
     */
    public void enableDebugLogging() {
        getEventListeners().setOnLoginState(
                success -> {
                    final var username = success.getUser().getUsername();
                    System.out.println("EVENT: Access Granted " + username + "! (Opening Session)");
                },
                failure -> System.out.println("EVENT: Invalid Username Or Password!"));

        getEventListeners().setOnSessionState(
                opened -> {
                    final var username = opened.getSession().getUsername();
                    System.out.println("EVENT: " + username + " Logged In Successfully! (Session Opened)");
                },
                closed -> {
                    final var username = closed.getSession().getUsername();
                    System.out.println("EVENT: " + username + " Logged Out Successfully! (Session Closed)");
                });

        getEventListeners().setOnMultiSessionOpened(e -> {
            final var username = e.getSession().getUsername();
            System.out.println("EVENT: " + username + " Logged In Successfully! (Session Opened)");
        });

        getEventListeners().setOnMultiSessionClosed(e -> {
            final var username = e.getSession().getUsername();
            System.out.println("EVENT: " + username + " Logged Out Successfully! (Session Closed)");
        });

        getEventListeners().setOnAdminOverrideStarted(e -> System.out.println("Requesting Admin Override..."));

        getEventListeners().setOnAdminOverrideSuccess(e -> {
            System.out.println("Admin Permissions Granted! Continuing...");
        });

        getEventListeners().setOnAdminOverrideFailure(e -> {
            System.out.println("Override Request Failed!");
        });

        getEventListeners().setOnUserVerifyStarted(e -> {
            System.out.println("Requesting User Verification...");
        });

        getEventListeners().setOnUserVerifySuccess(e -> {
            System.out.println("Account Verified! Continuing...");
        });

        getEventListeners().setOnUserVerifyFailure(e -> {
            System.out.println("Verification Request Failed!");
        });
    }

    /**
     * Disables basic event handlers with System.Out logging for all events.
     * @since 1.5.0
     */
    public void disableDebugLogging() {
        getEventListeners().setOnLoginState(null, null);

        getEventListeners().setOnSessionState(null, null);

        getEventListeners().setOnMultiSessionOpened(null);

        getEventListeners().setOnMultiSessionClosed(null);

        getEventListeners().setOnAdminOverrideStarted(null);

        getEventListeners().setOnAdminOverrideSuccess(null);

        getEventListeners().setOnAdminOverrideFailure(null);

        getEventListeners().setOnUserVerifyStarted(null);

        getEventListeners().setOnUserVerifySuccess(null);

        getEventListeners().setOnUserVerifyFailure(null);
    }

    /**
     * Returns a new instance of the EventListeners class that contains methods for all session related event handlers.
     * @return a new instance of the EventListeners class
     * @since 1.5.0
     */
    public EventListeners getEventListeners() { return new EventListeners(); }

    /**
     * Contains methods for all session related event handlers.
     * @since 1.5.0
     */
    public final class EventListeners {
        /**
         * Sets the event handler that will fire when the session is opened.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the sessionOpened event in the EventManager.
         * @param e the event handler
         */
        public void setOnSessionOpened(final EventHandler<SessionEvent> e) {
            if (e == null) removeEventHandler(SessionEvent.SESSION_OPENED);
            else addEventHandler(SessionEvent.SESSION_OPENED, e);
        }

        /**
         * Sets the event handler that will fire when the session is closed.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the sessionClosed event in the EventManager.
         * @param e the event handler
         */
        public void setOnSessionClosed(final EventHandler<SessionEvent> e) {
            if (e == null) removeEventHandler(SessionEvent.SESSION_CLOSED);
            else addEventHandler(SessionEvent.SESSION_CLOSED, e);
        }

        /**
         * Sets the event handlers that will fire when the session state changes.
         * The currently assigned event handlers are removed if the parameters are null.
         * The opened event handler is assigned to the sessionOpened event in the EventManager.
         * The closed event handler is assigned to the sessionClosed event in the EventManager.
         * @param opened the opened event handler
         * @param closed the closed event handler
         */
        public void setOnSessionState(final EventHandler<SessionEvent> opened,
                                      final EventHandler<SessionEvent> closed) {
            if (opened == null) removeEventHandler(SessionEvent.SESSION_OPENED);
            else addEventHandler(SessionEvent.SESSION_OPENED, opened);
            if (closed == null) removeEventHandler(SessionEvent.SESSION_CLOSED);
            else addEventHandler(SessionEvent.SESSION_CLOSED, closed);
        }

        /**
         * Sets the event handler that will fire when the session is opened.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the multiSessionOpened event in the EventManager.
         * @param e the event handler
         */
        public void setOnMultiSessionOpened(final EventHandler<SessionEvent> e) {
            if(e == null) removeEventHandler(SessionEvent.MULTI_SESSION_OPENED);
            else addEventHandler(SessionEvent.MULTI_SESSION_OPENED, e);
        }

        /**
         * Sets the event handler that will fire when the session is closed.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the multiSessionClosed event in the EventManager.
         * @param e the event handler
         */
        public void setOnMultiSessionClosed(final EventHandler<SessionEvent> e) {
            if(e == null) removeEventHandler(SessionEvent.MULTI_SESSION_CLOSED);
            else addEventHandler(SessionEvent.MULTI_SESSION_CLOSED, e);
        }

        /**
         * Sets the event handler that will fire when a user login succeeds.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the sessionLoginSuccess event in the EventManager.
         * @param e the event handler
         */
        public void setOnLoginSuccess(final EventHandler<SessionEvent> e) {
            if(e == null) removeEventHandler(SessionEvent.SESSION_LOGIN_SUCCESS);
            else addEventHandler(SessionEvent.SESSION_LOGIN_SUCCESS, e);
        }

        /**
         * Sets the event handler that will fire when a user login fails.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the sessionLoginFailure event in the EventManager.
         * @param e the event handler
         */
        public void setOnLoginFailure(final EventHandler<SessionEvent> e) {
            if(e == null) removeEventHandler(SessionEvent.SESSION_LOGIN_FAILURE);
            else addEventHandler(SessionEvent.SESSION_LOGIN_FAILURE, e);
        }

        /**
         * Sets the event handlers that will fire when a user login state changes.
         * The currently assigned event handlers are removed if the parameters are null.
         * The success event handler is assigned to the sessionLoginSuccess event in the EventManager.
         * The failure event handler is assigned to the sessionLoginFailure event in the EventManager.
         * @param success the success event handler
         * @param failure the failure event handler
         */
        public void setOnLoginState(final EventHandler<SessionEvent> success,
                                    final EventHandler<SessionEvent> failure) {
            if(success == null) removeEventHandler(SessionEvent.SESSION_LOGIN_SUCCESS);
            else addEventHandler(SessionEvent.SESSION_LOGIN_SUCCESS, success);
            if(failure == null) removeEventHandler(SessionEvent.SESSION_LOGIN_FAILURE);
            else addEventHandler(SessionEvent.SESSION_LOGIN_FAILURE, failure);
        }

        /**
         * Sets the event handler that will fire when an admin override starts.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the adminOverrideStarted event in the EventManager.
         * @param e the event handler
         */
        public void setOnAdminOverrideStarted(final EventHandler<SessionEvent> e) {
            if(e == null) removeEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_STARTED);
            else addEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_STARTED, e);
        }

        /**
         * Sets the event handler that will fire when an admin override succeeds.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the adminOverrideSuccess event in the EventManager.
         * @param e the event handler
         */
        public void setOnAdminOverrideSuccess(final EventHandler<SessionEvent> e) {
            if(e == null) removeEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_SUCCESS);
            else addEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_SUCCESS, e);
        }

        /**
         * Sets the event handler that will fire when an admin override succeeds.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the adminOverrideFailure event in the EventManager.
         * @param e the event handler
         */
        public void setOnAdminOverrideFailure(final EventHandler<SessionEvent> e) {
            if(e == null) removeEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_FAILURE);
            else addEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_FAILURE, e);
        }

        /**
         * Sets the event handler that will fire when a user verify starts.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the userVerifyStarted event in the EventManager.
         * @param e the event handler
         */
        public void setOnUserVerifyStarted(final EventHandler<SessionEvent> e) {
            if(e == null) removeEventHandler(SessionEvent.SESSION_USER_VERIFY_STARTED);
            else addEventHandler(SessionEvent.SESSION_USER_VERIFY_STARTED, e);
        }

        /**
         * Sets the event handler that will fire when a user verify succeeds.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the userVerifySucceeded event in the EventManager.
         * @param e the event handler
         */
        public void setOnUserVerifySuccess(final EventHandler<SessionEvent> e) {
            if(e == null) removeEventHandler(SessionEvent.SESSION_USER_VERIFY_SUCCESS);
            else addEventHandler(SessionEvent.SESSION_USER_VERIFY_SUCCESS, e);
        }

        /**
         * Sets the event handler that will fire when a user verify fails.
         * The currently assigned event handler is removed if the parameter is null.
         * The specified event handler is assigned to the userVerifyFailure event in the EventManager.
         * @param e the event handler
         */
        public void setOnUserVerifyFailure(final EventHandler<SessionEvent> e) {
            if(e == null) removeEventHandler(SessionEvent.SESSION_USER_VERIFY_FAILURE);
            else addEventHandler(SessionEvent.SESSION_USER_VERIFY_FAILURE, e);
        }
    }

    /**
     * Returns a new instance of the Events class that contains methods for all session related events.
     * @return a new instance of the Events class
     */
    Events getEvents() { return new Events(); }

    /** Contains methods for all session related events. */
    final class Events {
        public SessionEvent getEventLoginSuccess() {
            return eventLoginSuccess;
        }

        public SessionEvent getEventLoginFailure() {
            return eventLoginFailure;
        }

        public SessionEvent getEventAdminOverrideStarted() {
            return eventAdminOverrideStarted;
        }

        public SessionEvent getEventAdminOverrideSuccess() {
            return eventAdminOverrideSuccess;
        }

        public SessionEvent getEventAdminOverrideFailure() {
            return eventAdminOverrideFailure;
        }

        public SessionEvent getEventUserVerifyStarted() {
            return eventUserVerifyStarted;
        }

        public SessionEvent getEventUserVerifySuccess() {
            return eventUserVerifySuccess;
        }

        public SessionEvent getEventUserVerifyFailure() {
            return eventUserVerifyFailure;
        }

        public SessionEvent getEventSessionOpened() {
            return eventSessionOpened;
        }

        public SessionEvent getEventSessionClosed() {
            return eventSessionClosed;
        }

        public SessionEvent getEventMultiSessionOpened() {
            return eventMultiSessionOpened;
        }

        public SessionEvent getEventMultiSessionClosed() {
            return eventMultiSessionClosed;
        }
    }

    /**
     * Sets the maximum number of allowed sessions, under the multi session context, before login is disabled.
     * The default of -1 removes limit and 0 blocks all new sessions.
     * @param maxSessions the maximum number of allowed sessions
     */
    public void setMaxSessions(final int maxSessions) { this.maxSessions = maxSessions; }

    /**
     * Returns the maximum number of allowed sessions, under the multi session context, before login is disabled.
     * @return the maximum number of allowed sessions
     */
    public int getMaxSessions() { return maxSessions; }

    /**
     * Returns the current number of logged in sessions under the multi session context.
     * @return the current number of logged in sessions under the multi session context
     */
    public int getSessionsCount() { return multiUserSessions.size(); }

    /**
     * Returns the current logged in sessions under the multi session context.
     * @return the current logged in sessions under the multi session context
     */
    public Map<String, Session> getSessions() { return Collections.unmodifiableMap(multiUserSessions); }

    /** Returns the username of the currently logged in user under the single session context.
     * @return the username of the currently logged in user under the single session context
     */
    @Nullable
    public String getLoggedInUsername() { return currentSession != null ? currentSession.getUsername() : null; }

    /**
     * Returns true if a username is logged in under the single session context.
     * @return true if a username is logged in under the single session context
     */
    public boolean isUserLoggedIn() { return currentSession != null; }

    /**
     * Returns true if the specified username is logged in under the single session context.
     * @param username the username to lookup
     * @return true if the specified username is logged in
     */
    public boolean isUserLoggedIn(final String username) { return isUserLoggedIn(username, false); }

    /**
     * Returns true if the specified username is logged in under the specified context.
     * @param username the username to lookup
     * @param multiSession if true uses the multi session context, otherwise the single session context
     * @return true if the specified username is logged in
     * @since 1.5.0
     */
    public boolean isUserLoggedIn(final String username, final boolean multiSession) {
        return multiSession ? multiUserSessions.containsKey(username)
                : currentSession != null && currentSession.getUsername().equals(username);
    }

    /**
     * Returns the current session for the currently logged in username under the single session context.
     * @return the current session for the currently logged in username
     */
    public Session getSession() { return currentSession; }

    /**
     * Returns the current session for the specified username under the single session context.
     * @param username the username to lookup
     * @return the current session for the specified username
     */
    public Session getSession(final String username) { return getSession(username, false); }

    /**
     * Returns the current session for the specified username, under the specified context.
     * @param username the username to lookup
     * @param multiSession if true uses the multi session context, otherwise the single session context
     * @return the current session for the specified username
     * @since 1.5.0
     */
    public Session getSession(final String username, final boolean multiSession) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Null Or Empty!");
        }
        if(multiSession) return multiUserSessions.get(username);
        else return currentSession != null && currentSession.getUsername().equals(username) ? currentSession : null;
    }

    /**
     * Logs in a user under the single session context with the specified username,
     * no password checking is used.
     * @param username the username of the user
     * @return false if username does not exist or if session already exists
     * @throws IllegalStateException if the user role is disabled
     * @throws ExpiredCredentialsException if the user credentials are expired
     * @throws LockedAccountException if the user account is locked
     */
    public boolean loginUser(final String username)
            throws IllegalStateException, ExpiredCredentialsException, LockedAccountException {
        return loginUser(username, false);
    }

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
    //TODO create a method for multi-user context that accepts a UsernameLoginToken
    public boolean loginUser(final String username, final boolean multiSession)
            throws IllegalStateException, ExpiredCredentialsException, LockedAccountException {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty!");
        }

        if(isNewSessionAllowed(username, multiSession) && userManager.userExists(username)) {
            final var account = userManager.getUser(username);
            if(!account.isLocked()) {
                if(!account.isPasswordExpired()) {
                    eventLoginSuccess.fireEvent(this, account);
                    final var role = userManager.getUserRole(username);
                    if (role.isEnabled()) {
                        final var newSession = new Session(username, role);
                        if (multiSession) multiUserSessions.put(username, newSession);
                        else {
                            PermissionManager.getInstance().loadPermissions(role);
                            currentSession = newSession;
                        }
                        if (multiSession) eventMultiSessionOpened.fireEvent(this, account, newSession);
                        else eventSessionOpened.fireEvent(this, account, newSession);
                        return true;
                    } else throw new IllegalStateException("User Role " + role + " Is Disabled!");
                } else throw new ExpiredCredentialsException("User " + username + "'s password has expired!");
            } else throw new LockedAccountException("User " + username + " is locked!");
        }
        return false;
    }

    /**
     * Returns true if creation of a new session for the specified username is allowed under the specified context.
     * @param username the username to lookup
     * @param multiSession if true checks status under the multi session context,
     *                     otherwise checks under the single session context
     * @return true if creation of a new session is allowed under the specified context
     * @since 1.5.0
     */
    public boolean isNewSessionAllowed(final String username, final boolean multiSession) {
        return !isUserLoggedIn(username)
                && (!multiSession || (maxSessions != 0 && multiUserSessions.size() != maxSessions));
    }

    /**
     * Logs out the currently logged in user from the single session context and clears any set permissions.
     * NOTE: if user was deleted from the database while logged in, the getUser event method will return null.
     * @return false if a session does not exist
     */
    public boolean logoutUser() { return logoutUser(null, false); }

    /**
     * Logs out the specified user from the single session context and clears any set permissions.
     * NOTE: if user was deleted from the database while logged in, the getUser event method will return null.
     * @param username the username of the user
     * @return false if the username does not exist or if a session does not exist for the username
     */
    public boolean logoutUser(final String username) { return logoutUser(username, false); }

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
        if(multiSession && (username == null || username.trim().isEmpty())) {
            throw new IllegalArgumentException("Username cannot be null or empty!");
        } else if(username != null && username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty!");
        } else if(username == null && !isUserLoggedIn()) return false;

        final var targetUsername = username == null ? currentSession.getUsername() : username;
        if(isUserLoggedIn(targetUsername, multiSession)) {
            final var session = multiSession ? multiUserSessions.get(targetUsername) : currentSession;
            UserAccount user = null;
            if(userManager.userExists(targetUsername)) user = userManager.getUser(targetUsername);
            if(multiSession) {
                eventMultiSessionClosed.fireEvent(this, user, session);
                multiUserSessions.remove(targetUsername);
            }
            else {
                PermissionManager.getInstance().loadPermissions(false);
                eventSessionClosed.fireEvent(this, user, session);
                currentSession = null;
            }
            return true;
        } else return false;
    }

    /**
     * Shows the login dialog window to log a user into the single session context.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if user logged in successfully and false if cancel button was pressed or login failed
     * @since 1.4.1
     * @since 1.5.0 changed implementation
     */
    public boolean showLoginWindow(final boolean retryLoginOnFailure) {
        return new LoginDialogBuilder(this, userManager, loginErrorMessages)
                .setAttemptNewSession(true)
                .setRetryLoginOnFailure(retryLoginOnFailure)
                .setDialogTitle(programName + " - Login Required")
                .setDialogIconPath((appIconPath != null && !appIconPath.trim().isEmpty()) ? appIconPath : "")
                .show();
    }

    /**
     * Returns the user role of the currently logged in user under the single user context.
     * @return the user role of the currently logged in user under the single user context
     */
    @Nullable
    public UserRole getLoggedInUserRole() { return currentSession != null ? currentSession.getUserRole() : null; }

    /**
     * Returns true if an admin user is currently logged in under the single user context.
     * @return true if an admin user is currently logged in under the single user context
     */
    public boolean isAdminLoggedIn() {
        return isUserLoggedIn()
                && Objects.equals(getLoggedInUserRole(), UserRoleManager.SystemUserRoles.ADMIN.getRole());
    }

    /**
     * Shows the login dialog window requesting an admin user to authenticate.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if admin override succeeded and false if cancel button was pressed or override failed
     * @since 1.4.1
     * @since 1.5.0 changed implementation
     */
    private boolean requestAdminLogin(final boolean retryLoginOnFailure) {
        return new LoginDialogBuilder(this, userManager, loginErrorMessages)
                .setRetryLoginOnFailure(retryLoginOnFailure)
                .setDialogTitle(programName != null && !programName.trim().isEmpty()
                        ? programName + " - Admin Override Required"
                        : "Admin Override Required")
                .setDialogIconPath((appIconPath != null && !appIconPath.trim().isEmpty()) ? appIconPath : "")
                .addPredicate(user -> Objects.equals(userManager.getUserRole(user.getUsername()),
                        UserRoleManager.SystemUserRoles.ADMIN.getRole()))
                .show();
    }

    /**
     * Shows the login dialog window requesting the currently logged in username
     * to re-login under the single user context.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if user verified successfully and false if cancel button was pressed or verification failed
     * @since 1.4.1
     * @since 1.5.0 changed implementation
     */

    private boolean requestUserLogin(final boolean retryLoginOnFailure) {
        return new LoginDialogBuilder(this, userManager, loginErrorMessages)
                .setRetryLoginOnFailure(retryLoginOnFailure)
                .setDialogTitle(programName != null && !programName.trim().isEmpty()
                        ? programName + " - User Verification Required"
                        : "User Verification Required")
                .setDialogIconPath((appIconPath != null && !appIconPath.trim().isEmpty()) ? appIconPath : "")
                .addPredicate(user -> user.getUsername().equals(currentSession.getUsername()))
                .show();
    }

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
        eventAdminOverrideStarted.fireEvent(this);
        var adminLoggedIn = isUserLoggedIn()
                && Objects.equals(getLoggedInUserRole(), UserRoleManager.SystemUserRoles.ADMIN.getRole());
        if(!adminLoggedIn) {
            if(requestAdminLogin(retryLoginOnFailure)) {
                eventAdminOverrideSuccess.fireEvent(this);
                adminLoggedIn = true;
            } else eventAdminOverrideFailure.fireEvent(this);
        } else eventAdminOverrideSuccess.fireEvent(this, userManager.getUser(getLoggedInUsername()));
        return adminLoggedIn;
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
        eventUserVerifyStarted.fireEvent(this);
        final var result = requestUserLogin(retryLoginOnFailure);
        if(result) eventUserVerifySuccess.fireEvent(this, userManager.getUser(getLoggedInUsername()));
        else eventUserVerifyFailure.fireEvent(this, userManager.getUser(getLoggedInUsername()));
        return result;
    }

    /**
     * Checks that the logged in user, under the single session context,
     * is an admin and if false, requests an admin override.
     * @return true if admin permissions retrieved successfully
     * @since 1.4.1
     */
    public boolean requireAdmin() { return isAdminLoggedIn() || getAdminOverride(true); }

    /**
     * Checks that the logged in user, under the single session context, is an admin,
     * if true, prompts the admin to re-login and
     * if false, requests an admin override.
     * @return true if admin permissions retrieved successfully and admin verification succeeded
     * @since 1.4.1
     */
    public boolean requireAndVerifyAdmin() {
        return isAdminLoggedIn()
                ? getUserVerification(true)
                : getAdminOverride(true);
    }

    @Contract(" -> fail")
    @Override public Object clone() throws CloneNotSupportedException { throw new CloneNotSupportedException(); }
}
