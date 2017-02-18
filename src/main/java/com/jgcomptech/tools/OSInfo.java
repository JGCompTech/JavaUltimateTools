package com.jgcomptech.tools;

import com.jgcomptech.tools.enums.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.UnknownHostException;
import java.util.Locale;

import static com.jgcomptech.tools.OSInfo.CheckIf.*;
import static com.jgcomptech.tools.OSInfo.Windows.CheckIf.isWin8OrLater;
import static com.jgcomptech.tools.OSInfo.Windows.CheckIf.isWindowsServer;

public class OSInfo {
    /**
     * Determines if the current application is 32 or 64-bit.
     */
    public static class Architecture {
        /** Determines if the current application is 32 or 64-bit. */
        public static String String() { return is64BitOS() ? "64 bit" : "32 bit"; }

        /** Determines if the current application is 32 or 64-bit. */
        public static int Number() { return is64BitOS() ? 64 : 32; }
    }

    /**
     * Runs different checks against the OS and returns a Boolean value.
     */
    public static class CheckIf {
        private static final String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

        /**
         * Identifies if OS is a 32 Bit OS.
         *
         * @return True if OS is a 32 Bit OS.
         */
        public static boolean is32BitOS() { return !is64BitOS(); }

        /**
         * Identifies if OS is a 64 Bit OS.
         *
         * @return True if OS is a 64 Bit OS.
         */
        public static boolean is64BitOS() {
            if(isWindows()) {
                return (System.getenv("ProgramFiles(x86)") != null);
            } else {
                return (OS.contains("64"));
            }
        }

        /**
         * Identifies if OS is Windows.
         *
         * @return True if OS is Windows.
         */
        public static boolean isWindows() { return (OS.contains("win")); }

        /**
         * Identifies if OS is MacOSX.
         *
         * @return True if OS is MacOSX.
         */
        public static boolean isMac() { return (OS.contains("mac")) || (OS.contains("darwin")); }

        /**
         * Identifies if OS is a distro of Linux.
         *
         * @return True if OS is Linux.
         */
        public static boolean isLinux() { return ((OS.contains("nix") || OS.contains("nux")) && !OS.contains("aix")); }

        /**
         * Identifies if OS is Solaris.
         *
         * @return True if OS is Solaris.
         */
        public static boolean isSolaris() { return (OS.contains("sunos")); }
    }

    /**
     * Gets the different names provided by the operating system.
     */
    public static class Name {
        private static final String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

        /**
         * Return a full version String, ex.: "Windows XP Service Pack 2 (32 Bit)".
         *
         * @return String representing a fully displayable version as stored in WMI.
         */
        public static String StringExpanded() {
            if(isWindows()) {
                String SPString = "";
                if(isWin8OrLater()) SPString = " - " + Windows.Version.Build();
                else SPString = " SP" + Windows.ServicePack.String().substring(SPString.length() - 1);

                return String() + " " + Windows.Edition.String() + " " + SPString + " (" + Architecture.Number() + " Bit)";
            } else return OS;
        }

        /**
         * Return a full version String, ex.: "Windows XP Service Pack 2 (32 Bit)".
         *
         * @return String representing a fully displayable version as stored in Windows Registry.
         */
        public static String StringExpandedFromRegistry() {
            if(isWindows()) {
                String SPString = "";
                if(isWin8OrLater()) SPString = " - " + Windows.Version.Build();
                else SPString = " SP" + Windows.ServicePack.String().substring(SPString.length() - 1);

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
        @NotNull
        public static String ComputerNamePending() {
            String key = "System\\ControlSet001\\Control\\ComputerName\\ActiveComputerName";
            String value = "ComputerNameActive";
            String text = RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            return text.equals("") ? "N/A" : text;
        }
    }

    public static class Windows {
        /**
         * Gets information about the Windows activation status.
         */
        public static class Activation {
            /**
             * Identifies if OS is activated
             *
             * @return true if activated, false if not activated
             */
            public static boolean isActivated() { return Activation.getStatus().equals(Activation.Status.Licensed); }

            public static Status getStatus() {
                switch(getStatusFromSLMGR()) {
                    case "Unlicensed":
                        return Status.Unlicensed;

                    case "Licensed":
                        return Status.Licensed;

                    case "Out-Of-Box Grace":
                        return Status.OutOfBoxGrace;

                    case "Out-Of-Tolerance Grace":
                        return Status.OutOfToleranceGrace;

                    case "Non Genuine Grace":
                        return Status.NonGenuineGrace;

                    case "Notification":
                        return Status.Notification;

                    case "Extended Grace":
                        return Status.ExtendedGrace;

                    default:
                        return Status.Unknown;
                }
            }

            /**
             * Checks If Windows is Activated. Uses the Software Licensing Manager Script.
             * This is the quicker method.
             * <p>
             * <returns>Licensed If Genuinely Activated</returns>
             */
            public static String getStatusString() { return getStatusFromSLMGR(); }

            /**
             * Checks If Windows is Activated. Uses WMI.
             * <p>
             * <returns>Licensed If Genuinely Activated</returns>
             */
            public static String getStatusFromWMI() {
                final String ComputerName = "localhost";

                String ReturnString = "";
                try {
                    String LicenseStatus = WMI.getWMIValue("SELECT * FROM SoftwareLicensingProduct Where PartialProductKey <> null AND ApplicationId='55c92734-d682-4d71-983e-d6ec3f16059f' AND LicenseisAddon=False", "LicenseStatus");

                    switch(Integer.parseInt(LicenseStatus)) {
                        case 0:
                            ReturnString = "Unlicensed";
                            break;

                        case 1:
                            ReturnString = "Licensed";
                            break;

                        case 2:
                            ReturnString = "Out-Of-Box Grace";
                            break;

                        case 3:
                            ReturnString = "Out-Of-Tolerance Grace";
                            break;

                        case 4:
                            ReturnString = "Non Genuine Grace";
                            break;

                        case 5:
                            ReturnString = "Notification";
                            break;

                        case 6:
                            ReturnString = "Extended Grace";
                            break;

                        default:
                            ReturnString = "Unknown License Status";
                            break;
                    }
                } catch(Exception ex) {
                    System.out.println(ex.getMessage());
                }

                if(ReturnString.isEmpty()) return "Unknown License Status";
                return ReturnString;
            }

            /**
             * Checks If Windows is Activated. Uses the Software Licensing Manager Script.
             * This is the quicker method.
             * <p>
             * <returns>Licensed If Genuinely Activated</returns>
             */
            public static String getStatusFromSLMGR() {
                try {
                    while(true) {
                        Process p = Runtime.getRuntime().exec("cscript C:\\Windows\\System32\\Slmgr.vbs /dli");
                        BufferedReader stdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String s;
                        while((s = stdOut.readLine()) != null) {
                            //System.out.println(s);
                            if(s.contains("License Status: ")) {
                                return s.replace("License Status: ", "");
                            }
                        }
                    }
                } catch(Exception ex) {
                    System.out.println(ex.getMessage());
                }
                return "Error!";
            }

            public enum Status {
                Unlicensed,
                Licensed,
                OutOfBoxGrace,
                OutOfToleranceGrace,
                NonGenuineGrace,
                Notification,
                ExtendedGrace,
                Unknown
            }
        }

        /**
         * Contains boolean functions to check if certain conditions are true
         */
        public static class CheckIf {
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

        /**
         * Gets the product type of the operating system running on this Computer.
         */
        public static class Edition {
            /**
             * Gets the product type of the OS as an integer
             *
             * @return integer equivalent of the operating system product type
             */
            public static int ProductType() {
                WinNT.OSVERSIONINFOEX osVersionInfo = new WinNT.OSVERSIONINFOEX();
                if(NativeMethods.getVersionInfoFailed(osVersionInfo)) return ProductEdition.Undefined.getValue();
                return osVersionInfo.wProductType;
            }

            /**
             * Gets the product type of the OS as a string
             *
             * @return string containing the the operating system product type
             */
            public static String String() {
                switch(Version.Major()) {
                    case 5:
                        return GetVersion5();
                    case 6:
                    case 10:
                        return GetVersion6AndUp();
                }
                return "";
            }

            /**
             * Returns the product type from Windows 2000 to XP and Server 2000 to 2003
             */
            @NotNull
            private static String GetVersion5() {
                WinNT.OSVERSIONINFOEX osVersionInfo = new WinNT.OSVERSIONINFOEX();
                if(NativeMethods.getVersionInfoFailed(osVersionInfo)) return "";
                WinDef.WORD wSuiteMask = osVersionInfo.wSuiteMask;

                if(NativeMethods.getSystemMetrics(OtherConsts.SMMediaCenter)) return " Media Center";
                if(NativeMethods.getSystemMetrics(OtherConsts.SMTabletPC)) return " Tablet PC";
                if(CheckIf.isWindowsServer()) {
                    if(Version.Minor() == 0) {
                        // Windows 2000 Datacenter Server
                        if(VERSuite.parse(wSuiteMask).contains(VERSuite.Datacenter)) { return " Datacenter Server"; }
                        // Windows 2000 Advanced Server
                        if(VERSuite.parse(wSuiteMask).contains(VERSuite.Enterprise)) { return " Advanced Server"; }
                        // Windows 2000 Server
                        return " Server";
                    }
                    if(Version.Minor() == 2) {
                        // Windows Server 2003 Datacenter Edition
                        if(VERSuite.parse(wSuiteMask).contains(VERSuite.Datacenter)) { return " Datacenter Edition"; }
                        // Windows Server 2003 Enterprise Edition
                        if(VERSuite.parse(wSuiteMask).contains(VERSuite.Enterprise)) { return " Enterprise Edition"; }
                        // Windows Server 2003 Storage Edition
                        if(VERSuite.parse(wSuiteMask).contains(VERSuite.StorageServer)) { return " Storage Edition"; }
                        // Windows Server 2003 Compute Cluster Edition
                        if(VERSuite.parse(wSuiteMask).contains(VERSuite.ComputeServer)) {
                            return " Compute Cluster Edition";
                        }
                        // Windows Server 2003 Web Edition
                        if(VERSuite.parse(wSuiteMask).contains(VERSuite.Blade)) { return " Web Edition"; }
                        // Windows Server 2003 Standard Edition
                        return " Standard Edition";
                    }
                } else {
                    //Windows XP Embedded
                    if(VERSuite.parse(wSuiteMask).contains(VERSuite.EmbeddedNT)) { return " Embedded"; }
                    // Windows XP / Windows 2000 Professional
                    return (VERSuite.parse(wSuiteMask).contains(VERSuite.Personal)) ? " Home" : " Professional";
                }
                return "";
            }

            /**
             * Returns the product type from Windows Vista to 10 and Server 2008 to 2016
             */
            @NotNull
            private static String GetVersion6AndUp() {
                switch(ProductEdition.parse(getProductInfo())) {
                    case Ultimate:
                    case UltimateE:
                    case UltimateN:
                        return "Ultimate";

                    case Professional:
                    case ProfessionalE:
                    case ProfessionalN:
                        return "Professional";

                    case HomePremium:
                    case HomePremiumE:
                    case HomePremiumN:
                        return "Home Premium";

                    case HomeBasic:
                    case HomeBasicE:
                    case HomeBasicN:
                        return "Home Basic";

                    case Enterprise:
                    case EnterpriseE:
                    case EnterpriseN:
                    case EnterpriseServerV:
                        return "Enterprise";

                    case Business:
                    case BusinessN:
                        return "Business";

                    case Starter:
                    case StarterE:
                    case StarterN:
                        return "Starter";

                    case ClusterServer:
                        return "Cluster Server";

                    case DatacenterServer:
                    case DatacenterServerV:
                        return "Datacenter";

                    case DatacenterServerCore:
                    case DatacenterServerCoreV:
                        return "Datacenter (Core installation)";

                    case EnterpriseServer:
                        return "Enterprise";

                    case EnterpriseServerCore:
                    case EnterpriseServerCoreV:
                        return "Enterprise (Core installation)";

                    case EnterpriseServerIA64:
                        return "Enterprise For Itanium-based Systems";

                    case SmallBusinessServer:
                        return "Small Business Server";
                    //case SmallBusinessServerPremium:
                    //  return "Small Business Server Premium Edition";
                    case ServerForSmallBusiness:
                    case ServerForSmallBusinessV:
                        return "Windows Essential Server Solutions";

                    case StandardServer:
                    case StandardServerV:
                        return "Standard";

                    case StandardServerCore:
                    case StandardServerCoreV:
                        return "Standard (Core installation)";

                    case WebServer:
                    case WebServerCore:
                        return "Web Server";

                    case MediumBusinessServerManagement:
                    case MediumBusinessServerMessaging:
                    case MediumBusinessServerSecurity:
                        return "Windows Essential Business Server";

                    case StorageEnterpriseServer:
                    case StorageExpressServer:
                    case StorageStandardServer:
                    case StorageWorkgroupServer:
                        return "Storage Server";
                }
                return "";
            }

            private static int getProductInfo() {
                return NativeMethods.getProductInfo(Version.Major(), Version.Minor());
            }
        }

        /**
         * Gets the different names provided by the operating system.
         */
        public static class Name {
            public static OSList Enum() {
                switch(Version.Number()) {
                    case 51:
                        return OSList.WindowsXP;

                    case 52:
                        return isWindowsServer() ? (NativeMethods.getSystemMetrics(OtherConsts.SMServerR2) ? OSList.Windows2003R2 : OSList.Windows2003) : OSList.WindowsXP64;
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
                                return isWindowsServer() ? (NativeMethods.getSystemMetrics(OtherConsts.SMServerR2) ? "Windows Server 2003 R2" : "Windows Server 2003") : "WindowsXP x64";
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

        /**
         * Gets the service pack information of the operating system running on this Computer.
         */
        public static class ServicePack {
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

        /**
         * Gets information about the current Windows installation
         */
        public static class SystemInformation {
            /**
             * Gets information about the current Windows installation as text
             *
             * @return string
             */
            @NotNull
            public static String getInfo() throws IOException {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec("systeminfo");
                BufferedReader systemInformationReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while((line = systemInformationReader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(System.lineSeparator());
                }

                return stringBuilder.toString().trim();
            }

            /**
             * Gets current system time
             *
             * @return string
             */
            public static String getTime() {
                WinBase.SYSTEMTIME time = new WinBase.SYSTEMTIME();
                NativeMethods.Kernel32.INSTANCE.GetSystemTime(time);
                return time.wMonth + "/" + time.wDay + "/" + time.wYear + " " + time.wHour + ":" + time.wMinute;
            }
        }

        /**
         * Gets info about the currently logged in user account.
         */
        public static class UserInfo {
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

        /**
         * Gets the full version of the operating system running on this Computer.
         */
        public static class Version {
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
                    String VersionString = WMI.getWMIValue("SELECT * FROM " + WMIClasses.OS.OperatingSystem,
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

        public static class WMI {
            private static final String CRLF = "\r\n";

            /**
             * Generate a VBScript string capable of querying the desired WMI information.
             *
             * @param wmiQueryStr                the query string to be passed to the WMI sub-system. <br>i.e. "Select *
             *                                   from Win32_ComputerSystem"
             * @param wmiCommaSeparatedFieldName a comma separated list of the WMI fields to be collected from the query
             *                                   results. <br>i.e. "Model"
             * @return the vbscript string.
             */
            @NotNull
            private static String getVBScript(String wmiQueryStr, String wmiCommaSeparatedFieldName) {
                StringBuilder vbs = new StringBuilder();
                vbs.append("Dim oWMI : Set oWMI = GetObject(\"winmgmts:\")").append(CRLF);
                vbs.append(String.format("Dim classComponent : Set classComponent = oWMI.ExecQuery(\"%s\")", wmiQueryStr)).append(CRLF);
                vbs.append("Dim obj, strData").append(CRLF);
                vbs.append("For Each obj in classComponent").append(CRLF);
                String[] wmiFieldNameArray = wmiCommaSeparatedFieldName.split(",");
                for(String fieldName : wmiFieldNameArray) {
                    vbs.append(String.format("  strData = strData & obj.%s & VBCrLf", fieldName)).append(CRLF);
                }
                vbs.append("Next").append(CRLF);
                vbs.append("wscript.echo strData").append(CRLF);
                return vbs.toString();
            }

            /**
             * Get an environment variable from Windows
             *
             * @param variableName The name of the environment variable to get
             * @return The value of the environment variable
             */
            public static String getEnvironmentVar(String variableName) {
                String varName = "%" + variableName + "%";
                String variableValue = CommandInfo.Run("cmd", "/C echo " + varName).Result.get(0);
                //execute(new String[] {"cmd.exe", "/C", "echo " + varName});
                variableValue = variableValue.replace("\"", "");
                if(variableValue.equals(varName)) {
                    throw new IllegalArgumentException(String.format("Environment variable '%s' does not exist!", variableName));
                }
                return variableValue;
            }

            /**
             * Write the specified string to the specified file
             *
             * @param filename the file to write the string to
             * @param data     a string to be written to the file
             */
            private static void writeStringToFile(String filename, String data) {
                try {
                    FileWriter output = new FileWriter(filename);
                    output.write(data);
                    output.flush();
                    output.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            /**
             * Get the given WMI value from the WMI subsystem on the local computer
             *
             * @param wmiQueryStr                the query string as syntactically defined by the WMI reference
             * @param wmiCommaSeparatedFieldName the field object that you want to get out of the query results
             * @return the value
             */
            @NotNull
            public static String getWMIValue(String wmiQueryStr, String wmiCommaSeparatedFieldName) {
                String tmpFileName = getEnvironmentVar("TEMP").trim() + File.separator + "javawmi.vbs";
                writeStringToFile(tmpFileName, getVBScript(wmiQueryStr, wmiCommaSeparatedFieldName));
                String output = CommandInfo.Run("cmd.exe", "/C cscript.exe " + tmpFileName).Result.toString();
                new File(tmpFileName).delete();

                return output.trim();
            }

            public static void executeDemoQueries() {
                try {
                    System.out.println(getWMIValue("Select * from Win32_ComputerSystem", "Model"));
                    System.out.println(getWMIValue("Select Name from Win32_ComputerSystem", "Name"));
                    //System.out.println(getWMIValue("Select Description from Win32_PnPEntity", "Description"));
                    //System.out.println(getWMIValue("Select Description, Manufacturer from Win32_PnPEntity", "Description,Manufacturer"));
                    //System.out.println(getWMIValue("Select * from Win32_Service WHERE State = 'Stopped'", "Name"));
                    //this will return everything since the field is incorrect and was not used to a filter
                    //System.out.println(getWMIValue("Select * from Win32_Service", "Name"));
                    //this will return nothing since there is no field specified
                    System.out.println(getWMIValue("Select Name from Win32_ComputerSystem", ""));
                    //this is a failing case where the Win32_Service class does not contain the 'Name' field
                    //System.out.println(getWMIValue("Select * from Win32_Service", "Name"));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
