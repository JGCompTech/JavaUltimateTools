package com.jgcomptech.tools;

import com.jgcomptech.tools.enums.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.jgcomptech.tools.OSInfo.CheckIf.*;
import static com.jgcomptech.tools.OSInfo.Windows.CheckIf.isWin8OrLater;
import static com.jgcomptech.tools.OSInfo.Windows.CheckIf.isWindowsServer;
import static java.nio.charset.StandardCharsets.UTF_8;

/** Returns information about the operating system. */
public final class OSInfo {
    /** Determines if the current application is 32 or 64-bit. */
    public static final class Architecture {
        /**
         * Determines if the current application is 32 or 64-bit.
         * @return if computer is 32 bit or 64 bit as string
         */
        public static String String() { return is64BitOS() ? "64 bit" : "32 bit"; }

        /**
         * Determines if the current application is 32 or 64-bit.
         * @return if computer is 32 bit or 64 bit as int
         */
        public static int Number() { return is64BitOS() ? 64 : 32; }

        /** Prevents instantiation of this utility class. */
        private Architecture() { }
    }

    /** Runs different checks against the OS and returns a boolean value. */
    public static final class CheckIf {
        private static final String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

        /**
         * Identifies if OS is a 32 Bit OS.
         * @return True if OS is a 32 Bit OS
         */
        public static boolean is32BitOS() { return !is64BitOS(); }

        /**
         * Identifies if OS is a 64 Bit OS.
         * @return True if OS is a 64 Bit OS
         */
        public static boolean is64BitOS() {
            return isWindows() ? System.getenv("ProgramFiles(x86)") != null : OS.contains("64");
        }

        /**
         * Identifies if OS is Windows.
         * @return True if OS is Windows
         */
        public static boolean isWindows() { return (OS.contains("win")); }

        /**
         * Identifies if OS is MacOSX.
         * @return True if OS is MacOSX
         */
        public static boolean isMac() { return (OS.contains("mac")) || (OS.contains("darwin")); }

        /**
         * Identifies if OS is a distro of Linux.
         * @return True if OS is Linux
         */
        public static boolean isLinux() { return ((OS.contains("nix") || OS.contains("nux")) && !OS.contains("aix")); }

        /**
         * Identifies if OS is Solaris.
         * @return True if OS is Solaris
         */
        public static boolean isSolaris() { return (OS.contains("sunos")); }

        /** Prevents instantiation of this utility class. */
        private CheckIf() { }
    }

    /** Returns the different names provided by the operating system. */
    public static final class Name {
        private static final String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

        /**
         * Returns a full version String, ex.: "Windows XP SP2 (32 Bit)".
         * @return String representing a fully displayable version as stored in WMI
         * @throws IOException if error occurs
         * @throws InterruptedException if command is interrupted
         */
        public static String StringExpanded() throws IOException, InterruptedException {
            if(isWindows()) {
                final var SPString = isWin8OrLater() ? " - " + Windows.Version.Build()
                        : " SP" + Windows.ServicePack.String().replace("Service Pack ", "");

                return String() + ' ' + Windows.Edition.String() + ' '
                        + SPString + " (" + Architecture.Number() + " Bit)";
            } else return OS;
        }

        /**
         * Returns a full version String, ex.: "Windows XP SP2 (32 Bit)".
         * @return String representing a fully displayable version as stored in Windows Registry
         * @throws IOException if error occurs
         * @throws InterruptedException if command is interrupted
         */
        public static String StringExpandedFromRegistry() throws IOException, InterruptedException {
            if(isWindows()) {
                final var SPString = isWin8OrLater() ? " - " + Windows.Version.Build()
                        : " SP" + Windows.ServicePack.String().replace("Service Pack ", "");

                final var key = "Software\\\\Microsoft\\\\Windows NT\\\\CurrentVersion";
                final var value = "ProductName";
                final var text = RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
                return text + ' ' + SPString + " (" + Architecture.Number() + " Bit)";
            } else return OS;
        }

        /**
         * Returns the name of the operating system running on this Computer.
         * @return Enum value containing the the operating system name
         * @throws IOException if error occurs
         * @throws InterruptedException if command is interrupted
         */
        public static OSList Enum() throws IOException, InterruptedException {
            if(isWindows()) return Windows.Name.Enum();
            else if(isMac()) return OSList.MacOSX;
            else if(isLinux()) return OSList.Linux;
            else if(isSolaris()) return OSList.Solaris;
            return OSList.Unknown;
        }

        /**
         * Returns the name of the operating system running on this computer.
         * @return String value containing the the operating system name
         * @throws IOException if error occurs
         * @throws InterruptedException if command is interrupted
         */
        public static String String() throws IOException, InterruptedException {
            if(isWindows()) return Windows.Name.String();
            else if(isMac()) return "Mac OSX";
            else if(isLinux()) return "Linux";
            else if(isSolaris()) return "Solaris";
            return "UNKNOWN";
        }

        /**
         * Returns the current computer name.
         * @return String value of current computer name
         */
        public static String ComputerNameActive() {
            final var key = "System\\ControlSet001\\Control\\ComputerName\\ActiveComputerName";
            final var value = "ComputerName";
            return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        }

        /**
         * Returns the pending computer name that it will update to on reboot.
         * @return String value of the pending computer name
         */
        public static String ComputerNamePending() {
            final var key = "System\\ControlSet001\\Control\\ComputerName\\ComputerName";
            final var value = "ComputerName";
            final var text = RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            return text.trim().isEmpty() ? "N/A" : text;
        }

        /** Prevents instantiation of this utility class. */
        private Name() { }
    }

    /** Returns information about the Windows installation. */
    public static final class Windows {
        /** Returns information about the Windows activation status. */
        public static final class Activation {
            /**
             * Identifies if OS is activated.
             * @return true if activated, false if not activated
             * @throws IOException if error occurs
             */
            public static boolean isActivated() throws IOException { return getStatusAsEnum() == Status.Licensed; }

            /**
             * Identifies If Windows is Activated, uses the Software Licensing Manager Script,
             * this is the quicker method.
             * @return "Licensed" If Genuinely Activated as enum
             * @throws IOException if error occurs
             */
            public static Status getStatusAsEnum() throws IOException {
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
             * Identifies If Windows is Activated, uses the Software Licensing Manager Script,
             * this is the quicker method.
             * @return "Licensed" If Genuinely Activated
             * @throws IOException if an error occurs
             */
            public static String getStatusString() throws IOException { return getStatusFromSLMGR(); }

            /**
             * Identifies If Windows is Activated, uses WMI.
             * @return Licensed If Genuinely Activated
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static String getStatusFromWMI() throws IOException, InterruptedException {
                final var ComputerName = "localhost";

                final var LicenseStatus = WMI.getWMIValue("SELECT * "
                        + "FROM SoftwareLicensingProduct "
                        + "Where PartialProductKey <> null "
                        + "AND ApplicationId='55c92734-d682-4d71-983e-d6ec3f16059f' "
                        + "AND LicenseisAddon=False", "LicenseStatus");

                switch(Integer.parseInt(LicenseStatus)) {
                    case 0:
                        return "Unlicensed";
                    case 1:
                        return "Licensed";
                    case 2:
                        return "Out-Of-Box Grace";
                    case 3:
                        return "Out-Of-Tolerance Grace";
                    case 4:
                        return "Non Genuine Grace";
                    case 5:
                        return "Notification";
                    case 6:
                        return "Extended Grace";
                    default:
                        return "Unknown License Status";
                }
            }

            /**
             * Identifies If Windows is Activated, uses the Software Licensing Manager Script,
             * this is the quicker method.
             * @return Licensed If Genuinely Activated
             * @throws IOException if an error occurs
             */
            public static String getStatusFromSLMGR() throws IOException {
                while(true) {
                    final var p = Runtime.getRuntime().exec(
                            "cscript C:\\Windows\\System32\\Slmgr.vbs /dli");
                    try(final var stdOut = new BufferedReader(new InputStreamReader(p.getInputStream(), UTF_8))) {
                        String s;
                        while((s = stdOut.readLine()) != null) {
                            //System.out.println(s);
                            if(s.contains("License Status: ")) {
                                return s.replace("License Status: ", "");
                            }
                        }
                    }
                }
            }


            /** A list of Activation statuses that are the result of the methods in the {@link Activation} class. */
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

            /** Prevents instantiation of this utility class. */
            private Activation() { }
        }

        /** Contains boolean functions to check if certain conditions are true. */
        public static final class CheckIf {
            /**
             * Identifies if OS is activated.
             * @return true if activated, false if not activated
             * @throws IOException if error occurs
             */
            public static boolean isActivated() throws IOException { return Activation.isActivated(); }

            /**
             * Identifies if OS is a Windows Server OS.
             * @return true if OS is a Windows Server OS
             */
            public static boolean isWindowsServer() {
                return ProductType.parse(Edition.ProductType()) != ProductType.NTWorkstation;
            }

            /**
             * Identifies if OS is a Windows Domain Controller.
             * @return true if OS is a Windows Server OS
             */
            public static boolean isWindowsDomainController() {
                return ProductType.parse(Edition.ProductType()) == ProductType.NTDomainController;
            }

            /**
             * Identifies if computer has joined a domain.
             * @return true if computer has joined a domain
             * @throws UnknownHostException if error occurs
             */
            //TODO Add C# implementation
            public static boolean isDomainJoined() throws UnknownHostException {
                return !UserInfo.CurrentMachineName().equals(UserInfo.CurrentDomainName());
            }

            /**
             * Identifies if the current user is an account administrator.
             * @return true if current user is an account administrator
             */
            //TODO Add C# implementation
            public static boolean isCurrentUserAdmin() {
                var isAdmin = false;
                final var groups = Advapi32Util.getCurrentUserGroups();
                for(final var group : groups) {
                    final var sid = new WinNT.PSIDByReference();
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
             * Identifies if OS is XP or later.
             * @return true if XP or later, false if 2000 or previous
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static boolean isWinXPOrLater() throws IOException, InterruptedException {
                return Version.Number() >= 51; }

            /**
             * Identifies if OS is XP x64 or later.
             * @return true if XP x64 or later, false if XP or previous
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static boolean isWinXP64OrLater() throws IOException, InterruptedException {
                return Version.Number() >= 52; }

            /**
             * Identifies if OS is Vista or later.
             * @return true if Vista or later, false if XP or previous
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static boolean isWinVistaOrLater() throws IOException, InterruptedException {
                return Version.Number() >= 60; }

            /**
             * Identifies if OS is Windows 7 or later.
             * @return true if Windows 7 or later, false if Vista or previous
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static boolean isWin7OrLater() throws IOException, InterruptedException {
                return Version.Number() >= 61; }

            /**
             * Identifies if OS is Windows 8 or later.
             * @return true if Windows 8 or later, false if Windows 7 or previous
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static boolean isWin8OrLater() throws IOException, InterruptedException {
                return Version.Number() >= 62; }

            /**
             * Identifies if OS is Windows 8.1 or later.
             * @return true if Windows 8.1 or later, false if Windows 8 or previous
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static boolean isWin81OrLater() throws IOException, InterruptedException {
                return Version.Number() >= 63; }

            /**
             * Identifies if OS is Windows 10 or later.
             * @return true if Windows 10 or later, false if Windows 10 or previous
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static boolean isWin10OrLater() throws IOException, InterruptedException {
                return Version.Number() >= 100; }

            /** Prevents instantiation of this utility class. */
            private CheckIf() { }
        }

        /** Returns the product type of the operating system running on this Computer. */
        public static final class Edition {
            /**
             * Returns the product type of the OS as an integer.
             * @return integer equivalent of the operating system product type
             */
            public static int ProductType() {
                final var osVersionInfo = new WinNT.OSVERSIONINFOEX();
                if(NativeMethods.getVersionInfoFailed(osVersionInfo)) return ProductEdition.Undefined.getValue();
                return osVersionInfo.wProductType;
            }

            /**
             * Returns the product type of the OS as a string.
             * @return string containing the the operating system product type
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static String String() throws IOException, InterruptedException {
                switch(Version.Major()) {
                    case 5:
                        return GetVersion5();
                    case 6:
                    case 10:
                        return GetVersion6AndUp();
                    default:
                        return "";
                }
            }

            /**
             * Returns the product type from Windows 2000 to XP and Server 2000 to 2003.
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             * @return the version string
             */
            private static String GetVersion5() throws IOException, InterruptedException {
                final var osVersionInfo = new WinNT.OSVERSIONINFOEX();
                if(NativeMethods.getVersionInfoFailed(osVersionInfo)) return "";
                final var wSuiteMask = osVersionInfo.wSuiteMask;

                if(NativeMethods.getSystemMetrics(OtherConsts.SMMediaCenter)) return " Media Center";
                if(NativeMethods.getSystemMetrics(OtherConsts.SMTabletPC)) return " Tablet PC";
                if(isWindowsServer()) {
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
             * Returns the product type from Windows Vista to 10 and Server 2008 to 2016.
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             * @return the version string
             */
            private static String GetVersion6AndUp() throws IOException, InterruptedException {
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
                    case Undefined:
                        return "";
                }
                return "";
            }

            private static int getProductInfo() throws IOException, InterruptedException {
                return NativeMethods.getProductInfo(Version.Major(), Version.Minor());
            }

            /** Prevents instantiation of this utility class. */
            private Edition() { }
        }

        /** Returns the different names provided by the operating system. */
        public static final class Name {
            /**
             * Returns the OS name.
             * @return OS name as enum
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static OSList Enum() throws IOException, InterruptedException {
                switch(Version.Number()) {
                    case 51:
                        return OSList.WindowsXP;

                    case 52:
                        return isWindowsServer() ? (NativeMethods.getSystemMetrics(OtherConsts.SMServerR2)
                                ? OSList.Windows2003R2 : OSList.Windows2003) : OSList.WindowsXP64;

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

                    default:
                        return OSList.Windows2000AndPrevious;
                }
            }

            /**
             * Returns the OS name.
             * @return OS name as string
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static String String() throws IOException, InterruptedException {
                switch(Version.Major()) {
                    case 5: {
                        switch(Version.Minor()) {
                            case 1:
                                return "Windows XP";
                            case 2:
                                return isWindowsServer() ? (NativeMethods.getSystemMetrics(OtherConsts.SMServerR2)
                                        ? "Windows Server 2003 R2" : "Windows Server 2003") : "WindowsXP x64";
                            default:
                                return "UNKNOWN";
                        }
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
                            default:
                                return "UNKNOWN";
                        }
                    }
                    case 10: {
                        switch(Version.Minor()) {
                            case 0:
                                return isWindowsServer() ? "Windows 2016" : "Windows 10";
                            default:
                                return "UNKNOWN";
                        }
                    }
                    default:
                        return "UNKNOWN";
                }
            }

            /** Prevents instantiation of this utility class. */
            private Name() { }
        }

        /** Returns the service pack information of the operating system running on this Computer. */
        public static final class ServicePack {
            /**
             * Returns the service pack information of the operating system running on this Computer.
             * @return A String containing the operating system service pack information
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static String String() throws IOException, InterruptedException {
                final var sp = Integer.toString(Number());
                return isWin8OrLater() ? "" : sp.trim().isEmpty() ? "Service Pack 0" : "Service Pack " + sp;
            }

            /**
             * Returns the service pack information of the operating system running on this Computer.
             * @return A int containing the operating system service pack number
             */
            public static int Number() {
                final var osVersionInfo = new WinNT.OSVERSIONINFOEX();
                return NativeMethods.getVersionInfoFailed(osVersionInfo)
                        ? -1
                        : osVersionInfo.wServicePackMajor.intValue();
            }

            /** Prevents instantiation of this utility class. */
            private ServicePack() { }
        }

        /** Returns information about the current Windows installation. */
        public static final class SystemInformation {
            /**
             * Returns information about the current Windows installation as text.
             * @return Information as string
             * @throws IOException if command cannot be run
             */
            public static String getInfo() throws IOException {
                final var runtime = Runtime.getRuntime();
                final var process = runtime.exec("systeminfo");

                try(final var systemInformationReader =
                            new BufferedReader(new InputStreamReader(process.getInputStream(), UTF_8))) {
                    return systemInformationReader
                            .lines()
                            .map(line -> line + System.lineSeparator())
                            .collect(Collectors.joining())
                            .trim();
                }
            }

            /**
             * Returns current system time.
             * @return Current time as string
             */
            public static String getTime() {
                final var time = new WinBase.SYSTEMTIME();
                NativeMethods.Kernel32.INSTANCE.GetSystemTime(time);
                return time.wMonth + "/" + time.wDay + '/' + time.wYear + ' ' + time.wHour + ':' + time.wMinute;
            }

            /** Prevents instantiation of this utility class. */
            private SystemInformation() { }
        }

        /** Returns info about the currently logged in user account. */
        public static final class UserInfo {
            /**
             * Returns the current Registered Organization.
             * @return Registered Organization as string
             */
            public static String RegisteredOrganization() {
                final var key = "Software\\Microsoft\\Windows NT\\CurrentVersion";
                final var value = "RegisteredOrganization";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }

            /**
             * Returns the current Registered Owner.
             * @return Registered Owner as string
             */
            public static String RegisteredOwner() {
                final var key = "Software\\Microsoft\\Windows NT\\CurrentVersion";
                final var value = "RegisteredOwner";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }

            /**
             * Returns the user name of the person who is currently logged on to the Windows operating system.
             * @return Logged in username as string
             * @throws IllegalStateException if cannot retrieve the logged-in username
             */
            @SuppressWarnings("StringSplitter")
            public static String LoggedInUserName() {
                final var userNameBuf = new char[10000];
                final var size = new IntByReference(userNameBuf.length);
                final var result = NativeMethods.Secur32.INSTANCE.GetUserNameEx(
                        Secur32.EXTENDED_NAME_FORMAT.NameSamCompatible, userNameBuf, size);

                if(!result)
                    throw new IllegalStateException("Cannot retrieve name of the currently logged-in user");

                final var username = new String(userNameBuf, 0, size.getValue()).split("\\\\");
                return username[1];
            }

            /**
             * Returns the network domain name associated with the current user.
             * @return Current domain name as string
             * @throws IllegalStateException if cannot retrieve the joined domain
             */
            @SuppressWarnings("StringSplitter")
            public static String CurrentDomainName() {
                final var userNameBuf = new char[10000];
                final var size = new IntByReference(userNameBuf.length);
                final var result = NativeMethods.Secur32.INSTANCE.GetUserNameEx(
                        Secur32.EXTENDED_NAME_FORMAT.NameSamCompatible, userNameBuf, size);

                if(!result)
                    throw new IllegalStateException("Cannot retrieve name of the currently joined domain");

                final var username = new String(userNameBuf, 0, size.getValue()).split("\\\\");
                return username[0];
            }

            /**
             * Returns the current host name for the system.
             * @return Current domain name as string
             * @throws UnknownHostException if error occurs
             */
            public static String CurrentMachineName() throws UnknownHostException {
                final var localMachineIP = InetAddress.getLocalHost();
                return localMachineIP.getHostName();
            }

            /** Prevents instantiation of this utility class. */
            private UserInfo() { }
        }

        /** Returns the full version of the operating system running on this Computer. */
        public static final class Version {
            /**
             * Returns the full version of the operating system running on this Computer.
             * @return Full version as string
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static String Main() throws IOException, InterruptedException { return getVersionInfo(Type.Main); }

            /**
             * Returns the major version of the operating system running on this Computer.
             * @return Major version as int
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static int Major() throws IOException, InterruptedException {
                return Integer.parseInt(getVersionInfo(Type.Major)); }

            /**
             * Returns the minor version of the operating system running on this Computer.
             * @return Minor version as int
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static int Minor() throws IOException, InterruptedException {
                return Integer.parseInt(getVersionInfo(Type.Minor)); }

            /**
             * Returns the build version of the operating system running on this Computer.
             * @return Build version as int
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static int Build() throws IOException, InterruptedException {
                return Integer.parseInt(getVersionInfo(Type.Build)); }

            /**
             * Returns the revision version of the operating system running on this Computer.
             * @return Build Revision as int
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static int Revision() throws IOException, InterruptedException {
                return Integer.parseInt(getVersionInfo(Type.Revision)); }

            /**
             * Returns a numeric value representing OS version.
             * @return OSMajorVersion times 10 plus OSMinorVersion
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static int Number() throws IOException, InterruptedException { return (Major() * 10 + Minor()); }

            static String getVersionInfo(final Type type) throws IOException, InterruptedException {
                final var VersionString = WMI.getWMIValue("SELECT * FROM "
                        + WMIClasses.OS.OperatingSystem,"Version");

                String Temp;
                final var Major = VersionString.substring(0, VersionString.indexOf("."));
                Temp = VersionString.substring(Major.length() + 1);
                final var Minor = Temp.substring(0, VersionString.indexOf(".") - 1);
                Temp = VersionString.substring(Major.length() + 1 + Minor.length() + 1);
                final String Build;
                if(Temp.contains(".")) {
                    Build = Temp.substring(0, VersionString.indexOf(".") - 1);
                    Temp = VersionString.substring(Major.length() + 1 + Minor.length() + 1 + Build.length() + 1);
                } else {
                    Build = Temp;
                    Temp = "0";
                }
                final var Revision = Temp;

                switch(type) {
                    case Main:
                        return VersionString;
                    case Major:
                        return Major;
                    case Minor:
                        return Minor;
                    case Build:
                        return Build;
                    case Revision:
                        return Revision;
                }
                return "0";
            }


            /** A list of Version types used in the {@link OSInfo.Windows.Version} class. */
            public enum Type {
                Main,
                Major,
                Minor,
                Build,
                Revision
            }

            /** Prevents instantiation of this utility class. */
            private Version() { }
        }

        /** Returns information from WMI. */
        public static final class WMI {
            private static final String CRLF = System.lineSeparator();

            /**
             * Generate a VBScript string capable of querying the desired WMI information.
             * @param wmiQueryStr                the query string to be passed to the WMI sub-system. <br>i.e. "Select *
             *                                   FROM Win32_ComputerSystem"
             * @param wmiCommaSeparatedFieldName a comma separated list of the WMI fields to be collected from the query
             *                                   results. <br>i.e. "Model"
             * @return the vbscript string.
             */
            @SuppressWarnings("StringSplitter")
            private static String getVBScript(final String wmiQueryStr, final String wmiCommaSeparatedFieldName) {
                final var vbs = new StringBuilder();
                vbs.append("Dim oWMI : Set oWMI = GetObject(\"winmgmts:\")").append(CRLF);
                vbs.append(String.format("Dim classComponent : Set classComponent = oWMI.ExecQuery(\"%s\")",
                        wmiQueryStr)).append(CRLF);
                vbs.append("Dim obj, strData").append(CRLF);
                vbs.append("For Each obj in classComponent").append(CRLF);
                final var wmiFieldNameArray = wmiCommaSeparatedFieldName.split(",");
                for(final var fieldName : wmiFieldNameArray) {
                    vbs.append(String.format("  strData = strData & obj.%s & VBCrLf", fieldName)).append(CRLF);
                }
                vbs.append("Next").append(CRLF);
                vbs.append("wscript.echo strData").append(CRLF);
                return vbs.toString();
            }

            /**
             * Get an environment variable from Windows.
             * @param variableName The name of the environment variable to get
             * @return The value of the environment variable
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             * @throws IllegalArgumentException if Environment Variable does't exist
             */
            public static String getEnvironmentVar(final String variableName) throws IOException, InterruptedException {
                final var varName = '%' + variableName + '%';
                var variableValue = CommandInfo.Run("cmd", "/C echo " + varName).Result.get(0);
                //execute(new String[] {"cmd.exe", "/C", "echo " + varName});
                variableValue = variableValue.replace("\"", "");
                if(variableValue.equals(varName)) {
                    throw new IllegalArgumentException(
                            String.format("Environment variable '%s' does not exist!", variableName));
                }
                return variableValue;
            }

            /**
             * Write the specified string to the specified file.
             * @param filename the file to write the string to
             * @param data     a string to be written to the file
             * @throws IOException if error occurs
             */
            private static void writeStringToFile(final String filename, final String data) throws IOException {
                try(final Writer output = Files.newBufferedWriter(Paths.get(filename), UTF_8)) {
                    output.write(data);
                    output.flush();
                }
            }

            /**
             * Get the given WMI value from the WMI subsystem on the local computer.
             * @param wmiQueryStr                the query string as syntactically defined by the WMI reference
             * @param wmiCommaSeparatedFieldName the field object that you want to get out of the query results
             * @return the value
             * @throws IOException if error occurs
             * @throws InterruptedException if command is interrupted
             */
            public static String getWMIValue(final String wmiQueryStr, final String wmiCommaSeparatedFieldName)
                    throws IOException, InterruptedException {
                final var tmpFileName = getEnvironmentVar(
                        "TEMP").trim() + File.separator + "javawmi.vbs";
                writeStringToFile(tmpFileName, getVBScript(wmiQueryStr, wmiCommaSeparatedFieldName));
                final var output = CommandInfo.Run("cmd.exe", "/C cscript.exe " + tmpFileName)
                        .Result.toString();
                Files.delete(Paths.get(tmpFileName));

                return output.trim();
            }

            public static void executeDemoQueries() throws IOException, InterruptedException {
                System.out.println(getWMIValue("Select * FROM Win32_ComputerSystem",
                        "Model"));
                System.out.println(getWMIValue("Select Name FROM Win32_ComputerSystem",
                        "Name"));
                //System.out.println(getWMIValue("Select Description FROM Win32_PnPEntity", "Description"));
                //System.out.println(getWMIValue("Select Description, Manufacturer FROM Win32_PnPEntity",
                // "Description,Manufacturer"));
                //System.out.println(getWMIValue("Select * FROM Win32_Service WHERE State = 'Stopped'", "Name"));
                //this will return everything since the field is incorrect and was not used to a filter
                //System.out.println(getWMIValue("Select * FROM Win32_Service", "Name"));
                //this will return nothing since there is no field specified
                System.out.println(getWMIValue("Select Name FROM Win32_ComputerSystem",
                        ""));
                //this is a failing case WHERE the Win32_Service class does not contain the 'Name' field
                //System.out.println(getWMIValue("Select * FROM Win32_Service", "Name"));
            }

            /** Prevents instantiation of this utility class. */
            private WMI() { }
        }

        /** Prevents instantiation of this utility class. */
        private Windows() { }
    }

    /** An Install Info Object for use with the {@link ComputerInfo} class. */
    public static final class InstallInfoObject {
        String ActivationStatus = "";
        String Architecture = "";
        String NameExpanded = "";
        String NameExpandedFromRegistry = "";
        String Name = "";
        String ServicePack = "";
        int ServicePackNumber;
        VersionObject Version = new VersionObject();

        public String ActivationStatus() { return ActivationStatus; }

        public String Architecture() { return Architecture; }

        public String NameExpanded() { return NameExpanded; }

        public String NameExpandedFromRegistry() { return NameExpandedFromRegistry; }

        public String Name() { return Name; }

        public String ServicePack() { return ServicePack; }

        public int ServicePackNumber() { return ServicePackNumber; }

        public VersionObject Version() { return Version; }
    }

    /** A Version Object for use with the {@link ComputerInfo} class. */
    public static final class VersionObject {
        String Main;
        int Major;
        int Minor;
        int Build;
        int Revision;
        int Number;

        public String Main() { return Main; }

        public int Major() { return Major; }

        public int Minor() { return Minor; }

        public int Build() { return Build; }

        public int Revision() { return Revision; }

        public int Number() { return Number; }
    }

    /** An Operating System Object for use with the {@link ComputerInfo} class. */
    public static final class OSObject {
        String ComputerName;
        String ComputerNamePending;
        InstallInfoObject InstallInfo;
        String RegisteredOrganization;
        String RegisteredOwner;
        String LoggedInUserName;
        String DomainName;

        public String ComputerName() { return ComputerName; }

        public String ComputerNamePending() { return ComputerNamePending; }

        public InstallInfoObject InstallInfo() { return InstallInfo; }

        public String RegisteredOrganization() { return RegisteredOrganization; }

        public String RegisteredOwner() { return RegisteredOwner; }

        public String LoggedInUserName() { return LoggedInUserName; }

        public String DomainName() { return DomainName; }
    }

    /** Prevents instantiation of this utility class. */
    private OSInfo() { }
}
