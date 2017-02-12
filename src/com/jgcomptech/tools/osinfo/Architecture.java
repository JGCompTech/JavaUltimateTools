package com.jgcomptech.tools.osinfo;

/**
 * Determines if the current application is 32 or 64-bit.
 */
public class Architecture {
    /** Determines if the current application is 32 or 64-bit. */
    public static String String() { return CheckIf.is64BitOS() ? "64 bit" : "32 bit"; }

    /** Determines if the current application is 32 or 64-bit. */
    public static int Number() { return CheckIf.is64BitOS() ? 64 : 32; }
}
