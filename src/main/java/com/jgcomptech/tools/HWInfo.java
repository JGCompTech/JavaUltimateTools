package com.jgcomptech.tools;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.ShlObj;
import com.sun.jna.platform.win32.WinDef;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import static com.jgcomptech.tools.Misc.ConvertBytes;
import static com.jgcomptech.tools.OSInfo.CheckIf.*;

/** Returns information about the system hardware */
public final class HWInfo {
    /** Returns information about the system BIOS */
    public final static class BIOS {
        /**
         * Returns the system BIOS release date stored in the registry
         *
         * @return BIOS date as string
         */
        public static String getReleaseDate() {
            if(isWindows()) {
                final String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
                final String value = "BIOSReleaseDate";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }
            return "Unknown";
        }

        /**
         * Returns the system BIOS version stored in the registry
         *
         * @return BIOS version as string
         */
        public static String getVersion() {
            if(isWindows()) {
                final String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
                final String value = "BIOSVersion";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }
            return "Unknown";
        }

        /**
         * Returns the system BIOS vendor name stored in the registry
         *
         * @return BIOS vendor name as string
         */
        public static String getVendor() {
            if(isWindows()) {
                final String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
                final String value = "BIOSVendor";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }
            return "Unknown";
        }

        // This class should only be called statically
        private BIOS() { super(); }
    }

    /** Returns information about the current network */
    public final static class Network {
        /**
         * Returns the Internal IP Address
         *
         * @return Internal IP Address as string
         */
        public static String getInternalIPAddress() {
            try {
                final String ip = (InetAddress.getLocalHost().getHostAddress()).trim();
                return ip.equals("127.0.0.1") ? "N/A" : ip;
            } catch(UnknownHostException ex) { return ex.getMessage(); }
        }

        /**
         * Returns the External IP Address by connecting to "http://api.ipify.org"
         *
         * @return External IP address as string
         */
        public static String getExternalIPAddress() {
            final URL url;
            try { url = new URL("http://api.ipify.org"); }
            catch(MalformedURLException e) { return e.getMessage(); }
            try(BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                return (in.readLine()).trim();
            } catch(IOException ex) {
                return ex.toString().contains("java.net.UnknownHostException:") ? "N/A" : ex.getMessage();
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
    public final static class OEM {
        /**
         * Returns the system manufacturer name that is stored in the registry
         *
         * @return OEM name as string
         * */
        public static String Name() {
            String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
            String value = "SystemManufacturer";
            final String text = RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            if(text.isEmpty()) {
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
            final String text = RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            if(text.isEmpty()) {
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
    public final static class Processor {
        /**
         * Returns the system processor name that is stored in the registry
         *
         * @return Processor name as string
         * */
        public static String Name() {
            final String key = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0";
            final String value = "ProcessorNameString";
            return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        }

        /**
         * Returns the number of cores available on the system processor
         *
         * @return Number of cores as int
         * @throws IOException if error occurs
         * */
        public static int Cores() throws IOException {
            String command = "";

            if(isMac()) command = "sysctl -n machdep.cpu.core_count";
            else if(isLinux()) command = "lscpu";
            else if(isWindows()) command = "cmd /C WMIC CPU Get /Format:List";

            final Process process;
            int numberOfCores = 0;
            int sockets = 0;
            if(isMac()) {
                final String[] cmd = { "/bin/sh", "-c", command };
                process = Runtime.getRuntime().exec(cmd);
            } else process = Runtime.getRuntime().exec(command);

            assert process != null;
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;

                while((line = reader.readLine()) != null) {
                    if(isMac()) {
                        numberOfCores = line.isEmpty() ? 0 : Integer.parseInt(line);
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
            }

            if(isLinux()) return numberOfCores * sockets;
            return numberOfCores;
        }

        // This class should only be called statically
        private Processor() { super(); }
    }

    /** Returns information about the system RAM */
    public final static class RAM {
        /**
         * Returns the total ram installed on the system
         *
         * @return Total Ram as string
         * */
        public static String getTotalRam() {
            final long memorySize = ((com.sun.management.OperatingSystemMXBean)
                    java.lang.management.ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
            return ConvertBytes((double) memorySize);
        }

        // This class should only be called statically
        private RAM() { super(); }
    }

    /** Returns information about the system storage */
    public final static class Storage {
        /**
         * Returns the file path to the root of the drive Windows is installed on
         *
         * @return System drive file path as string
         * */
        public static String getSystemDrivePath() {
            final char[] pszPath = new char[WinDef.MAX_PATH];
            NativeMethods.Shell32.INSTANCE.SHGetFolderPath
                    (null, ShlObj.CSIDL_WINDOWS, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath);
            return Native.toString(pszPath).replace("WINDOWS", "");
        }

        /**
         * Returns the file path to the Windows directory
         *
         * @return Windows directory file path as string
         * */
        public static String getWindowsPath() {
            final char[] pszPath = new char[WinDef.MAX_PATH];
            NativeMethods.Shell32.INSTANCE.SHGetFolderPath
                    (null, ShlObj.CSIDL_WINDOWS, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath);
            return Native.toString(pszPath);
        }

        /**
         * Returns the drive size of the drive Windows is installed on
         *
         * @return System drive size as string
         * */
        public static String getSystemDriveSize() {
            return getDriveSize(getSystemDrivePath().replace(":/", "").charAt(0));
        }

        /**
         * Returns the drive size of the specified drive by drive letter, returns "N/A" if drive doesn't exist
         *
         * @param driveLetter Drive letter of drive to get the size of
         * @return Drive size of the specified drive letter
         * */
        public static String getDriveSize(char driveLetter) {
            final File aDrive = new File(driveLetter + ":");
            return aDrive.exists() ? ConvertBytes((double) aDrive.getTotalSpace()) : "N/A";
        }

        /**
         * Returns the free space of drive of the drive Windows is installed on
         *
         * @return System drive free space as string
         * */
        public static String getSystemDriveFreeSpace() {
            return getDriveFreeSpace(getSystemDrivePath().replace(":/", "").charAt(0));
        }

        /**
         * Returns the free space of the specified drive by drive letter, returns "N/A" if drive doesn't exist
         *
         * @param driveLetter Drive letter of drive to get the free space of
         * @return Drive free space of the specified drive letter
         * */
        public static String getDriveFreeSpace(char driveLetter) {
            final File aDrive = new File(driveLetter + ":");
            return aDrive.exists() ? ConvertBytes((double) aDrive.getUsableSpace()) : "N/A";
        }

        // This class should only be called statically
        private Storage() { super(); }
    }

    /**
     * A Hardware Object for use with the {@link ComputerInfo} class
     */
    public final static class HWObject {
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

    /**
     * A BIOS Object for use with the {@link ComputerInfo} class
     */
    public final static class BIOSObject {
        String Name;
        String ReleaseDate;
        String Vendor;
        String Version;

        public String Name() { return Name; }

        public String ReleaseDate() { return ReleaseDate; }

        public String Vendor() { return Vendor; }

        public String Version() { return Version; }
    }

    /**
     * A Drive Object for use with the {@link ComputerInfo} class
     */
    public final static class DriveObject {
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

    /**
     * A Network Object for use with the {@link ComputerInfo} class
     */
    public final static class NetworkObject {
        String InternalIPAddress;
        String ExternalIPAddress;
        Boolean ConnectionStatus;

        public String InternalIPAddress() { return InternalIPAddress; }

        public String ExternalIPAddress() { return ExternalIPAddress; }

        public Boolean ConnectionStatus() { return ConnectionStatus; }
    }

    /**
     * A Processor Object for use with the {@link ComputerInfo} class
     */
    public final static class ProcessorObject {
        String Name;
        int Cores;

        public String Name() { return Name; }

        public int Cores() { return Cores; }
    }

    /**
     * A RAM Object for use with the {@link ComputerInfo} class
     */
    public final static class RAMObject {
        String TotalInstalled;

        public String TotalInstalled() { return TotalInstalled; }
    }

    /**
     * A Storage Object for use with the {@link ComputerInfo} class
     */
    public final static class StorageObject {
        //List<DriveObject> InstalledDrives;
        DriveObject SystemDrive;

        //public List<DriveObject> InstalledDrives() { return InstalledDrives; }
        public DriveObject SystemDrive() { return SystemDrive; }
    }

    // This class should only be called statically
    private HWInfo() { super(); }
}
