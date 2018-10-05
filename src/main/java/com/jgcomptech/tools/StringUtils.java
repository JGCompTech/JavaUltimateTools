package com.jgcomptech.tools;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains methods dealing with strings.
 * @since 1.4.0
 */
public final class StringUtils {
    /**
     * Converts a map into a delimited string value.
     * A "{@literal =}" separates the keys and values and a "{@literal &}" separates the key pairs.
     * @param stringMap map to convert
     * @return string representation of map
     * @throws IllegalArgumentException if StringMap is null
     */
    public static String convertMapToString(final Map<String, String> stringMap) {
        return CollectionUtils.convertMapToString(stringMap);
    }

    /**
     * Converts a delimited string value into a map.
     * <p>Expects that "{@literal =}" separates the keys and values and a "{@literal &}" separates the key pairs.</p>
     * @param value map to convert
     * @return string representation of map
     * @throws IllegalArgumentException if Value is null
     * @since 1.5.0 now uses a stream to process the pairs
     */
    public static Map<String, String> convertStringToMap(final String value) {
        return CollectionUtils.convertStringToMap(value);
    }

    /**
     * Converts an object to a Base64 byte string to use for socket communication.
     * @param object the object to convert to a Base64 byte string
     * @return the Base64 byte string
     * @throws IllegalArgumentException if Object is null
     * @throws IOException if the conversion fails
     */
    public static String convertToByteString(final Object object) throws IOException {
        if(object == null) throw new IllegalArgumentException("Object cannot be null!");
        try (final var bos = new ByteArrayOutputStream(); final ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            final var byteArray = bos.toByteArray();
            return Base64.getEncoder().encodeToString(byteArray);
        }
    }

    /**
     * Converts a byte array to an object.
     * @param bytes the byte array to convert to an object
     * @return the converted object
     * @throws IllegalArgumentException if Bytes is null
     * @throws IOException if the conversion fails
     * @throws ClassNotFoundException if the converted object doesn't match a found object type
     */
    public static Object convertFromByteArray(final byte[] bytes) throws IOException, ClassNotFoundException {
        if(bytes == null) throw new IllegalArgumentException("Bytes cannot be null!");
        try (final var bis = new ByteArrayInputStream(bytes); final ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    /**
     * Converts a Base64 byte string to an object.
     * @param byteString the Base64 byte string to convert to an object
     * @return the converted object
     * @throws IllegalArgumentException if ByteString is null
     * @throws IOException if the conversion fails
     * @throws ClassNotFoundException if the converted object doesn't match a found object type
     */
    public static Object convertFromByteString(final String byteString)
            throws IOException, ClassNotFoundException {
        if(byteString == null) throw new IllegalArgumentException("Byte String cannot be null!");
        final var bytes = Base64.getDecoder().decode(byteString);
        try (final var bis = new ByteArrayInputStream(bytes); final ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    /**
     * Surrounds a string with quotes.
     * @param input string to edit
     * @return a string surrounded with quotes
     * @throws IllegalArgumentException if String is null
     */
    public static String quoteString(final String input) {
        if(input == null) throw new IllegalArgumentException("String cannot be null!");
        return '"' + input + '"';
    }

    /**
     * Removes quotes from a string.
     * @param input string to edit
     * @return a string with quotes removed
     * @throws IllegalArgumentException if String is null
     */
    public static String unquoteString(final String input) {
        if(input == null) throw new IllegalArgumentException("String cannot be null!");
        return (input.startsWith("\"") && input.endsWith("\""))
                || (input.startsWith("'") && input.endsWith("'"))
                ? input.substring(1, input.length() - 1) : input;

    }

    /**
     * Converts a string to a Boolean.
     * @param input String to check
     * @return true if string does matches a boolean
     * @throws IllegalArgumentException if string does not match a boolean value or is null
     * @since 1.5.0 added "enabled" returns true and "disabled" returns false
     */
    public static Boolean toBoolean(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        final var value = input.toLowerCase(Locale.ENGLISH).trim();
        switch(value)
        {
            case "true":
            case "t":
            case "yes":
            case "y":
            case "1":
            case "succeeded":
            case "succeed":
            case "enabled":
                return true;
            case "false":
            case "f":
            case "no":
            case "n":
            case "0":
            case "-1":
            case "failed":
            case "fail":
            case "disabled":
                return false;
            default:
                throw new IllegalArgumentException("Input is not a boolean value.");
        }
    }

    /**
     * Checks if a string is a valid IPv4 address.
     * @param input string to check
     * @return true if string is a valid IPv4 address
     * @throws IllegalArgumentException if input is null
     */
    public static Boolean isValidIPAddress(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-4])\\.){3}"
                + "([0-9]|[1-9][0-9]|1[0-9‌​]{2}|2[0-4][0-9]|25[0-4])$");
    }

    /**
     * Checks if a string is an valid URL.
     * @param input string to check
     * @return true if string is an valid URL
     * @throws IllegalArgumentException if input is null
     */
    public static Boolean isValidUrl(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }

    /**
     * Checks to see if the specified string is a valid email address according to the RFC 2822 specification.
     * @param input the email address string to test for validity
     * @return true if the given text is valid according to RFC 2822, false otherwise
     * @throws IllegalArgumentException if input is null
     */
    public static boolean isValidEmailAddress(final String input) { return isValidEmailAddress(input,
                false, false); }

    /**
     * Checks to see if the specified string is a valid email address according to the RFC 2822 specification.
     * @param input the email address string to test for validity
     * @param allowQuotedIdentifiers specifies if quoted identifiers are allowed
     * @return true if the given text is valid according to RFC 2822, false otherwise
     * @throws IllegalArgumentException if input is null
     */
    public static boolean isValidEmailAddress(final String input, final boolean allowQuotedIdentifiers) {
        return isValidEmailAddress(input, allowQuotedIdentifiers, false);
    }

    /**
     * Checks to see if the specified string is a valid email address according to the RFC 2822 specification.
     * @param input the email address string to test for validity
     * @param allowQuotedIdentifiers specifies if quoted identifiers are allowed
     * @param allowDomainLiterals specifies if domain literals are allowed
     * @return true if the given text is valid according to RFC 2822, false otherwise
     * @throws IllegalArgumentException if input is null
     */
    public static boolean isValidEmailAddress(final String input,
                                              final boolean allowQuotedIdentifiers, final boolean allowDomainLiterals) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        final var validator = new EmailValidator(allowQuotedIdentifiers, allowDomainLiterals);
        return validator.validPattern.matcher(input).matches();
    }

    /**
     * Ensures that a string starts with a given prefix.
     * @param input string to check
     * @param prefix prefix to check
     * @param ignoreCase if true ignores case
     * @return string with the specified prefix
     * @throws IllegalArgumentException if Input or Prefix is null
     */
    public static String ensureStartsWith(final String input, final String prefix, final Boolean ignoreCase) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        if(prefix == null) throw new IllegalArgumentException("Prefix cannot be null!");
        var startsWith = input.startsWith(prefix);
        if(!startsWith && ignoreCase) startsWith = input.startsWith(prefix.toUpperCase(Locale.ENGLISH));
        if(!startsWith && ignoreCase) startsWith = input.startsWith(prefix.toLowerCase(Locale.ENGLISH));
        return startsWith ? input : prefix + input;
    }

    /**
     * Ensures that a string ends with a given suffix.
     * @param input string to check
     * @param suffix suffix to check
     * @param ignoreCase if true ignores case
     * @return string with the specified suffix
     * @throws IllegalArgumentException if Input or Suffix is null
     */
    public static String ensureEndsWith(final String input, final String suffix, final Boolean ignoreCase) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        if(suffix == null) throw new IllegalArgumentException("Suffix cannot be null!");
        var endsWith = input.endsWith(suffix);
        if(!endsWith && ignoreCase) endsWith = input.startsWith(suffix.toUpperCase(Locale.ENGLISH));
        if(!endsWith && ignoreCase) endsWith = input.startsWith(suffix.toLowerCase(Locale.ENGLISH));
        return endsWith ? input : input + suffix;
    }

    /**
     * Checks if a string ends with a given suffix.
     * @param input string to check
     * @param suffix suffix to check
     * @return true if string ends with a given suffix
     * @throws IllegalArgumentException if Input or Suffix is null
     */
    public static boolean endsWithIgnoreCase(final String input, final String suffix) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        if(suffix == null) throw new IllegalArgumentException("Suffix cannot be null!");
        return input.endsWith(suffix)
                || (input.length() >= suffix.length() && input.toLowerCase().endsWith(suffix.toLowerCase()));
    }

    /**
     * Checks if a string starts with a given suffix.
     * @param input string to check
     * @param prefix suffix to check
     * @return true if string starts with a given suffix
     * @throws IllegalArgumentException if Input or Suffix is null
     */
    public static boolean startsWithIgnoreCase(final String input, final String prefix) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        if(prefix == null) throw new IllegalArgumentException("Suffix cannot be null!");
        return input.startsWith(prefix)
                || (input.length() >= prefix.length() && input.toLowerCase().startsWith(prefix.toLowerCase()));
    }

    /**
     * Removes the last character from a string.
     * @param input string to edit
     * @return string with last character removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeLastCharacter(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.substring(0, input.length() - 1);
    }

    /**
     * Removes the specified number of characters removed from the end of a string.
     * @param input string to edit
     * @param number the number of characters to remove
     * @return string with the specified number of characters removed from the end
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeLastCharacters(final String input, final int number) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.substring(0, input.length() - number);
    }

    /**
     * Removes the first character from a string.
     * @param input string to edit
     * @return string with first character removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeFirstCharacter(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.substring(1);
    }

    /**
     * Removes the specified number of characters removed from the beginning of a string.
     * @param input string to edit
     * @param number the number of characters to remove
     * @return string with the specified number of characters removed from the beginning
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeFirstCharacters(final String input, final int number) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.substring(number);
    }

    /**
     * Removes all characters except alphanumeric from specified string.
     * @param input string to edit
     * @return string with all characters except alphanumeric removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeAllSpecialCharacters(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.replaceAll("[^a-zA-Z0-9]+","");
    }

    /**
     * Removes all alphanumeric characters from specified string.
     * @param input string to edit
     * @return string with all alphanumeric characters removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeAllAlphanumericCharacters(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.replaceAll("[a-zA-Z0-9]+","");
    }

    /**
     * Removes all letters from specified string.
     * @param input string to edit
     * @return string with all letters removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeAllLetters(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.replaceAll("[a-zA-Z]+","");
    }

    /**
     * Removes all numbers from specified string.
     * @param input string to edit
     * @return string with all numbers removed
     * @throws IllegalArgumentException if Input is null
     */
    public static String removeAllNumbers(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.replaceAll("[0-9]+","");
    }

    /**
     * Reverses the characters in the specified string.
     * @param input string to edit
     * @return string with characters reversed
     * @throws IllegalArgumentException if Input is null
     */
    public static String reverse(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return new StringBuilder(input).reverse().toString();
    }

    /**
     * Returns the first part of the string, up until the character c. If c is not found in the
     * string the whole string is returned.
     * @param input the string to edit
     * @param c the character to find
     * @return edited string
     * @throws IllegalArgumentException if Input is null
     */
    public static String leftOf(final String input, final char c) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        final var index = input.indexOf(c);
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
    public static String rightOf(final String input, final char c) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        final var index = input.indexOf(c);
        if (index >= 0) return input.substring(index + 1);
        return input;
    }

    /**
     * Returns first character in a string or empty string if input is empty.
     * @param input string to check
     * @return first character in a string
     * @throws IllegalArgumentException if Input is null
     */
    public static String firstChar(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.length() > 1 ? input.substring(0, 1) : input;
    }

    /**
     * Returns last character in a string or empty string if input is empty.
     * @param input string to check
     * @return last character in a string
     * @throws IllegalArgumentException if Input is null
     */
    public static String lastChar(final String input) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.length() > 1 ? input.substring(input.length() - 1, 1) : input;
    }

    /**
     * Returns first number of characters in a string or empty string if input is empty.
     * @param input string to check
     * @param number number of characters to retrieve
     * @return first number of characters in a string
     * @throws IllegalArgumentException if Input is null
     */
    public static String firstChars(final String input, final int number) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.length() < number ? input : input.substring(0, number);
    }

    /**
     * Returns last number of characters in a string or empty string if input is empty.
     * @param input string to check
     * @param number number of characters to retrieve
     * @return last number of characters in a string
     * @throws IllegalArgumentException if Input is null
     */
    public static String lastChars(final String input, final int number) {
        if(input == null) throw new IllegalArgumentException("Input cannot be null!");
        return input.length() < number ? input : input.substring(number + 1);
    }

    /**
     * Returns string with first char uppercase.
     * @param input string to edit
     * @return string with first char uppercase
     * @throws IllegalArgumentException if Input is null or empty
     */
    public static String uppercaseFirst(final String input) {
        if(input == null || input.trim().isEmpty()) throw new IllegalArgumentException("Input cannot be null or empty!");
        return input.length() > 1
                ? input.substring(0, 1).toUpperCase(Locale.ENGLISH) + input.substring(1)
                : input.toUpperCase(Locale.ENGLISH);
    }

    /**
     * Returns string with first char lowercase.
     * @param input string to edit
     * @return string with first char lowercase
     * @throws IllegalArgumentException if Input is null or empty
     */
    public static String lowercaseFirst(final String input) {
        if(input == null || input.trim().isEmpty()) throw new IllegalArgumentException("Input cannot be null or empty!");
        return input.length() > 1
                ? input.substring(0, 1).toLowerCase(Locale.ENGLISH) + input.substring(1)
                : input.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Capitalizes all words in a string.
     * @param input string to edit
     * @return string with all words capitalized
     * @throws IllegalArgumentException if Input is null or empty
     */
    public static String toTitleCase(final String input) {
        if(input == null || input.trim().isEmpty()) throw new IllegalArgumentException("Input cannot be null or empty!");
        final var words = input.trim().split(" ");
        return Arrays.stream(words)
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1) + ' ')
                .collect(Collectors.joining())
                .trim();
    }

    /**
     * Return a not null string.
     * @param input String
     * @return empty string if it is null otherwise the string passed in as parameter.
     */
    public static String nonNull(final String input) { return input == null ? "" : input; }

    /** Prevents instantiation of this utility class. */
    private StringUtils() { }
}

