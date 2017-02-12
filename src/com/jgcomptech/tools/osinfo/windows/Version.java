package com.jgcomptech.tools.osinfo.windows;

import static com.citumpe.ctpTools.jWMI.CLASS_Win32_OperatingSystem;
import static com.citumpe.ctpTools.jWMI.getWMIValue;

/**
 * Gets the full version of the operating system running on this Computer.
 */
public class Version {
    /**
     * Gets the full version of the operating system running on this Computer.
     */
    public static String Main() { return getVersionInfo(Type.Main); }

    /**
     * Gets the major version of the operating system running on this Computer.
     */
    public static int Major() { return Integer.parseInt(getVersionInfo(Type.Major)); }

    /**
     * Gets the minor version of the operating system running on this Computer.
     */
    public static int Minor() { return Integer.parseInt(getVersionInfo(Type.Minor)); }

    /**
     * Gets the build version of the operating system running on this Computer.
     */
    public static int Build() { return Integer.parseInt(getVersionInfo(Type.Build)); }

    /**
     * Gets the revision version of the operating system running on this Computer.
     */
    public static int Revision() { return Integer.parseInt(getVersionInfo(Type.Revision)); }

    /**
     * Return a numeric value representing OS version.
     * <p>
     * <returns>(OSMajorVersion * 10 + OSMinorVersion)</returns>
     */
    public static int Number() { return (Major() * 10 + Minor()); }

    static String getVersionInfo(Type type) {
        String ReturnString = "0";
        try {
            String VersionString = getWMIValue("SELECT * FROM " + CLASS_Win32_OperatingSystem,
                    "Version");

            String Temp;
            String Major = VersionString.substring(0, VersionString.indexOf("."));
            Temp = VersionString.substring(Major.length() + 1);
            String Minor = Temp.substring(0, VersionString.indexOf(".") - 1);
            Temp = VersionString.substring(Major.length() + 1 + Minor.length() + 1);
            String Build;
            if(Temp.contains(".")) {
                Build = Temp.substring(0, VersionString.indexOf(".") - 1);
                Temp = VersionString.substring(Major.length() + 1 + Minor.length() + 1 + Build.length() + 1);
            } else {
                Build = Temp;
                Temp = "0";
            }
            String Revision = Temp;

            switch(type) {
                case Main:
                    ReturnString = VersionString;
                    break;

                case Major:
                    ReturnString = Major;
                    break;

                case Minor:
                    ReturnString = Minor;
                    break;

                case Build:
                    ReturnString = Build;
                    break;

                case Revision:
                    ReturnString = Revision;
                    break;
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }

        if(ReturnString.isEmpty()) return "0";
        return ReturnString;
    }

    public enum Type {
        Main,
        Major,
        Minor,
        Build,
        Revision
    }
}