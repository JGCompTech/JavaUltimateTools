package com.jgcomptech.tools.events;

import java.util.List;

/**
 * An {@link Event} representing some type of action.
 * @since 1.4.0
 */
public class ActionEvent extends Event {
    /** The only valid EventType for the ActionEvent. */
    public static final EventType<ActionEvent> ACTION = new EventType<>(Event.ANY, "ACTION");

    /** Common supertype for all action event types. */
    public static final EventType<ActionEvent> ANY = ACTION;

    /**
     * Construct a new {@code ActionEvent} with the specified event target.
     * All ActionEvents have their type set to {@code ACTION}.
     * @param target    the event target to associate with the event
     */
    public ActionEvent(final EventTarget<? extends ActionEvent> target) { super(target, ACTION); }

    /**
     * Construct a new {@code ActionEvent} with the specified event target and type.
     * All ActionEvents have their type set to {@code ACTION}.
     * @param target    the event target to associate with the event
     * @param eventType the event type
     */
    public ActionEvent(final EventTarget<? extends ActionEvent> target,
                       final EventType<? extends Event> eventType) {
        super(target, eventType);
    }

    /**
     * Construct a new {@code ActionEvent} with the specified event target, type and args.
     * All ActionEvents have their type set to {@code ACTION}.
     * @param target    the event target to associate with the event
     * @param eventType the event type
     * @param args arguments to make available to the EventHandler
     */
    public ActionEvent(final EventTarget<? extends ActionEvent> target,
                       final EventType<? extends Event> eventType,
                       final List<Object> args) {
        super(target, eventType, args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<? extends ActionEvent> getEventType() {
        return (EventType<? extends ActionEvent>) super.getEventType();
    }

    @Override
    public ActionEvent copyFor(final Object newSource, final EventTarget<? extends Event> newTarget) {
        return (ActionEvent) super.copyFor(newSource, newTarget);
    }
}
