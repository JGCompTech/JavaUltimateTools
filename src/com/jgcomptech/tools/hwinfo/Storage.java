package com.jgcomptech.tools.hwinfo;

import com.jgcomptech.tools.NativeMethods;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.ShlObj;
import com.sun.jna.platform.win32.WinDef;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.jgcomptech.tools.Misc.ConvertBytes;

/** Storage Information */
public class Storage {
    @NotNull
    public static String getSystemDrivePath() {
        char[] pszPath = new char[WinDef.MAX_PATH];
        NativeMethods.Shell32.INSTANCE.SHGetFolderPath
                (null, ShlObj.CSIDL_WINDOWS, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath);
        return Native.toString(pszPath).replace("WINDOWS", "");
    }

    @NotNull
    public static String getWindowsPath() {
        char[] pszPath = new char[WinDef.MAX_PATH];
        NativeMethods.Shell32.INSTANCE.SHGetFolderPath
                (null, ShlObj.CSIDL_WINDOWS, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath);
        return Native.toString(pszPath);
    }

    /** Returns the drive size of the drive Windows is installed on. */
    @NotNull
    public static String getSystemDriveSize() {
        return getDriveSize(getSystemDrivePath().replace(":/", "").charAt(0));
    }

    /** Returns the drive size of the specified drive by drive letter. Returns "N/A" if drive doesn't exist.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              */
    @NotNull
    public static String getDriveSize(char driveLetter) {
        File aDrive = new File(driveLetter + ":");
        if(aDrive.exists()) return ConvertBytes((double)aDrive.getTotalSpace());
        return "N/A";
    }

    /**
     * Returns the free space of drive of the drive Windows is installed on.
     */
    @NotNull
    public static String getSystemDriveFreeSpace() {
        return getDriveFreeSpace(getSystemDrivePath().replace(":/", "").charAt(0));
    }

    /**
     * Returns the free space of the specified drive by drive letter. Returns "N/A" if drive doesn't exist.
     */
    @NotNull
    public static String getDriveFreeSpace(char driveLetter) {
        File aDrive = new File(driveLetter + ":");
        if(aDrive.exists()) return ConvertBytes((double)aDrive.getUsableSpace());
        return "N/A";
    }
}
