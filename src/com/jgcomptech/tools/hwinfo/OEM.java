package com.jgcomptech.tools.hwinfo;

import com.jgcomptech.tools.RegistryInfo;

/**
 * Manufacturer Information
 */
public class OEM {
    /**
     * Returns the system manufacturer name that is stored in the registry.
     */
    public static String Name() {
        String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
        String value = "SystemManufacturer";
        String text = RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        if(text.equals("")) {
            key = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\OEMInFormation";
            value = "Manufacturer";
            return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        }
        return text;
    }

    /**
     * Returns the system product name that is stored in the registry.
     */
    public static String ProductName() {
        String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
        String value = "SystemProductName";
        String text = RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        if(text.equals("")) {
            key = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\OEMInFormation";
            value = "Model";
            return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        }
        return text;
    }
}
