package com.jgcomptech.tools.events;

import java.util.EventListener;

/**
 * Handler for events of a specific class / type.
 * @param <T> the event class this handler can handle
 * @since 1.4.0
 */
@FunctionalInterface
public interface EventHandler<T extends Event> extends EventListener {
    /**
     * Invoked when a specific event of the type for which this handler is registered happens.
     * @param event the event which occurred
     */
    void handle(T event);
}
