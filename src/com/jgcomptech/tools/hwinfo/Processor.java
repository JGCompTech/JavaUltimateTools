package com.jgcomptech.tools.hwinfo;

import com.jgcomptech.tools.RegistryInfo;
import com.jgcomptech.tools.osinfo.CheckIf;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Processor Information
 */
public class Processor {
    /**
     * Returns the system processor name that is stored in the registry.
     */
    @NotNull
    public static String Name() {
        String key = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0";
        String value = "ProcessorNameString";
        return RegistryInfo.getStringValue(RegistryInfo.HKEY.LOCAL_MACHINE, key, value);
    }

    /**
     * Returns the number of cores available on the system processor.
     */
    public static int Cores() {
        String command = "";
        if(CheckIf.isMac()) {
            command = "sysctl -n machdep.cpu.core_count";
        } else if(CheckIf.isLinux()) {
            command = "lscpu";
        } else if(CheckIf.isWindows()) {
            command = "cmd /C WMIC CPU Get /Format:List";
        }
        final Process process;
        int numberOfCores = 0;
        int sockets = 0;
        try {
            if(CheckIf.isMac()) {
                String[] cmd = { "/bin/sh", "-c", command };
                process = Runtime.getRuntime().exec(cmd);
            } else {
                process = Runtime.getRuntime().exec(command);
            }

            assert process != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if(CheckIf.isMac()) {
                    numberOfCores = line.length() > 0 ? Integer.parseInt(line) : 0;
                } else if (CheckIf.isLinux()) {
                    if (line.contains("Core(s) per socket:")) {
                        numberOfCores = Integer.parseInt(line.split("\\s+")[line.split("\\s+").length - 1]);
                    }
                    if(line.contains("Socket(s):")) {
                        sockets = Integer.parseInt(line.split("\\s+")[line.split("\\s+").length - 1]);
                    }
                } else if (CheckIf.isWindows()) {
                    if (line.contains("NumberOfCores")) {
                        numberOfCores = Integer.parseInt(line.split("=")[1]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(CheckIf.isLinux()) {
            return numberOfCores * sockets;
        }
        return numberOfCores;
    }
}
