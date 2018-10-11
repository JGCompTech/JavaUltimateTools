package com.jgcomptech.tools.events;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Base class for custom events. Each event has associated an event source,
 * event target and an event type.
 * @since 1.4.0
 */
public class Event implements Cloneable, Serializable {

    private static final long serialVersionUID = 20121107L;
    /** Common supertype for all event types. */
    public static final EventType<Event> ANY = EventType.ROOT;

    /** The object on which the Event initially occurred. */
    private transient Object source;

    /** Type of the event. */
    private EventType<? extends Event> eventType;

    /** Event target that defines the path through which the event will travel when posted. */
    private transient EventTarget<? extends Event> target;

    /** Whether this event has been consumed by any filter or handler. */
    private boolean consumed;

    /** Event arguments to make available to the EventHandler. */
    private final List<Object> args = new ArrayList<>();

    /**
     * Construct a new {@code Event} with the specified event target.
     * @param target the event target to associate with the event
     */
    public Event(final EventTarget<? extends Event> target) {
        if (target == null) {
            throw new IllegalArgumentException("Event target cannot be null!");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null!");
        }
        this.target = target;
        eventType = ANY;
    }

    /**
     * Construct a new {@code Event} with the specified event target and type.
     * @param target the event target to associate with the event
     * @param eventType the event type
     */
    public Event(final EventTarget<? extends Event> target,
                 final EventType<? extends Event> eventType) {
        if (target == null) {
            throw new IllegalArgumentException("Event target cannot be null!");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null!");
        }
        this.target = target;
        this.eventType = eventType;
    }

    /**
     * Construct a new {@code Event} with the specified event target, type and args.
     * @param target the event target to associate with the event
     * @param eventType the event type
     * @param args arguments to make available to the EventHandler
     */
    public Event(final EventTarget<? extends Event> target,
                 final EventType<? extends Event> eventType,
                 final List<Object> args) {
        if(target == null) {
            throw new IllegalArgumentException("Event target cannot be null!");
        }
        if(eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null!");
        }
        if(args == null) {
            throw new IllegalArgumentException("Args cannot be null!");
        }
        this.target = target;
        this.eventType = eventType;
        this.args.clear();
        this.args.addAll(args);
    }

    /**
     * Returns the event target of this event.
     * The event target specifies the path through which the event will travel when posted.
     * @return the event target
     */
    public final EventTarget<? extends Event> getTarget() { return target; }

    /**
     * Sets the event target of this event, only available to other event objects.
     * @param target the event target
     */
    protected final void setTarget(final EventTarget<? extends Event> target) { this.target = target; }

    /**
     * Gets the event type of this event.
     * Objects of the same {@code Event} class can have different event types.
     * These event types further specify what kind of event occurred.
     * @return the event type
     */
    public EventType<? extends Event> getEventType() { return eventType; }

    /**
     * Creates and returns a copy of this event with the specified event source and target.
     * @param newSource the new source of the copied event
     * @param newTarget the new target of the copied event
     * @return the event copy with the new source and target
     */
    public Event copyFor(final Object newSource, final EventTarget<? extends Event> newTarget) {
        if (newSource == null) {
            throw new IllegalArgumentException("Event source cannot be null!");
        }
        if (newTarget == null) {
            throw new IllegalArgumentException("Event target cannot be null!");
        }
        final var newEvent = (Event) clone();

        newEvent.source = newSource;
        newEvent.target = newTarget;
        newEvent.consumed = false;

        return newEvent;
    }

    /**
     * The object on which the Event initially occurred.
     * @return the object on which the Event initially occurred.
     */
    public final Object getSource() { return source; }

    /**
     * Sets the event source, only available to other event objects.
     * @param source the source to set
     */
    protected void setSource(final Object source) { this.source = source; }

    /**
     * Indicates whether this {@code Event} has been consumed by any filter or handler.
     * @return {@code true} if this {@code Event} has been consumed, {@code false} otherwise
     */
    public final boolean isConsumed() { return consumed; }

    /** Marks this {@code Event} as consumed. This stops its further propagation. */
    public final void consume() { consumed = true; }

    /**
     * Returns the Event arguments.
     * @return the Event arguments
     */
    public final List<Object> getArgs() { return Collections.unmodifiableList(args); }

    /**
     * Creates and returns a copy of this {@code Event}.
     * @return a new instance of {@code Event} with all values copied from this {@code Event}.
     */
    @Override
    public final Object clone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            // we implement Cloneable, this shouldn't happen
            throw new IllegalStateException("Can't clone Event");
        }
    }

    /**
     * Fires the event with the specified source.
     * @param source the event source which sent the event
     */
    public final void fireEvent(final Object source) {
        this.source = source;
        if(!consumed) target.fire(this, eventType);
        args.clear();
    }

    /**
     * Fires the event with the specified source and args.
     * @param source the event source which sent the event
     * @param args a list of parameters to pass to the EventHandler
     */
    public final void fireEvent(final Object source, final Object... args) {
        this.source = source;
        this.args.addAll(Arrays.asList(args));
        if(!consumed) target.fire(this, eventType);
        this.args.clear();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Event)) return false;

        final var event = (Event) o;

        return new EqualsBuilder()
                .append(consumed, event.consumed)
                .append(source, event.source)
                .append(eventType, event.eventType)
                .append(target, event.target)
                .append(args, event.args)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(source)
                .append(eventType)
                .append(target)
                .append(consumed)
                .append(args)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("source", source)
                .append("eventType", eventType)
                .append("target", target)
                .append("consumed", consumed)
                .append("args", args)
                .toString();
    }
}
