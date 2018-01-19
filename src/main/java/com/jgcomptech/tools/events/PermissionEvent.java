package com.jgcomptech.tools.events;

import com.jgcomptech.tools.permissions.Permission;

import java.util.List;

/**
 * A {@link Event} for use with user {@link Permission} objects.
 * @since 1.4.0
 */
public class PermissionEvent extends Event {
    private Permission permission;
    public Permission getPermission() { return permission; }
    /** Common supertype for all permission event types. */
    public static final EventType<PermissionEvent> ANY = new EventType<>(Event.ANY, "PERMISSION");

    public static final EventType<PermissionEvent> PERMISSION_ENABLED = ANY.createSubType("PERMISSION_ENABLED");

    public static final EventType<PermissionEvent> PERMISSION_DISABLED = ANY.createSubType("PERMISSION_DISABLED");

    public static final EventType<PermissionEvent> PERMISSIONS_APPLIED = ANY.createSubType("PERMISSIONS_APPLIED");

    public static final EventType<PermissionEvent> PERMISSIONS_ALL_ENABLED =
            ANY.createSubType("PERMISSIONS_ALL_ENABLED");

    public static final EventType<PermissionEvent> PERMISSIONS_ALL_DISABLED =
            ANY.createSubType("PERMISSIONS_ALL_DISABLED");

    /**
     * Construct a new {@code Event} with the specified event target, type and args.
     * @param target    the event target to associate with the event
     * @param eventType the event type
     * @param args      arguments to make available to the EventHandler
     */
    public PermissionEvent(final EventTarget<? extends Event> target,
                           final EventType<? extends Event> eventType,
                           final List<Object> args) {
        super(target, eventType, args);
    }

    @Override
    public EventType<? extends PermissionEvent> getEventType() {
        return (EventType<? extends PermissionEvent>) super.getEventType();
    }

    @Override
    public PermissionEvent copyFor(final Object newSource, final EventTarget<? extends Event> newTarget) {
        return (PermissionEvent) super.copyFor(newSource, newTarget);
    }

    /**
     * Fires the event with the specified source and permission.
     *
     * @param source      the event source which sent the event
     * @param permission  the permission object to pass to the EventHandler
     */
    public void fireEvent(final Object source, final Permission permission) {
        this.permission = permission;
        setSource(source);
       super.fireEvent(source);
    }
}
