package com.jgcomptech.tools.osinfo.windows;

import com.jgcomptech.tools.osinfo.enums.ProductType;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinNT;

/**
 * Contains boolean functions to check if certain conditions are true
 */
public class CheckIf {
    /**
     * Identifies if OS is activated
     *
     * @return true if activated, false if not activated
     */
    public static boolean isActivated() { return Activation.isActivated(); }

    /**
     * Identifies if OS is a Windows Server OS
     *
     * @return true if OS is a Windows Server OS
     */
    public static boolean isWindowsServer() {
        return ProductType.parse(Edition.ProductType()) != ProductType.NTWorkstation;
    }

    /**
     * Identifies if OS is a Windows Domain Controller
     *
     * @return true if OS is a Windows Server OS
     */
    public static boolean isWindowsDomainController() {
        return ProductType.parse(Edition.ProductType()) == ProductType.NTDomainController;
    }

    /**
     * Identifies if computer has joined a domain
     *
     * @return true if computer has joined a domain
     */
    //TODO Add C# implementation
    public static boolean isDomainJoined() {
        return !UserInfo.CurrentMachineName().equals(UserInfo.CurrentDomainName());
    }

    /**
     * Identifies if the current user is an account administrator
     *
     * @return true if current user is an account administrator
     */
    //TODO Add C# implementation
    public static boolean isCurrentUserAdmin() {
        boolean isAdmin = false;
        Advapi32Util.Account[] groups = Advapi32Util.getCurrentUserGroups();
        for(Advapi32Util.Account group : groups) {
            WinNT.PSIDByReference sid = new WinNT.PSIDByReference();
            Advapi32.INSTANCE.ConvertStringSidToSid(group.sidString, sid);
            if(Advapi32.INSTANCE.IsWellKnownSid(sid.getValue(),
                    WinNT.WELL_KNOWN_SID_TYPE.WinBuiltinAdministratorsSid)) {
                isAdmin = true;
                break;
            }
        }
        return isAdmin;
    }

    /**
     * Identifies if OS is XP or later
     *
     * @return true if XP or later, false if 2000 or previous
     */
    public static boolean isWinXPOrLater() { return Version.Number() >= 51; }

    /**
     * Identifies if OS is XP x64 or later
     *
     * @return true if XP x64 or later, false if XP or previous
     */
    public static boolean isWinXP64OrLater() { return Version.Number() >= 52; }

    /**
     * Identifies if OS is Vista or later
     *
     * @return true if Vista or later, false if XP or previous
     */
    public static boolean isWinVistaOrLater() { return Version.Number() >= 60; }

    /**
     * Identifies if OS is Windows 7 or later
     *
     * @return true if Windows 7 or later, false if Vista or previous
     */
    public static boolean isWin7OrLater() { return Version.Number() >= 61; }

    /**
     * Identifies if OS is Windows 8 or later
     *
     * @return true if Windows 8 or later, false if Windows 7 or previous
     */
    public static boolean isWin8OrLater() { return Version.Number() >= 62; }

    /**
     * Identifies if OS is Windows 8.1 or later
     *
     * @return true if Windows 8.1 or later, false if Windows 8 or previous
     */
    public static boolean isWin81OrLater() { return Version.Number() >= 63; }

    /**
     * Identifies if OS is Windows 10 or later
     *
     * @return true if Windows 10 or later, false if Windows 10 or previous
     */
    public static boolean isWin10OrLater() { return Version.Number() >= 100; }
}
