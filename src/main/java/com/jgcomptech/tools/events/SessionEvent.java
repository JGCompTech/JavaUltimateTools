package com.jgcomptech.tools.events;

import com.jgcomptech.tools.authc.Session;
import com.jgcomptech.tools.authc.UserAccount;

import java.util.List;

/**
 * A {@link Event} for use with user {@link Session} objects.
 * @since 1.4.0
 */
public class SessionEvent extends Event {
    private Session session;
    public Session getSession() { return session; }
    private UserAccount user;
    public UserAccount getUser() { return user; }
    /** Common supertype for all permission event types. */
    public static final EventType<SessionEvent> ANY = new EventType<>(Event.ANY, "SESSION");

    public static final EventType<SessionEvent> SESSION_LOGIN_SUCCESS = ANY.createSubType("SESSION_LOGIN_SUCCESS");

    public static final EventType<SessionEvent> SESSION_LOGIN_FAILURE = ANY.createSubType("SESSION_LOGIN_FAILURE");

    public static final EventType<SessionEvent> SESSION_OPENED = ANY.createSubType("SESSION_OPENED");

    public static final EventType<SessionEvent> SESSION_CLOSED = ANY.createSubType("SESSION_CLOSED");

    public static final EventType<SessionEvent> MULTI_SESSION_OPENED = ANY.createSubType("MULTI_SESSION_OPENED");

    public static final EventType<SessionEvent> MULTI_SESSION_CLOSED = ANY.createSubType("MULTI_SESSION_CLOSED");

    public static final EventType<SessionEvent> SESSION_ADMIN_OVERRIDE_STARTED
            = ANY.createSubType("SESSION_ADMIN_OVERRIDE_STARTED");

    public static final EventType<SessionEvent> SESSION_ADMIN_OVERRIDE_SUCCESS
            = ANY.createSubType("SESSION_ADMIN_OVERRIDE_SUCCESS");

    public static final EventType<SessionEvent> SESSION_ADMIN_OVERRIDE_FAILURE
            = ANY.createSubType("SESSION_ADMIN_OVERRIDE_FAILURE");

    public static final EventType<SessionEvent> SESSION_USER_VERIFY_STARTED
            = ANY.createSubType("SESSION_USER_VERIFY_STARTED");

    public static final EventType<SessionEvent> SESSION_USER_VERIFY_SUCCESS
            = ANY.createSubType("SESSION_USER_VERIFY_SUCCESS");

    public static final EventType<SessionEvent> SESSION_USER_VERIFY_FAILURE
            = ANY.createSubType("SESSION_USER_VERIFY_FAILURE");

    /**
     * Construct a new {@code Event} with the specified event target and type.
     * @param target    the event target to associate with the event
     * @param eventType the event type
     */
    public SessionEvent(final EventTarget<? extends Event> target,
                        final EventType<? extends Event> eventType) {
        super(target, eventType);
    }

    /**
     * Construct a new {@code Event} with the specified event target, type and args.
     * @param target    the event target to associate with the event
     * @param eventType the event type
     * @param args      arguments to make available to the EventHandler
     */
    public SessionEvent(final EventTarget<? extends Event> target,
                        final EventType<? extends Event> eventType,
                        final List<Object> args) {
        super(target, eventType, args);
    }

    /**
     * Gets the event type of this event. Objects of the same {@code Event}
     * class can have different event types. These event types further specify
     * what kind of event occurred.
     *
     * @return the event type
     */
    @Override
    @SuppressWarnings("unchecked")
    public EventType<? extends SessionEvent> getEventType() {
        return (EventType<? extends SessionEvent>) super.getEventType();
    }

    /**
     * Creates and returns a copy of this event with the specified event source and target.
     *
     * @param newSource the new source of the copied event
     * @param newTarget the new target of the copied event
     * @return the event copy with the new source and target
     */
    @Override
    public SessionEvent copyFor(final Object newSource, final EventTarget<? extends Event> newTarget) {
        return (SessionEvent) super.copyFor(newSource, newTarget);
    }

    /**
     * Fires the event with the specified source and user.
     * @param source    the event source which sent the event
     * @param user   the user account object to pass to the EventHandler
     */
    public void fireEvent(final Object source, final UserAccount user) {
        this.user = user;
        setSource(source);
        super.fireEvent(source);
    }

    /**
     * Fires the event with the specified source, user and session.
     * @param source    the event source which sent the event
     * @param user      the user object to pass to the EventHandler
     * @param session   the session object to pass to the EventHandler
     */
    public void fireEvent(final Object source, final UserAccount user, final Session session) {
        this.session = session;
        this.user = user;
        setSource(source);
        super.fireEvent(source);
    }
}
