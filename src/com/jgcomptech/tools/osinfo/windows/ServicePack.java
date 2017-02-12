package com.jgcomptech.tools.osinfo.windows;

import com.jgcomptech.tools.NativeMethods;
import com.sun.jna.platform.win32.WinNT;

import static com.jgcomptech.tools.osinfo.windows.CheckIf.isWin8OrLater;

/**
 * Gets the service pack information of the operating system running on this Computer.
 */
public class ServicePack {
    /**
     * Returns the service pack information of the operating system running on this Computer.
     *
     * @return A String containing the operating system service pack inFormation.
     */
    public static String String() {
        String sp = Integer.toString(Number());
        return isWin8OrLater() ? "" : ((sp.equals("") | sp.isEmpty()) ? "Service Pack 0" : sp);
    }

    /**
     * Returns the service pack information of the operating system running on this Computer.
     *
     * @return A int containing the operating system service pack number.
     */
    public static int Number() {
        WinNT.OSVERSIONINFOEX osVersionInfo = new WinNT.OSVERSIONINFOEX();
        return NativeMethods.getVersionInfoFailed(osVersionInfo) ? -1 : osVersionInfo.wServicePackMajor.intValue();
    }
}
