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
        /** Returns the system BIOS release date stored in the registry. */
        @NotNull
        public static String getReleaseDate() {
            if(isWindows()) {
                String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
                String value = "BIOSReleaseDate";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }
            return "Unknown";
        }

        /** Returns the system BIOS version stored in the registry. */
        @NotNull
        public static String getVersion() {
            if(isWindows()) {
                String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
                String value = "BIOSVersion";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }
            return "Unknown";
        }

        /** Returns the system BIOS vendor name stored in the registry. */
        @NotNull
        public static String getVendor() {
            if(isWindows()) {
                String key = "HARDWARE\\DESCRIPTION\\System\\BIOS";
                String value = "BIOSVendor";
                return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
            }
            return "Unknown";
        }
    }

    /** Returns information about the current network */
    public static class Network {
        /** Returns the Internal IP Address */
        public static String getInternalIPAddress() {
            try {
                String ip = (InetAddress.getLocalHost().getHostAddress()).trim();
                if(ip.equals("127.0.0.1")) return "N/A";
                return ip;
            } catch(Exception ex) { return "ERROR"; }
        }

        /** Returns the External IP Address by connecting to "http://api.ipify.org" */
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

        /** Returns status of internet connection */
        public static boolean isConnectedToInternet() { return !getExternalIPAddress().equals("N/A"); }
    }

    /** Returns information about the system manufacturer */
    public static class OEM {
        /** Returns the system manufacturer name that is stored in the registry */
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

        /** Returns the system product name that is stored in the registry */
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
    }

    /** Returns information about the system processor */
    public static class Processor {
        /** Returns the system processor name that is stored in the registry */
        @NotNull
        public static String Name() {
            String key = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0";
            String value = "ProcessorNameString";
            return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
        }

        /** Returns the number of cores available on the system processor */
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
    }

    /** Returns information about the system RAM */
    public static class RAM {
        /** Returns the total ram installed on the system */
        public static String GetTotalRam() {
            long memorySize = ((com.sun.management.OperatingSystemMXBean)
                    java.lang.management.ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
            return ConvertBytes((double) memorySize);
        }
    }

    /** Returns information about the system storage */
    public static class Storage {
        /** Returns the file path to the root of the drive Windows is installed on */
        @NotNull
        public static String getSystemDrivePath() {
            char[] pszPath = new char[WinDef.MAX_PATH];
            NativeMethods.Shell32.INSTANCE.SHGetFolderPath
                    (null, ShlObj.CSIDL_WINDOWS, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath);
            return Native.toString(pszPath).replace("WINDOWS", "");
        }

        /** Returns the file path to the Windows directory */
        @NotNull
        public static String getWindowsPath() {
            char[] pszPath = new char[WinDef.MAX_PATH];
            NativeMethods.Shell32.INSTANCE.SHGetFolderPath
                    (null, ShlObj.CSIDL_WINDOWS, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath);
            return Native.toString(pszPath);
        }

        /** Returns the drive size of the drive Windows is installed on */
        @NotNull
        public static String getSystemDriveSize() {
            return getDriveSize(getSystemDrivePath().replace(":/", "").charAt(0));
        }

        /** Returns the drive size of the specified drive by drive letter. Returns "N/A" if drive doesn't exist */
        @NotNull
        public static String getDriveSize(char driveLetter) {
            File aDrive = new File(driveLetter + ":");
            if(aDrive.exists()) return ConvertBytes((double) aDrive.getTotalSpace());
            return "N/A";
        }

        /** Returns the free space of drive of the drive Windows is installed on */
        @NotNull
        public static String getSystemDriveFreeSpace() {
            return getDriveFreeSpace(getSystemDrivePath().replace(":/", "").charAt(0));
        }

        /** Returns the free space of the specified drive by drive letter. Returns "N/A" if drive doesn't exist */
        @NotNull
        public static String getDriveFreeSpace(char driveLetter) {
            File aDrive = new File(driveLetter + ":");
            if(aDrive.exists()) return ConvertBytes((double) aDrive.getUsableSpace());
            return "N/A";
        }
    }
}
