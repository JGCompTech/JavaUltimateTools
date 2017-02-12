package com.jgcomptech.tools.osinfo;

import java.util.Locale;

/**
 * Runs different checks against the OS and returns a Boolean value.
 */
public class CheckIf {
    private static final String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

    /**
     * Identifies if OS is a 32 Bit OS.
     *
     * @return True if OS is a 32 Bit OS.
     */
    public static boolean is32BitOS() { return !is64BitOS(); }
    /**
     * Identifies if OS is a 64 Bit OS.
     *
     * @return True if OS is a 64 Bit OS.
     */
    public static boolean is64BitOS() {
        if (OS.contains("Windows")) {
            return (System.getenv("ProgramFiles(x86)") != null);
        } else {
            return (OS.contains("64"));
        }
    }

    /**
     * Identifies if OS is Windows.
     *
     * @return True if OS is Windows.
     */
    public static boolean isWindows() { return (OS.contains("win")); }

    /**
     * Identifies if OS is MacOSX.
     *
     * @return True if OS is MacOSX.
     */
    public static boolean isMac() { return (OS.contains("mac")) || (OS.contains("darwin")); }

    /**
     * Identifies if OS is a distro of Linux.
     *
     * @return True if OS is Linux.
     */
    public static boolean isLinux() { return ((OS.contains("nix") || OS.contains("nux")) && !OS.contains("aix")); }

    /**
     * Identifies if OS is Solaris.
     *
     * @return True if OS is Solaris.
     */
    public static boolean isSolaris() { return (OS.contains("sunos")); }
}
