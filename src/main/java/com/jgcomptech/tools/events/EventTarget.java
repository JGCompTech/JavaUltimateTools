package com.jgcomptech.tools.events;

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
    public final void fire(final Event event, final EventType<? extends Event> eventType) {
        for (Map.Entry<EventType<? extends Event>, EventHandler<T>> entry : eventHandlers.entrySet()) {
            if(entry.getKey().equals(eventType)) {
                entry.getValue().handle((T) event);
            }
        }
    }
}
