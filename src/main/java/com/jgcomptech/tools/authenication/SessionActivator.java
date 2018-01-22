package com.jgcomptech.tools.authenication;

import com.jgcomptech.tools.events.EventHandler;
import com.jgcomptech.tools.events.EventManager;
import com.jgcomptech.tools.events.EventTarget;
import com.jgcomptech.tools.events.SessionEvent;
import com.jgcomptech.tools.permissions.PermissionManager;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides SessionManager and MultiSessionManager with methods needed to control user sessions.
 * @since 1.4.0
 */
public class SessionActivator extends EventTarget<SessionEvent> {
    private final boolean multiUser;
    protected final EventManager eventManager;
    protected final UserManager userManager;
    protected SessionEvent eventSessionOpened;
    protected SessionEvent eventSessionClosed;
    protected Session currentSession;
    protected final Map<String, Session> sessions = new HashMap<>();
    protected int maxSessions = -1;

    /**
     * Creates a new instance of the Session Activator.
     * @param userManager the instance of the UserManager
     * @param multiUser if true allows multiple sessions, false allows only one single session
     */
    protected SessionActivator(final UserManager userManager, final boolean multiUser) {
        if(userManager == null) { throw new IllegalArgumentException("User Manager Cannot Be Null!"); }
        this.userManager = userManager;
        this.multiUser = multiUser;
        eventManager = EventManager.getInstance();
        createEvents();
    }

    /**
     * Returns true if the specified username is logged in.
     * @param username the username to lookup
     * @return true if the specified username is logged in
     */
    public boolean isUserLoggedIn(final String username) {
        return multiUser ? sessions.containsKey(username) : currentSession.getUsername().equals(username);
    }

    /**
     * Logs in a user with the specified username and user role, no password checking is used.
     * @param username the username of the user
     * @return false if username does not exist or if session already exists
     * or if multi user and if max sessions is reached or equals 0
     */
    protected final boolean saLoginUser(final String username) {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty!");
        }

        if(isUserLoggedIn(username)) return false;

        if(!multiUser && currentSession != null
                || multiUser && (maxSessions == 0 || maxSessions != -1 && sessions.size() >= maxSessions))
        { return false; }

        if (userManager.userExists(username)) {
            UserAccount account = userManager.getUser(username);
            if (account.getUserRole().isEnabled()) {
                Session newSession = new Session(username, account.getUserRole());
                if(multiUser) sessions.put(username, newSession);
                else {
                    PermissionManager.getInstance().loadPermissions(account.getUserRole());
                    currentSession = newSession;
                }
                eventSessionOpened.fireEvent(this, account, newSession);
                return true;
            } else throw new IllegalStateException("User Role " + account.getUserRole() + " Is Disabled!");
        }
        return false;
    }

    /**
     * Logs out the specified user and, if single user, clears any set permissions.
     * NOTE: if user was deleted from the database while logged in, the getUser event method will return null.
     * @param username the username of the user
     * @return false if username or if session does not exist
     */
    protected final boolean saLogoutUser(final String username) {
        if(multiUser && (username == null || username.isEmpty())) {
            throw new IllegalArgumentException("Username cannot be null or empty!");
        }

        if(multiUser && !isUserLoggedIn(username) || !multiUser && currentSession == null) return false;

        String usernameObj = null;
        Session sessionObj = null;
        boolean sessionFound = false;

        if(multiUser && !sessions.isEmpty() && sessions.containsKey(username)) {
            usernameObj = username;
            sessionObj = sessions.get(username);
            sessionFound = true;
        } else if(!multiUser) {
            PermissionManager.getInstance().loadPermissions(false);
            usernameObj = currentSession.getUsername();
            sessionObj = currentSession;
            sessionFound = true;
        }

        if(sessionFound) {
            if(userManager.userExists(usernameObj)) {
                UserAccount user = userManager.getUser(usernameObj);
                eventSessionClosed.fireEvent(this, user, sessionObj);
            } else eventSessionClosed.fireEvent(this, null, sessionObj);
            if(multiUser) sessions.remove(usernameObj);
            else currentSession = null;
            return true;
        } else return false;
    }

    /**
     * Sets the event handler that will fire when the session is opened.
     * The currently assigned event handler is removed if the parameter is null.
     * The specified event handler is assigned to the sessionOpened or multiSessionOpened event in the EventManager.
     * @param e the event handler
     */
    protected final void saSetOnSessionOpened(final EventHandler<SessionEvent> e) {
        if(multiUser) {
            if(e == null) removeEventHandler(SessionEvent.MULTI_SESSION_OPENED);
            else addEventHandler(SessionEvent.MULTI_SESSION_OPENED, e);
        } else {
            if (e == null) removeEventHandler(SessionEvent.SESSION_OPENED);
            else addEventHandler(SessionEvent.SESSION_OPENED, e);
        }
    }

    /**
     * Sets the event handler that will fire when the session is closed.
     * The currently assigned event handler is removed if the parameter is null.
     * The specified event handler is assigned to the sessionClosed or multiSessionClosed event in the EventManager.
     * @param e the event handler
     */
    protected final void saSetOnSessionClosed(final EventHandler<SessionEvent> e) {
        if(multiUser) {
            if(e == null) removeEventHandler(SessionEvent.MULTI_SESSION_CLOSED);
            else addEventHandler(SessionEvent.MULTI_SESSION_CLOSED, e);
        } else {
            if (e == null) removeEventHandler(SessionEvent.SESSION_CLOSED);
            else addEventHandler(SessionEvent.SESSION_CLOSED, e);
        }
    }

    /** Creates the needed event objects. */
    private void createEvents() {
        try {
            eventSessionOpened = eventManager.registerNewEvent(
                    multiUser ? "multiSessionOpened" : "sessionOpened",
                    SessionEvent.class,
                    this,
                    multiUser ? SessionEvent.MULTI_SESSION_OPENED : SessionEvent.SESSION_OPENED);
        } catch(Exception e) {
            if(multiUser) throw new IllegalStateException("multiSessionOpened Event Failed To Load!");
            else throw new IllegalStateException("sessionOpened Event Failed To Load!");
        }
        try {
            eventSessionClosed = eventManager.registerNewEvent(
                    multiUser ? "multiSessionClosed" : "sessionClosed",
                    SessionEvent.class,
                    this,
                    multiUser ? SessionEvent.MULTI_SESSION_CLOSED : SessionEvent.SESSION_CLOSED);
        } catch(Exception e) {
            if(multiUser) throw new IllegalStateException("multiSessionClosed Event Failed To Load!");
            else throw new IllegalStateException("sessionClosed Event Failed To Load!");
        }
    }

    @Contract(" -> fail")
    @Override public Object clone() throws CloneNotSupportedException { throw new CloneNotSupportedException(); }
}
