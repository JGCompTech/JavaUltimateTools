package com.jgcomptech.tools;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

/** Contains methods that allow access to Windows C++ dll files */
public class NativeMethods {
    /**
     * Generates a new instance of the VersionInfo object
     *
     * @param osVersionInfo Empty VersionInfo object to fill
     * @return True if an error occurs
     */
    public static boolean getVersionInfoFailed(WinNT.OSVERSIONINFOEX osVersionInfo) {
        return !Kernel32.INSTANCE.GetVersionEx(osVersionInfo);
    }

    /**
     * Checks if a system metrics value is true
     *
     * @param nIndex Value to check for
     * @return False if value is false
     */
    public static boolean getSystemMetrics(int nIndex) { return Kernel32.INSTANCE.GetSystemMetrics(nIndex); }

    /**
     * Generates a new instance of the ProductInfo object
     *
     * @param Major version
     * @param Minor version
     * @return Product Info as an int
     */
    public static int getProductInfo(int Major, int Minor) {
        IntByReference strProductType = new IntByReference();
        Kernel32.INSTANCE.GetProductInfo(Major, Minor, 0, 0, strProductType);
        return strProductType.getValue();
    }

    /**
     * Interface object to hold all the Kernel32 Instances
     * */
    public interface Kernel32 extends com.sun.jna.platform.win32.Kernel32 {
        Kernel32 INSTANCE = (Kernel32)
                Native.loadLibrary("kernel32", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);

        boolean GetProductInfo(
                int dwOSMajorVersion,
                int dwOSMinorVersion,
                int dwSpMajorVersion,
                int dwSpMinorVersion,
                IntByReference pdwReturnedProductType);

        boolean GetSystemMetrics(int nIndex);
    }

    /**
     * Interface object to hold all the Shell32 Instances
     * */
    public interface Shell32 extends com.sun.jna.platform.win32.Shell32 {
        Shell32 INSTANCE = (Shell32)
                Native.loadLibrary("shell32", Shell32.class, W32APIOptions.DEFAULT_OPTIONS);

        WinDef.HINSTANCE ShellExecuteW(WinDef.HWND hwnd,
                                       String lpOperation,
                                       WString lpFile,
                                       String lpParameters,
                                       String lpDirectory,
                                       int nShowCmd);
    }

    /**
     * Interface object to hold all the Secur32 Instances
     * */
    public interface Secur32 extends com.sun.jna.platform.win32.Secur32 {
        Secur32 INSTANCE = (Secur32)
                Native.loadLibrary("secur32", Secur32.class, W32APIOptions.DEFAULT_OPTIONS);
    }

    // This class should only be called statically
    private NativeMethods() { super(); }
}