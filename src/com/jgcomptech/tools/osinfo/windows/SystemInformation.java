package com.jgcomptech.tools.osinfo.windows;

import com.jgcomptech.tools.NativeMethods;
import com.sun.jna.platform.win32.WinBase;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Gets information about the current Windows installation
 */
public class SystemInformation {
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
