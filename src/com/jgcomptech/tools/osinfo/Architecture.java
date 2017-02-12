package com.jgcomptech.tools.osinfo;

import static com.jgcomptech.tools.osinfo.CheckIf.is64BitOS;

/**
 * Determines if the current application is 32 or 64-bit.
 */
public class Architecture {
    /** Determines if the current application is 32 or 64-bit. */
    public static String String() { return is64BitOS() ? "64 bit" : "32 bit"; }

    /** Determines if the current application is 32 or 64-bit. */
    public static int Number() { return is64BitOS() ? 64 : 32; }
}
