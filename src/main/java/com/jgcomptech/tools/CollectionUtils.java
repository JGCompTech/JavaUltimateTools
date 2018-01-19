package com.jgcomptech.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Contains methods for dealing with collections.
 * @since 1.4.0
 */
public final class CollectionUtils {
    /**
     * Converts a map into a delimited string value.
     * A "{@literal =}" separates the keys and values and a "{@literal &}" separates the key pairs.
     * @param stringMap map to convert
     * @return string representation of map
     */
    public static String convertMapToString(final Map<String, String> stringMap) {
        final StringBuilder sb = new StringBuilder();
        final char keySeparator = '=';
        final char pairSeparator = '&';
        for(final Map.Entry<String, String> pair:stringMap.entrySet())
        {
            sb.append(pair.getKey());
            sb.append(keySeparator);
            sb.append(pair.getValue());
            sb.append(pairSeparator);
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    /**
     * Converts a delimited string value into a map.
     * <p>Expects that "{@literal =}" separates the keys and values and a "{@literal &}" separates the key pairs.</p>
     * @param value map to convert
     * @return string representation of map
     */
    public static Map<String, String> convertStringToMap(final String value) {
        final Map<String, String> myMap = new HashMap<>();
        final String keySeparator = "=";
        final String pairSeparator = "&";
        final String[] pairs = value.split(pairSeparator);
        for(final String pair : pairs) {
            final String[] keyValue = pair.split(keySeparator);
            myMap.put(keyValue[0], keyValue[1]);
        }
        return myMap;
    }

    /**
     * Checks if an item userExists in a HashSet that matches the specified Predicate.
     * @param set the HashSet to check against
     * @param condition the Predicate to check
     * @param <T> the type of objects in the HashSet
     * @return true if condition is true
     * @throws IllegalArgumentException if HashSet or Predicate is null
     */
    public static <T> boolean doesItemExistInHashSet(final HashSet<T> set, final Predicate<T> condition) {
        if(set == null) throw new IllegalArgumentException("HashSet cannot be null!");
        if(condition == null) throw new IllegalArgumentException("Predicate cannot be null!");
        for(final T object : set) if (condition.test(object)) return true;
        return false;
    }

    /**
     * Checks if a value userExists in a HashMap that matches the specified Predicate.
     * @param map the HashMap to check against
     * @param condition the Predicate to check
     * @param <K> the type of the Key in the HashMap
     * @param <V> the type of the Value in the HashMap
     * @return true if condition is true
     * @throws IllegalArgumentException if HashMap or Predicate is null
     */
    public static <K, V> boolean doesItemExistInHashMap(final HashMap<K, V> map, final Predicate<V> condition) {
        if(map == null) throw new IllegalArgumentException("HashMap cannot be null!");
        if(condition == null) throw new IllegalArgumentException("Predicate cannot be null!");
        for(final Map.Entry<K, V> entry : map.entrySet()) if (condition.test(entry.getValue())) return true;
        return false;
    }

    /**
     * Returns item in HashSet that matches the specified Predicate.
     * @param set the HashSet to check against
     * @param condition the Predicate to check
     * @param <T> the type of objects in the HashSet
     * @return an Optional containing the matching object, Empty if not found
     * @throws IllegalArgumentException if HashSet or Predicate is null
     */
    public static <T> Optional<T> getItemInHashSet(final HashSet<T> set, final Predicate<T> condition) {
        if(set == null) throw new IllegalArgumentException("HashSet cannot be null!");
        if(condition == null) throw new IllegalArgumentException("Predicate cannot be null!");
        for(final T object : set) if (condition.test(object)) return Optional.of(object);
        return Optional.empty();
    }

    /**
     * Returns a HashSet of values in a HashMap that match the specified Predicate.
     * @param map the HashMap to check against
     * @param condition the Predicate to check
     * @param <K> the type of the Key in the HashMap
     * @param <V> the type of the Value in the HashMap
     * @return a HashSet of values matching the predicate, empty HashSet if no results found
     * @throws IllegalArgumentException if HashMap or Predicate is null
     */
    public static <K, V> HashSet<V> getValuesInHashMap(final HashMap<K, V> map, final Predicate<V> condition) {
        final HashSet<V> matchingValues = new HashSet<>();

        if(map == null) throw new IllegalArgumentException("HashMap cannot be null!");
        if(condition == null) throw new IllegalArgumentException("Predicate cannot be null!");

        map.forEach((key, value) -> { if(condition.test(value)) matchingValues.add(value); });

        return matchingValues;
    }

    /**
     * Returns the first item found in a HashMap that matches the specified Predicate.
     * @param map the HashMap to check against
     * @param condition the Predicate to check
     * @param <K> the type of the Key in the HashMap
     * @param <V> the type of the Value in the HashMap
     * @return an Optional containing the matching value, Empty if no results found
     * @throws IllegalArgumentException if HashMap or Predicate is null
     */
    public static <K, V> Optional<V> getValueInHashMap(final HashMap<K, V> map, final Predicate<V> condition) {
        V matchingValue = null;

        if(map == null) throw new IllegalArgumentException("HashMap cannot be null!");
        if(condition == null) throw new IllegalArgumentException("Predicate cannot be null!");

        for(final Map.Entry<K, V> entry : map.entrySet()) {
            if(condition.test(entry.getValue())) {
                matchingValue = entry.getValue();
                break;
            }
        }

        return matchingValue == null ? Optional.empty() : Optional.of(matchingValue);
    }

    /** Prevents instantiation of this utility class. */
    private CollectionUtils() { }
}
