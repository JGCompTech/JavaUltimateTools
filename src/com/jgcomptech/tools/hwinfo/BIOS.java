package com.jgcomptech.tools.hwinfo;

import com.jgcomptech.tools.RegistryInfo;
import com.jgcomptech.tools.osinfo.CheckIf;
import org.jetbrains.annotations.NotNull;

/** Returns information about the system BIOS. */
public class BIOS {
    /** Returns the system BIOS release date stored in the registry. */
    @NotNull
    public static String getReleaseDate() {
        if(CheckIf.isWindows()) {
            String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
            String value = "BIOSReleaseDate";
            return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        }
        return "Unknown";
    }

    /** Returns the system BIOS version stored in the registry. */
    @NotNull
    public static String getVersion() {
        if(CheckIf.isWindows()) {
            String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
            String value = "BIOSVersion";
            return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        }
        return "Unknown";
    }

    /** Returns the system BIOS vendor name stored in the registry. */
    @NotNull
    public static String getVendor() {
        if(CheckIf.isWindows()) {
            String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
            String value = "BIOSVendor";
            return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        }
        return "Unknown";
    }
}
