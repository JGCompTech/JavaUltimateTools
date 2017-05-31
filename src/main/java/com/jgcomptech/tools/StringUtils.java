package com.jgcomptech.tools;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Contains methods dealing with strings
 */
public final class StringUtils {
    /**
     * Converts a map into a delimited string value
     * @param stringMap map to convert
     * @return string representation of map
     * @throws IllegalArgumentException if StringMap is null
     */
    public static String convertMapToString(Map<String, String> stringMap) {
        if(stringMap == null) throw new IllegalArgumentException("StringMap cannot be null!");
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
     * Converts a delimited string value into a map
     * @param value map to convert
     * @return string representation of map
     * @throws IllegalArgumentException if Value is null
     */
    public static Map<String, String> convertStringToMap(String value) {
        if(value == null) throw new IllegalArgumentException("Value cannot be null!");
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
     * Converts an object to a byte array string
     * @param object to convert
     * @return a byte array string
     * @throws IllegalArgumentException if Object is null
     * @throws IOException if conversion fails
     */
    public static String convertToBytes(Object object) throws IOException {
        if(object == null) throw new IllegalArgumentException("Object cannot be null!");
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            final byte[] byteArray = bos.toByteArray();
            return new String(byteArray);
        }
    }

    /**
     * Converts a byte array to an object
     * @param bytes byte array to convert
     * @return the result object
     * @throws IllegalArgumentException if Bytes is null
     * @throws IOException if conversion fails
     * @throws ClassNotFoundException if conversion fails
     */
    public static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        if(bytes == null) throw new IllegalArgumentException("Bytes cannot be null!");
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    /**
     * Converts a byte string to an object
     * @param byteString byte string to convert
     * @return the result object
     * @throws IllegalArgumentException if ByteString is null
     * @throws IOException if conversion fails
     * @throws ClassNotFoundException if conversion fails
     */
    public static Object convertFromBytes(String byteString) throws IOException, ClassNotFoundException {
        if(byteString == null) throw new IllegalArgumentException("ByteString cannot be null!");
        final byte[] bytes = byteString.getBytes();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    /**
     * Surrounds a string with quotes
     * @param input string to edit
     * @return a string surrounded with quotes
     * @throws IllegalArgumentException if String is null
     */
    public static String quoteString(String input) {
        if(input == null) throw new IllegalArgumentException("String cannot be null!");
        return "\"" + input + "\"";
    }

    /**
     * Removes quotes from a string
     * @param input string to edit
     * @return a string with quotes removed
     * @throws IllegalArgumentException if String is null
     */
    public static String unquoteString(String input) {
        if(input == null) throw new IllegalArgumentException("String cannot be null!");
        return input.startsWith("\"") && input.endsWith("\"") ||
                input.startsWith("'") && input.endsWith("'") ?
                input.substring(1, input.length() - 1) : input;

    }

    /**
     * Converts a string to a Boolean
     *
     * @param input String to check
     * @throws IllegalArgumentException if string does not match a boolean value or is null
     */
    public static Boolean toBoolean(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        final String value = input.toLowerCase(Locale.ENGLISH).trim();
        switch(value)
        {
            case "true":
            case "t":
            case "yes":
            case "y":
            case "1":
            case "succeeded":
            case "succeed":
                return true;
            case "false":
            case "f":
            case "no":
            case "n":
            case "0":
            case "-1":
            case "failed":
            case "fail":
                return false;
            default:
                throw new IllegalArgumentException("Input is not a boolean value.");
        }
    }

    /**
     * Checks if a string is a valid IPv4 address
     * @param input string to check
     * @return true if string is a valid IPv4 address
     * @throws IllegalArgumentException if input is null
     */
    public static Boolean isValidIPAddress(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-4])\\.){3}" +
                "([0-9]|[1-9][0-9]|1[0-9‌​]{2}|2[0-4][0-9]|25[0-4])$");
    }

    /**
     * Checks if a string is an valid URL
     * @param input string to check
     * @return true if string is an valid URL
     * @throws IllegalArgumentException if input is null
     */
    public static Boolean isValidUrl(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.matches("/^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?/");
    }

    /**
     * Checks to see if the specified string is a valid email address according to the RFC 2822 specification
     * @param input the email address string to test for validity
     * @return true if the given text is valid according to RFC 2822, false otherwise
     * @throws IllegalArgumentException if input is null
     */
    public static boolean isValidEmailAddress(String input) { return isValidEmailAddress(input,
                false, false); }

    /**
     * Checks to see if the specified string is a valid email address according to the RFC 2822 specification
     * @param input the email address string to test for validity
     * @param allowQuotedIdentifiers specifies if quoted identifiers are allowed
     * @return true if the given text is valid according to RFC 2822, false otherwise
     * @throws IllegalArgumentException if input is null
     */
    public static boolean isValidEmailAddress(String input, boolean allowQuotedIdentifiers) {
        return isValidEmailAddress(input, allowQuotedIdentifiers, false);
    }

    /**
     * Checks to see if the specified string is a valid email address according to the RFC 2822 specification
     * @param input the email address string to test for validity
     * @param allowQuotedIdentifiers specifies if quoted identifiers are allowed
     * @param allowDomainLiterals specifies if domain literals are allowed
     * @return true if the given text is valid according to RFC 2822, false otherwise
     * @throws IllegalArgumentException if input is null
     */
    public static boolean isValidEmailAddress(String input,
                                              boolean allowQuotedIdentifiers, boolean allowDomainLiterals) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        final EmailValidator validator = new EmailValidator(allowQuotedIdentifiers, allowDomainLiterals);
        return validator.validPattern.matcher(input).matches();
    }

    /**
     * Ensures that a string starts with a given prefix
     * @param input string to check
     * @param prefix prefix to check
     * @param ignoreCase if true ignores case
     * @return string with the specified prefix
     * @throws IllegalArgumentException if Input or Prefix is null
     */
    public static String ensureStartsWith(String input, String prefix, Boolean ignoreCase) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        if(prefix == null) throw new IllegalArgumentException("Prefix cannot be null!");
        boolean startsWith = input.startsWith(prefix);
        if(!startsWith && ignoreCase) startsWith = input.startsWith(prefix.toUpperCase(Locale.ENGLISH));
        if(!startsWith && ignoreCase) startsWith = input.startsWith(prefix.toLowerCase(Locale.ENGLISH));
        return startsWith ? input : prefix.concat(input);
    }

    /**
     * Ensures that a string ends with a given suffix
     * @param input string to check
     * @param suffix suffix to check
     * @param ignoreCase if true ignores case
     * @return string with the specified suffix
     * @throws IllegalArgumentException if Input or Suffix is null
     */
    public static String ensureEndsWith(String input, String suffix, Boolean ignoreCase) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        if(suffix == null) throw new IllegalArgumentException("Suffix cannot be null!");
        boolean endsWith = input.endsWith(suffix);
        if(!endsWith && ignoreCase) endsWith = input.startsWith(suffix.toUpperCase(Locale.ENGLISH));
        if(!endsWith && ignoreCase) endsWith = input.startsWith(suffix.toLowerCase(Locale.ENGLISH));
        return endsWith ? input : input.concat(suffix);
    }

    /**
     * Checks if a string ends with a given suffix
     * @param input string to check
     * @param suffix suffix to check
     * @return true if string ends with a given suffix
     * @throws IllegalArgumentException if Input or Suffix is null
     */
    public static boolean endsWithIgnoreCase(String input, String suffix) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        if(suffix == null) throw new IllegalArgumentException("Suffix cannot be null!");
        return input.endsWith(suffix) ||
                input.length() >= suffix.length() && input.toLowerCase().endsWith(suffix.toLowerCase());
    }

    /**
     * Checks if a string starts with a given suffix
     * @param input string to check
     * @param prefix suffix to check
     * @return true if string starts with a given suffix
     * @throws IllegalArgumentException if Input or Suffix is null
     */
    public static boolean startsWithIgnoreCase(String input, String prefix) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        if(prefix == null) throw new IllegalArgumentException("Suffix cannot be null!");
        return input.startsWith(prefix) ||
                input.length() >= prefix.length() && input.toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * Removes the last character from a string
     * @param input string to edit
     * @return string with last character removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeLastCharacter(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.substring(0, input.length() - 1);
    }

    /**
     * Removes the specified number of characters removed from the end of a string
     * @param input string to edit
     * @param number the number of characters to remove
     * @return string with the specified number of characters removed from the end
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeLastCharacters(String input, int number) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.substring(0, input.length() - number);
    }

    /**
     * Removes the first character from a string
     * @param input string to edit
     * @return string with first character removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeFirstCharacter(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.substring(1);
    }

    /**
     * Removes the specified number of characters removed from the beginning of a string
     * @param input string to edit
     * @param number the number of characters to remove
     * @return string with the specified number of characters removed from the beginning
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeFirstCharacters(String input, int number) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.substring(number);
    }

    /**
     * Removes all characters except alphanumeric from specified string
     * @param input string to edit
     * @return string with all characters except alphanumeric removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeAllSpecialCharacters(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.replaceAll("[^a-zA-Z0-9]+","");
    }

    /**
     * Removes all alphanumeric characters from specified string
     * @param input string to edit
     * @return string with all alphanumeric characters removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeAllAlphanumericCharacters(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.replaceAll("[a-zA-Z0-9]+","");
    }

    /**
     * Removes all letters from specified string
     * @param input string to edit
     * @return string with all letters removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeAllLetters(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.replaceAll("[a-zA-Z]+","");
    }

    /**
     * Removes all numbers from specified string
     * @param input string to edit
     * @return string with all numbers removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeAllNumbers(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.replaceAll("[0-9]+","");
    }

    /**
     * Reverses the characters in the specified string
     * @param input string to edit
     * @return string with characters reversed
     * @throws IllegalArgumentException if Input is null
     */
    public static String reverse(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return new StringBuffer(input).reverse().toString();
    }

    /**
     * Returns the first part of the string, up until the character c. If c is not found in the
     * string the whole string is returned.
     * @param input the string to edit
     * @param c the character to find
     * @return edited string
     * @throws IllegalArgumentException if Input is null
     */
    public static String leftOf(String input, char c) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        final int index = input.indexOf(c);
        if (index >= 0) return input.substring(0, index);
        return input;
    }

    /**
     * Returns right part of the string, after the character c. If c is not found in the
     * string the whole string is returned.
     * @param input the string to edit
     * @param c the character to find
     * @return edited string
     * @throws IllegalArgumentException if Input is null
     */
    public static String rightOf(String input, char c) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        final int index = input.indexOf(c);
        if (index >= 0) return input.substring(index + 1);
        return input;
    }

    /**
     * Returns first character in a string or empty string if input is empty
     * @param input string to check
     * @return first character in a string
     * @throws IllegalArgumentException if Input is null
     */
    public static String firstChar(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.length() > 1 ? input.substring(0, 1) : input;
    }

    /**
     * Returns last character in a string or empty string if input is empty
     * @param input string to check
     * @return last character in a string
     * @throws IllegalArgumentException if Input is null
     */
    public static String lastChar(String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.length() > 1 ? input.substring(input.length() - 1, 1) : input;
    }

    /**
     * Returns first number of characters in a string or empty string if input is empty
     * @param input string to check
     * @param number number of characters to retrieve
     * @return first number of characters in a string
     * @throws IllegalArgumentException if Input is null
     */
    public static String firstChars(String input, int number) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.length() < number ? input : input.substring(0, number);
    }

    /**
     * Returns last number of characters in a string or empty string if input is empty
     * @param input string to check
     * @param number number of characters to retrieve
     * @return last number of characters in a string
     * @throws IllegalArgumentException if Input is null
     */
    public static String lastChars(String input, int number) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.length() < number ? input : input.substring(number + 1);
    }

    /**
     * Returns string with first char uppercase
     * @param input string to edit
     * @return string with first char uppercase
     * @throws IllegalArgumentException if Input is null or empty
     */
    public static String uppercaseFirst(String input) {
        if(input == null || input.isEmpty()) throw new IllegalArgumentException("Input cannot be null or empty!");
        return input.length() > 1 ? input.substring(0, 1).toUpperCase(Locale.ENGLISH) + input.substring(1) :
                input.toUpperCase(Locale.ENGLISH);
    }

    /**
     * Returns string with first char lowercase
     * @param input string to edit
     * @return string with first char lowercase
     * @throws IllegalArgumentException if Input is null or empty
     */
    public static String lowercaseFirst(String input) {
        if(input == null || input.isEmpty()) throw new IllegalArgumentException("Input cannot be null or empty!");
        return input.length() > 1 ? input.substring(0, 1).toLowerCase(Locale.ENGLISH) + input.substring(1) :
                input.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Capitalizes all words in a string
     * @param input string to edit
     * @return string with all words capitalized
     * @throws IllegalArgumentException if Input is null or empty
     */
    public static String toTitleCase(String input) {
        if(input == null || input.isEmpty()) throw new IllegalArgumentException("Input cannot be null or empty!");
        final String[] words = input.trim().split(" ");
        final StringBuilder sb = new StringBuilder();

        for(final String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Return a not null string.
     * @param input String
     * @return empty string if it is null otherwise the string passed in as parameter.
     */
    public static String nonNull(String input) { return input == null ? "" : input; }

    // This class should only be called statically
    private StringUtils() { super(); }
}

