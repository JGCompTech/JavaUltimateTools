package com.jgcomptech.tools.authenication;

import com.jgcomptech.tools.events.EventHandler;
import com.jgcomptech.tools.events.SessionEvent;

import java.util.Map;

/**
 * Manages multiple login sessions to allow users to login to your application.
 * @since 1.4.0
 */
public class MultiSessionManager extends SessionActivator {
    /**
     * Creates an instance of the MultiSessionManager.
     * Also creates the multiSessionOpened and multiSessionClosed events.
     * @param userManager the User Manager to use for the session
     */
    public MultiSessionManager(final UserManager userManager) { super(userManager, true); }

    /**
     * Logs in a user with the specified username and user role, no password checking is used.
     * Fires the multiSessionOpened event allowing the getUser and getSession methods
     * to be called by the assigned EventHandler.
     * @param username the username of the user
     * @return false if username does not exist or if session already exists
     * or if max sessions is reached or equals 0
     */
    public boolean loginUser(final String username) { return saLoginUser(username); }

    /**
     * Logs out the specified user and clears any set permissions.
     * Fires the multiSessionClosed event allowing the getUser and getSession methods
     * to be called by the assigned EventHandler.
     * NOTE: if user was deleted from the database while logged in, the getUser event method will return null.
     * @param username the username of the user
     * @return false if username or session does not exist
     */
    public boolean logoutUser(final String username) { return saLogoutUser(username); }

    /**
     * Sets the event handler that will fire when the session is opened.
     * The currently assigned event handler is removed if the parameter is null.
     * The specified event handler is assigned to the multiSessionOpened event in the EventManager.
     * @param e the event handler
     */
    public void setOnSessionOpened(final EventHandler<SessionEvent> e) { saSetOnSessionOpened(e); }

    /**
     * Sets the event handler that will fire when the session is closed.
     * The currently assigned event handler is removed if the parameter is null.
     * The specified event handler is assigned to the multiSessionClosed event in the EventManager.
     * @param e the event handler
     */
    public void setOnSessionClosed(final EventHandler<SessionEvent> e) { saSetOnSessionClosed(e); }

    /**
     * Sets the maximum number of allowed sessions before login is disabled.
     * The default of -1 removes limit and 0 blocks all new sessions.
     * @param maxSessions the maximum number of allowed sessions
     */
    public void setMaxSessions(final int maxSessions) { this.maxSessions = maxSessions; }

    /**
     * Returns the maximum number of allowed sessions before login is disabled.
     * @return the maximum number of allowed sessions
     */
    public int getMaxSessions() { return maxSessions; }

    /**
     * Returns the current number of logged in sessions.
     * @return the current number of logged in sessions
     */
    public int getSessionsCount() { return sessions.size(); }

    /**
     * Returns the current logged in sessions.
     * @return the current logged in sessions
     */
    public Map<String, Session> getSessions() { return sessions; }
}
