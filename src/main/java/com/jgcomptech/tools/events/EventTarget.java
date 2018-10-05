package com.jgcomptech.tools.events;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows object event handler registration and forwards received
 * events to the appropriate registered event handlers.
 * @param <T> the event type to use for the target
 * @since 1.4.0
 */
public class EventTarget<T extends Event> {
    private final Map<EventType<? extends Event>, EventHandler<T>> eventHandlers = new HashMap<>();

    /**
     * Sets the specified singleton handler. There can only be one such handler specified at a time.
     * @param eventType the event type to associate with the given eventHandler
     * @param eventHandler the handler to register, or null to unregister
     */
    public final void addEventHandler(final EventType<? extends Event> eventType,
                                final EventHandler<T> eventHandler) { eventHandlers.put(eventType, eventHandler); }

    /**
     * Removes the singleton handler assigned to the specified Event Type.
     * @param eventType the event type to associate with the given eventHandler
     */
    public final void removeEventHandler(final EventType<? extends T> eventType) { eventHandlers.remove(eventType); }

    /**
     * Returns the singleton handler assigned to the specified Event Type.
     * @param eventType the event type
     * @return the singleton handler assigned to the specified Event Type
     */
    public final EventHandler<T> getEventHandler(final EventType<? extends T> eventType) {
        return eventHandlers.get(eventType); }

    /**
     * Fires the handle method in all registered EventHandlers.
     * @param event the event
     * @param eventType the event type
     */
    @SuppressWarnings("unchecked")
    public final void fire(final Event event, final EventType<? extends Event> eventType) {
        for (final var entry : eventHandlers.entrySet()) {
            if(entry.getKey().equals(eventType)) {
                entry.getValue().handle((T) event);
            }
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof EventTarget)) return false;

        final var eventTarget = (EventTarget<?>) o;

        return new EqualsBuilder()
                .append(eventHandlers, eventTarget.eventHandlers)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(eventHandlers)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("eventHandlers", eventHandlers)
                .toString();
    }
}
