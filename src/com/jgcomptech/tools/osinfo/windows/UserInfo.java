package com.jgcomptech.tools.osinfo.windows;

import com.jgcomptech.tools.NativeMethods;
import com.jgcomptech.tools.RegistryInfo;
import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.ptr.IntByReference;
import org.jetbrains.annotations.NotNull;

import java.net.UnknownHostException;

/**
 * Gets info about the currently logged in user account.
 */
public class UserInfo {
    /**
     * Gets the current Registered Organization.
     */
    @NotNull
    public static String RegisteredOrganization() {
        String key = "Software\\Microsoft\\Windows NT\\CurrentVersion";
        String value = "RegisteredOrganization";
        return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
    }

    /**
     * Gets the current Registered Owner.
     */
    @NotNull
    public static String RegisteredOwner() {
        String key = "Software\\Microsoft\\Windows NT\\CurrentVersion";
        String value = "RegisteredOwner";
        return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
    }

    /**
     * Gets the user name of the person who is currently logged on to the Windows operating system.
     */
    @NotNull
    public static String LoggedInUserName() {
        char[] userNameBuf = new char[10000];
        IntByReference size = new IntByReference(userNameBuf.length);
        boolean result = NativeMethods.Secur32.INSTANCE.GetUserNameEx
                (Secur32.EXTENDED_NAME_FORMAT.NameSamCompatible, userNameBuf, size);

        if(!result)
            throw new IllegalStateException("Cannot retrieve name of the currently logged-in user");

        String[] username = new String(userNameBuf, 0, size.getValue()).split("\\\\");
        return username[1];
    }

    /**
     * Gets the network domain name associated with the current user.
     * <p>
     * <exception cref="IllegalStateException">The network domain name cannot be retrieved.</exception>
     */
    public static String CurrentDomainName() {
        char[] userNameBuf = new char[10000];
        IntByReference size = new IntByReference(userNameBuf.length);
        boolean result = NativeMethods.Secur32.INSTANCE.GetUserNameEx
                (Secur32.EXTENDED_NAME_FORMAT.NameSamCompatible, userNameBuf, size);

        if(!result)
            throw new IllegalStateException("Cannot retrieve name of the currently logged-in user");

        String[] username = new String(userNameBuf, 0, size.getValue()).split("\\\\");
        return username[0];
    }

    @NotNull
    public static String CurrentMachineName() {
        try {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            return localMachine.getHostName();
        } catch(UnknownHostException e) {
            e.printStackTrace();
        }
        return "Error";
    }
}
