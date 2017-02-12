package com.jgcomptech.tools.osinfo;

import com.jgcomptech.tools.RegistryInfo;
import com.jgcomptech.tools.osinfo.enums.OSList;
import com.jgcomptech.tools.osinfo.windows.CheckIf;
import com.jgcomptech.tools.osinfo.windows.Edition;
import com.jgcomptech.tools.osinfo.windows.ServicePack;
import com.jgcomptech.tools.osinfo.windows.Version;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

import static com.jgcomptech.tools.osinfo.CheckIf.*;

/**
 * Gets the different names provided by the operating system.
 */
public class Name {
    private static final String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

    /**
     * Return a full version String, ex.: "Windows XP Service Pack 2 (32 Bit)".
     *
     * @return String representing a fully displayable version as stored in WMI.
     */
    public static String StringExpanded() {
        if(isWindows()) {
            String SPString = "";
            if(CheckIf.isWin8OrLater()) SPString = " - " + Version.Build();
            else SPString = " SP" + ServicePack.String().substring(SPString.length() - 1);

            return String() + " " + Edition.String() + " " + SPString + " (" + Architecture.Number() + " Bit)";
        } else return OS;
    }

    /**
     * Return a full version String, ex.: "Windows XP Service Pack 2 (32 Bit)".
     *
     * @return String representing a fully displayable version as stored in Windows Registry.
     */
    public static String StringExpanded2() {
        if(isWindows()) {
            String SPString = "";
            if(CheckIf.isWin8OrLater()) SPString = " - " + Version.Build();
            else SPString = " SP" + ServicePack.String().substring(SPString.length() - 1);

            String key = "Software\\\\Microsoft\\\\Windows NT\\\\CurrentVersion";
            String value = "ProductName";
            String text = RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            return text + " " + SPString + " (" + Architecture.Number() + " Bit)";
        } else return OS;
    }

    /**
     * Returns the name of the operating system running on this Computer.
     *
     * @return Enum value containing the the operating system name.
     */
    public static OSList Enum() {
        if(isWindows()) return Name.Enum();
        else if(isMac()) return OSList.MacOSX;
        else if(isLinux()) return OSList.Linux;
        else if(isSolaris()) return OSList.Solaris;
        return OSList.Unknown;
    }

    /**
     * Returns the name of the operating system running on this computer.
     *
     * @return String value containing the the operating system name.
     */
    public static String String() {
        if(isWindows()) return Name.String();
        else if(isMac()) return "Mac OSX";
        else if(isLinux()) return "Linux";
        else if(isSolaris()) return "Solaris";
        return "UNKNOWN";
    }

    /**
     * Gets the current computer name.
     *
     * @return String value of current computer name.
     */
    @NotNull
    public static String ComputerNameActive() {
        String key = "System\\ControlSet001\\Control\\ComputerName\\ActiveComputerName";
        String value = "ComputerNameActive";
        return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
    }

    /**
     * Gets the pending computer name that it will update to on reboot.
     *
     * @return String value of the pending computer name.
     */
    //TODO Make C# version more efficient
    @NotNull
    public static String ComputerNamePending() {
        String key = "System\\ControlSet001\\Control\\ComputerName\\ActiveComputerName";
        String value = "ComputerNameActive";
        String text = RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        return Objects.equals(ComputerNameActive(), "") ? "N/A" : text;
    }
}
