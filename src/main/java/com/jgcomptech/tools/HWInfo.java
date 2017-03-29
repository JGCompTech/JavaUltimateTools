package com.jgcomptech.tools;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.ShlObj;
import com.sun.jna.platform.win32.WinDef;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

import static com.jgcomptech.tools.Misc.ConvertBytes;
import static com.jgcomptech.tools.OSInfo.CheckIf.*;

/** Returns information about the system hardware */
public class HWInfo {
    /** Returns information about the system BIOS */
    public static class BIOS {
        /**
         * Returns the system BIOS release date stored in the registry
         *
         * @return BIOS date as string
         */
        @NotNull
        public static String getReleaseDate() {
            if(isWindows()) {
                String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
                String value = "BIOSReleaseDate";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }
            return "Unknown";
        }

        /**
         * Returns the system BIOS version stored in the registry
         *
         * @return BIOS version as string
         */
        @NotNull
        public static String getVersion() {
            if(isWindows()) {
                String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
                String value = "BIOSVersion";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }
            return "Unknown";
        }

        /**
         * Returns the system BIOS vendor name stored in the registry
         *
         * @return BIOS vendor name as string
         */
        @NotNull
        public static String getVendor() {
            if(isWindows()) {
                String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
                String value = "BIOSVendor";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }
            return "Unknown";
        }

        // This class should only be called statically
        private BIOS() { super(); }
    }

    /** Returns information about the current network */
    public static class Network {
        /**
         * Returns the Internal IP Address
         *
         * @return Internal IP Address as string
         */
        public static String getInternalIPAddress() {
            try {
                String ip = (InetAddress.getLocalHost().getHostAddress()).trim();
                if(ip.equals("127.0.0.1")) return "N/A";
                return ip;
            } catch(Exception ex) { return "ERROR"; }
        }

        /**
         * Returns the External IP Address by connecting to "http://api.ipify.org"
         *
         * @return External IP address as string
         */
        public static String getExternalIPAddress() {
            try {
                URL url = new URL("http://api.ipify.org");
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                return (in.readLine()).trim();
            } catch(Exception ex) {
                if(ex.toString().contains("java.net.UnknownHostException:")) { return "N/A"; }
                return ex.toString();
            }
        }

        /**
         * Returns status of internet connection
         *
         * @return Internet connection status as boolean
         * */
        public static boolean isConnectedToInternet() { return !getExternalIPAddress().equals("N/A"); }

        // This class should only be called statically
        private Network() { super(); }
    }

    /** Returns information about the system manufacturer */
    public static class OEM {
        /**
         * Returns the system manufacturer name that is stored in the registry
         *
         * @return OEM name as string
         * */
        public static String Name() {
            String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
            String value = "SystemManufacturer";
            String text = RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            if(text.equals("")) {
                key = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\OEMInFormation";
                value = "Manufacturer";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }
            return text;
        }

        /**
         * Returns the system product name that is stored in the registry
         *
         * @return Product name as string
         * */
        public static String ProductName() {
            String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
            String value = "SystemProductName";
            String text = RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            if(text.equals("")) {
                key = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\OEMInFormation";
                value = "Model";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }
            return text;
        }

        // This class should only be called statically
        private OEM() { super(); }
    }

    /** Returns information about the system processor */
    public static class Processor {
        /**
         * Returns the system processor name that is stored in the registry
         *
         * @return Processor name as string
         * */
        @NotNull
        public static String Name() {
            String key = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0";
            String value = "ProcessorNameString";
            return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        }

        /**
         * Returns the number of cores available on the system processor
         *
         * @return Number of cores as int
         * */
        public static int Cores() {
            String command = "";

            if(isMac()) command = "sysctl -n machdep.cpu.core_count";
            else if(isLinux()) command = "lscpu";
            else if(isWindows()) command = "cmd /C WMIC CPU Get /Format:List";

            final Process process;
            int numberOfCores = 0;
            int sockets = 0;
            try {
                if(isMac()) {
                    String[] cmd = { "/bin/sh", "-c", command };
                    process = Runtime.getRuntime().exec(cmd);
                } else process = Runtime.getRuntime().exec(command);

                assert process != null;
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                while((line = reader.readLine()) != null) {
                    if(isMac()) {
                        numberOfCores = line.length() > 0 ? Integer.parseInt(line) : 0;
                    } else if(isLinux()) {
                        if(line.contains("Core(s) per socket:")) {
                            numberOfCores = Integer.parseInt(line.split("\\s+")[line.split("\\s+").length - 1]);
                        }
                        if(line.contains("Socket(s):")) {
                            sockets = Integer.parseInt(line.split("\\s+")[line.split("\\s+").length - 1]);
                        }
                    } else if(isWindows()) {
                        if(line.contains("NumberOfCores")) {
                            numberOfCores = Integer.parseInt(line.split("=")[1]);
                        }
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

            if(isLinux()) return numberOfCores * sockets;
            return numberOfCores;
        }

        // This class should only be called statically
        private Processor() { super(); }
    }

    /** Returns information about the system RAM */
    public static class RAM {
        /**
         * Returns the total ram installed on the system
         *
         * @return Total Ram as string
         * */
        public static String GetTotalRam() {
            long memorySize = ((com.sun.management.OperatingSystemMXBean)
                    java.lang.management.ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
            return ConvertBytes((double) memorySize);
        }

        // This class should only be called statically
        private RAM() { super(); }
    }

    /** Returns information about the system storage */
    public static class Storage {
        /**
         * Returns the file path to the root of the drive Windows is installed on
         *
         * @return System drive file path as string
         * */
        @NotNull
        public static String getSystemDrivePath() {
            char[] pszPath = new char[WinDef.MAX_PATH];
            NativeMethods.Shell32.INSTANCE.SHGetFolderPath
                    (null, ShlObj.CSIDL_WINDOWS, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath);
            return Native.toString(pszPath).replace("WINDOWS", "");
        }

        /**
         * Returns the file path to the Windows directory
         *
         * @return Windows directory file path as string
         * */
        @NotNull
        public static String getWindowsPath() {
            char[] pszPath = new char[WinDef.MAX_PATH];
            NativeMethods.Shell32.INSTANCE.SHGetFolderPath
                    (null, ShlObj.CSIDL_WINDOWS, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath);
            return Native.toString(pszPath);
        }

        /**
         * Returns the drive size of the drive Windows is installed on
         *
         * @return System drive size as string
         * */
        @NotNull
        public static String getSystemDriveSize() {
            return getDriveSize(getSystemDrivePath().replace(":/", "").charAt(0));
        }

        /**
         * Returns the drive size of the specified drive by drive letter, returns "N/A" if drive doesn't exist
         *
         * @param driveLetter Drive letter of drive to get the size of
         * @return Drive size of the specified drive letter
         * */
        @NotNull
        public static String getDriveSize(char driveLetter) {
            File aDrive = new File(driveLetter + ":");
            if(aDrive.exists()) return ConvertBytes((double) aDrive.getTotalSpace());
            return "N/A";
        }

        /**
         * Returns the free space of drive of the drive Windows is installed on
         *
         * @return System drive free space as string
         * */
        @NotNull
        public static String getSystemDriveFreeSpace() {
            return getDriveFreeSpace(getSystemDrivePath().replace(":/", "").charAt(0));
        }

        /**
         * Returns the free space of the specified drive by drive letter, returns "N/A" if drive doesn't exist
         *
         * @param driveLetter Drive letter of drive to get the free space of
         * @return Drive free space of the specified drive letter
         * */
        @NotNull
        public static String getDriveFreeSpace(char driveLetter) {
            File aDrive = new File(driveLetter + ":");
            if(aDrive.exists()) return ConvertBytes((double) aDrive.getUsableSpace());
            return "N/A";
        }

        // This class should only be called statically
        private Storage() { super(); }
    }

    public static class HWObject {
        String SystemOEM;
        String ProductName;
        BIOSObject BIOS;
        NetworkObject Network;
        ProcessorObject Processor;
        RAMObject RAM;
        StorageObject Storage;

        public String SystemOEM() { return SystemOEM; }

        public String ProductName() { return ProductName; }

        public BIOSObject BIOS() { return BIOS; }

        public NetworkObject Network() { return Network; }

        public ProcessorObject Processor() { return Processor; }

        public RAMObject RAM() { return RAM; }

        public StorageObject Storage() { return Storage; }
    }

    public static class BIOSObject {
        String Name;
        String ReleaseDate;
        String Vendor;
        String Version;

        public String Name() { return Name; }

        public String ReleaseDate() { return ReleaseDate; }

        public String Vendor() { return Vendor; }

        public String Version() { return Version; }
    }

    public static class DriveObject {
        //String Name;
        //String Format;
        //String Label;
        String DriveType;
        String TotalSize;
        String TotalFree;

        //public String Name() { return Name; }
        //public String Format() { return Format; }
        //public String Label() { return Label; }
        public String DriveType() { return DriveType; }

        public String TotalSize() { return TotalSize; }

        public String TotalFree() { return TotalFree; }
    }

    public static class NetworkObject {
        String InternalIPAddress;
        String ExternalIPAddress;
        Boolean ConnectionStatus;

        public String InternalIPAddress() { return InternalIPAddress; }

        public String ExternalIPAddress() { return ExternalIPAddress; }

        public Boolean ConnectionStatus() { return ConnectionStatus; }
    }

    public static class ProcessorObject {
        String Name;
        int Cores;

        public String Name() { return Name; }

        public int Cores() { return Cores; }
    }

    public static class RAMObject {
        String TotalInstalled;

        public String TotalInstalled() { return TotalInstalled; }
    }

    public static class StorageObject {
        //List<DriveObject> InstalledDrives;
        DriveObject SystemDrive;

        //public List<DriveObject> InstalledDrives() { return InstalledDrives; }
        public DriveObject SystemDrive() { return SystemDrive; }
    }

    // This class should only be called statically
    private HWInfo() { super(); }
}
