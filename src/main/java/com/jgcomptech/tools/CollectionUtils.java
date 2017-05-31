package com.jgcomptech.tools;

import java.util.*;
import java.util.function.Predicate;

public final class CollectionUtils {
    /**
     * Converts a map into a delimited string value.
     * A '=' seperates the keys and values and a '&' seperates the key pairs.
     * @param stringMap map to convert
     * @return string representation of map
     */
    public static String convertMapToString(Map<String, String> stringMap) {
        final StringBuilder sb = new StringBuilder();
        final char KeySeparator = '=';
        final char PairSeparator = '&';
        for(final Map.Entry<String, String> pair:stringMap.entrySet())
        {
            sb.append(pair.getKey());
            sb.append(KeySeparator);
            sb.append(pair.getValue());
            sb.append(PairSeparator);
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    /**
     * Converts a delimited string value into a map.
     * Expects that '=' seperates the keys and values and a '&' seperates the key pairs.
     * @param value map to convert
     * @return string representation of map
     */
    public static Map<String, String> convertStringToMap(String value) {
        final Map<String, String> myMap = new HashMap<>();
        final String KeySeparator = "=";
        final String PairSeparator = "&";
        final String[] pairs = value.split(PairSeparator);
        for(final String pair : pairs) {
            final String[] keyValue = pair.split(KeySeparator);
            myMap.put(keyValue[0], keyValue[1]);
        }
        return myMap;
    }

    /**
     * Checks if an item exists in a HashSet that matches the specified Predicate.
     * @param set the HashSet to check against
     * @param condition the Predicate to check
     * @param <T> the type of objects in the HashSet
     * @return true if condition is true
     * @throws IllegalArgumentException if HashSet or Predicate is null
     */
    public static <T> boolean doesItemExistInHashSet(HashSet<T> set, Predicate<T> condition) {
        if(set == null) throw new IllegalArgumentException("HashSet cannot be null!");
        if(condition == null) throw new IllegalArgumentException("Predicate cannot be null!");
        for(final T object : set) if (condition.test(object)) return true;
        return false;
    }

    /**
     * Checks if a value exists in a HashMap that matches the specified Predicate.
     * @param map the HashMap to check against
     * @param condition the Predicate to check
     * @param <K> the type of the Key in the HashMap
     * @param <V> the type of the Value in the HashMap
     * @return true if condition is true
     * @throws IllegalArgumentException if HashMap or Predicate is null
     */
    public static <K, V> boolean doesItemExistInHashMap(HashMap<K, V> map, Predicate<V> condition) {
        if(map == null) throw new IllegalArgumentException("HashMap cannot be null!");
        if(condition == null) throw new IllegalArgumentException("Predicate cannot be null!");
        for(final Map.Entry<K, V> entry : map.entrySet()) if (condition.test(entry.getValue())) return true;
        return false;
    }

    /**
     * Returns item in HashSet that matches the specified Predicate
     * @param set the HashSet to check against
     * @param condition the Predicate to check
     * @param <T> the type of objects in the HashSet
     * @return an Optional containing the matching object, Empty if not found
     * @throws IllegalArgumentException if HashSet or Predicate is null
     */
    public static <T> Optional<T> getItemInHashSet(HashSet<T> set, Predicate<T> condition) {
        if(set == null) throw new IllegalArgumentException("HashSet cannot be null!");
        if(condition == null) throw new IllegalArgumentException("Predicate cannot be null!");
        for(final T object : set) if (condition.test(object)) return Optional.of(object);
        return Optional.empty();
    }

    /**
     * Returns a HashSet of values in a HashMap that match the specified Predicate
     * @param map the HashMap to check against
     * @param condition the Predicate to check
     * @param <K> the type of the Key in the HashMap
     * @param <V> the type of the Value in the HashMap
     * @return a HashSet of values matching the predicate, empty HashSet if no results found
     * @throws IllegalArgumentException if HashMap or Predicate is null
     */
    public static <K, V> HashSet<V> getValuesInHashMap(HashMap<K, V> map, Predicate<V> condition) {
        final HashSet<V> matchingValues = new HashSet<>();

        if(map == null) throw new IllegalArgumentException("HashMap cannot be null!");
        if(condition == null) throw new IllegalArgumentException("Predicate cannot be null!");

        map.forEach((key, value) -> { if(condition.test(value)) matchingValues.add(value); });

        return matchingValues;
    }

    /**
     * Returns the first item found in a HashMap that matches the specified Predicate
     * @param map the HashMap to check against
     * @param condition the Predicate to check
     * @param <K> the type of the Key in the HashMap
     * @param <V> the type of the Value in the HashMap
     * @return an Optional containing the matching value, Empty if no results found
     * @throws IllegalArgumentException if HashMap or Predicate is null
     */
    public static <K, V> Optional<V> getValueInHashMap(HashMap<K, V> map, Predicate<V> condition) {
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
}
