package com.jgcomptech.tools.events;

import java.util.HashSet;

/**
 * This class represents a specific event type associated with an {@link Event}.
 * <p>
 * Event types form a hierarchy with the {@link EventType#ROOT} (equals to
 * {@link Event#ANY}) as its root. This is useful in event handler
 * registration where a single event handler can be registered to a
 * super event type and will be receiving its sub type events as well.
 * Note that you cannot construct two different EventType objects with the same
 * name and parent.
 * @param <T> the event class to which this type applies
 * @since 1.4.0
 */
public final class EventType<T extends Event> {
    /**
     * The root event type. All other event types are either direct or
     * indirect sub types of it. It is also the only event type which
     * has its super event type set to {@code null}.
     */
    public static final EventType<Event> ROOT = new EventType<>("EVENT", null);

    private HashSet<EventType<? extends T>> subTypes;

    private final EventType<? super T> superType;

    private final String name;

    /**
     * Constructs a new {@code EventType} with the specified name and the
     * {@code EventType.ROOT} as its super type.
     * @param name the name
     * @throws IllegalArgumentException if an EventType with the same name and
     * {@link EventType#ROOT}/{@link Event#ANY} as parent
     */
    public EventType(final String name) { this(ROOT, name); }

    /**
     * Constructs a new {@code EventType} with the specified super type and
     * the name set to {@code null}.
     * @param superType the event super type
     * @throws IllegalArgumentException if an EventType with "null" name and
     * under this supertype exists
     */
    public EventType(final EventType<? super T> superType) { this(superType, null); }

    /**
     * Constructs a new {@code EventType} with the specified super type and name.
     * @param superType the event super type
     * @param name the name
     * @throws IllegalArgumentException if an EventType with the same name and
     * superType exists
     */
    public EventType(final EventType<? super T> superType,
                     final String name) {
        if (superType == null) throw new IllegalArgumentException("Event super type must not be null!");
        this.superType = superType;
        this.name = name;
        superType.createSubType(this);
    }

    /**
     * Internal constructor that skips various checks.
     * @param name the name
     * @param superType the event super type
     */
    EventType(final String name,
              final EventType<? super T> superType) {
        this.superType = superType;
        this.name = name;
        if (superType != null) {
            if (superType.subTypes != null) {
                superType.subTypes
                        .removeIf(t -> name == null ? t.name == null : name.equals(t.name));
            }
            superType.createSubType(this);
        }
    }

    /**
     * Gets the super type of this event type. The returned value is
     * {@code null} only for the {@code EventType.ROOT}.
     * @return the super type
     */
    public EventType<? super T> getSuperType() { return superType; }

    /**
     * Gets the name of this event type.
     * @return the name
     */
    public String getName() { return name; }

    /**
     * Returns a string representation of this {@code EventType} object.
     * @return a string representation of this {@code EventType} object.
     */
    @Override
    public String toString() { return (name != null) ? name : super.toString(); }

    public EventType<T> createSubType(final String name) { return new EventType<>(this, name); }

    private void createSubType(final EventType<? extends T> subType) {
        if (subType == null) throw new IllegalArgumentException("Event super type must not be null!");
        if (subTypes == null) subTypes = new HashSet<>();
        subTypes.parallelStream().filter(t -> (t.name == null ? subType.name == null : t.name.equals(subType.name)))
                .forEach(t -> {
            throw new IllegalArgumentException("EventType \"" + subType + '"'
                    + "with parent \"" + subType.superType + "\" already exists");
        });
        subTypes.add(subType);
    }
}
