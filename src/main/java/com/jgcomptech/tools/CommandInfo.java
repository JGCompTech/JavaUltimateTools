package com.jgcomptech.tools;

import com.sun.jna.platform.win32.WinDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;

import static com.jgcomptech.tools.OSInfo.CheckIf.isWindows;

/** Allows you to run console commands and either run them elevated or not and return the result to a string */
public class CommandInfo {
    private static final String CRLF = "\r\n";

    /**
     * Runs command and returns results to ArrayList in Output object
     *
     * @param command Command to run
     * @param args    Arguments to pass to command
     * @return Output object
     */
    public static Output Run(String command, String args) {
        return Run(command, args, false, true, false);
    }

    /**
     * Runs command elevated, shows cmd window and pauses window when command is complete <p>
     * If "elevate" parameter is false, it is ignored and and results will be saved to Output object <p>
     * If OS is not Windows, "elevate" parameter is ignored and results will be saved to Output object
     *
     * @param command Command to run
     * @param args    Arguments to pass to command
     * @param elevate Boolean to set if command should be run elevated, if true Output object will be empty
     * @return Output object
     */
    public static Output Run(String command, String args, boolean elevate) {
        if(elevate) {
            return Run(command, args, true, false, true);
        }
        return Run(command, args);
    }

    /**
     * Runs command according to parameters, will only open cmd window if OS is Windows <p>
     * If OS is not Windows, all boolean parameters are ignored and results will be saved to Output object
     *
     * @param command        Command to run
     * @param args           Arguments to pass to command
     * @param elevate        Boolean to set if command should be run elevated, if true Output object will be empty <p>
     * @param hideWindow     If true, cmd window will be hidden, if true, and elevate is false, results will be saved to
     *                       Output object
     * @param keepWindowOpen If true, pauses cmd window and forces it to stay open after command is completed <p>
     *                       If false and "elevate" is true, cmd window will close after command is completed
     *                       <p>
     *                       This parameter is ignored if "hidewindow" is true, this prevents cmd window from staying
     *                       open when hidden and unnecessarily using RAM
     * @return Output object
     */
    @Nullable
    public static Output Run(String command, String args, boolean elevate, boolean hideWindow, boolean keepWindowOpen) {
        Output newOutput = new Output();

        if((elevate || !hideWindow) && isWindows()) {
            ShellExecute(command, args, elevate, hideWindow, keepWindowOpen);
        } else {
            final Process process;
            try {
                if(isWindows()) {
                    String cmdString = String.format("cmd /C \"%s %s\"", command, args);
                    process = Runtime.getRuntime().exec(cmdString);
                } else {
                    process = Runtime.getRuntime().exec(command);
                }

                assert process != null;
                final BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while((line = br.readLine()) != null) {
                    newOutput.Result.add(line);
                }

                process.waitFor();

                newOutput.ExitCode = process.exitValue();
            } catch(IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return newOutput;
    }

    private static void ShellExecute(String command, String args, boolean elevate, boolean hideWindow, boolean keepWindowOpen) {
        FileWriter writer;
        try {
            writer = new FileWriter("my.bat");
            writer.write("@Echo off" + System.getProperty("line.separator"));
            writer.write("\"" + command + "\" " + args + System.getProperty("line.separator"));
            if(keepWindowOpen && !hideWindow) { writer.write("pause"); }
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        int WindowStatus = hideWindow ? 0 : 1;
        String operation = elevate ? "runas" : "open";

        WinDef.HWND hw = null;
        String filename = "my.bat";
        NativeMethods.Shell32.INSTANCE.ShellExecute(hw, operation, filename, null, null, WindowStatus);
        try {
            Thread.sleep(2000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        File file = new File("my.bat");
        file.delete();
    }

    /** Output object that is returned after the command has completed */
    public static class Output {
        /** Returns the text result of the command */
        public final ArrayList<String> Result = new ArrayList<String>() {
            @NotNull
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                for(String line : Result) {
                    if(!line.contains("Windows Script Host Version") &&
                            !line.contains("Microsoft Corporation. All rights reserved.") && !line.equals("")) {
                        sb.append(line).append(CRLF);
                    }
                }
                return sb.toString();
            }
        };

        /** Returns the exit code, returns 0 if no error occurred */
        public int ExitCode = 0;

        public void print() {
            for(String line : this.Result) {
                System.out.println(line);
            }
        }
    }
}
