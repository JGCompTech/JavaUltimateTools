package com.jgcomptech.tools;

import java.io.*;
import java.util.*;

/**
 * Contains methods dealing with strings
 */
public class StringUtils {
    /**
     * Breaks a map into a delimited string value
     * @param stringMap map to convert
     * @return string representation of map
     */
    public static String BreakMapToString(Map<String, String> stringMap)
    {
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
     * Breaks a delimited string value into a map
     * @param value map to convert
     * @return string representation of map
     */
    public static Map<String, String> BreakStringToMap(String value)
    {
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

    public String convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            final byte[] byteArray = bos.toByteArray();
            return new String(byteArray);
        }
    }

    public Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    public Object convertFromBytes(String byteString) throws IOException, ClassNotFoundException {
        final byte[] bytes = byteString.getBytes();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }
}
