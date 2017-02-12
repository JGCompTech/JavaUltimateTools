package com.jgcomptech.tools.osinfo.windows;

import com.jgcomptech.tools.NativeMethods;
import com.jgcomptech.tools.osinfo.enums.OSList;
import com.jgcomptech.tools.osinfo.enums.OtherConsts;

import static com.jgcomptech.tools.osinfo.windows.CheckIf.isWindowsServer;

/**
 * Gets the different names provided by the operating system.
 */
public class Name {
    public static OSList Enum() {
        switch(Version.Number()) {
            case 51:
                return OSList.WindowsXP;

            case 52:
                return isWindowsServer() ? (NativeMethods.getSystemMetrics(OtherConsts.SMServerR2.getValue()) ? OSList.Windows2003R2 : OSList.Windows2003) : OSList.WindowsXP64;
            case 60:
                return isWindowsServer() ? OSList.Windows2008 : OSList.WindowsVista;

            case 61:
                return isWindowsServer() ? OSList.Windows2008R2 : OSList.Windows7;

            case 62:
                return isWindowsServer() ? OSList.Windows2012 : OSList.Windows8;

            case 63:
                return isWindowsServer() ? OSList.Windows2012R2 : OSList.Windows81;

            case 64:
                return isWindowsServer() ? OSList.Windows2016 : OSList.Windows10;
        }
        return OSList.Windows2000AndPrevious;
    }

    public static String String() {
        switch(Version.Major()) {
            case 5: {
                switch(Version.Minor()) {
                    case 1:
                        return "Windows XP";
                    case 2:
                        return isWindowsServer() ? (NativeMethods.getSystemMetrics(OtherConsts.SMServerR2.getValue()) ? "Windows Server 2003 R2" : "Windows Server 2003") : "WindowsXP x64";
                }
                break;
            }
            case 6: {
                switch(Version.Minor()) {
                    case 0:
                        return isWindowsServer() ? "Windows 2008" : "Windows Vista";
                    case 1:
                        return isWindowsServer() ? "Windows 2008 R2" : "Windows 7";
                    case 2:
                        return isWindowsServer() ? "Windows 2012" : "Windows 8";
                    case 3:
                        return isWindowsServer() ? "Windows 2012 R2" : "Windows 8.1";
                }
                break;
            }
            case 10: {
                switch(Version.Minor()) {
                    case 0:
                        return isWindowsServer() ? "Windows 2016" : "Windows 10";
                }
                break;
            }
        }
        return "UNKNOWN";
    }
}
