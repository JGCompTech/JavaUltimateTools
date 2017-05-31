package com.jgcomptech.tools;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

/** Registry tools for handling registry keys and values */
public final class RegistryInfo {
    /**
     * Gets string value from a registry key
     *
     * @param hkey  Root key to use to access key
     * @param key   Key path to access
     * @param value Value name to access
     * @return Key value as string
     */
    public static String getStringValue(HKEY hkey, String key, String value) {
        WinReg.HKEY keyobj = null;

        switch(hkey) {
            case CLASSES_ROOT:
                keyobj = WinReg.HKEY_CLASSES_ROOT;
                break;
            case CURRENT_USER:
                keyobj = WinReg.HKEY_CURRENT_USER;
                break;
            case LOCAL_MACHINE:
                keyobj = WinReg.HKEY_LOCAL_MACHINE;
                break;
            case USERS:
                keyobj = WinReg.HKEY_USERS;
                break;
            case PERFORMANCE_DATA:
                keyobj = WinReg.HKEY_PERFORMANCE_DATA;
                break;
            case CURRENT_CONFIG:
                keyobj = WinReg.HKEY_CURRENT_CONFIG;
                break;
        }

        if(Advapi32Util.registryValueExists(keyobj, key, value)) {
            return Advapi32Util.registryGetStringValue(keyobj, key, value);
        }
        return "";
    }

    /**
     * A list of the different parent keys in the Windows Registry that are used in the {@link RegistryInfo} class
     */
    public enum HKEY {
        CLASSES_ROOT,
        CURRENT_USER,
        LOCAL_MACHINE,
        USERS,
        PERFORMANCE_DATA,
        CURRENT_CONFIG
    }

    // This class should only be called statically
    private RegistryInfo() { super(); }
}
