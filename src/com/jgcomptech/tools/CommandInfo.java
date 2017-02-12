package com.jgcomptech.tools;

import com.jgcomptech.tools.osinfo.CheckIf;
import com.sun.jna.platform.win32.WinDef;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;

public class CommandInfo {
    /**
     * Runs command and returns results to ArrayList in Output object.
     *
     * @param command Command to run.
     * @param args    Arguments to pass to command.
     * @return Output object
     */
    public static Output Run(String command, String args) {
        return Run(command, args, false, true, false);
    }

    /**
     * Runs command elevated, shows cmd window and pauses window when command is complete.
     * If "elevate" parameter is false, it is ignored and and results will be saved to Output object.
     * If OS is not Windows, "elevate" parameter is ignored and results will be saved to Output object.
     *
     * @param command Command to run.
     * @param args    Arguments to pass to command.
     * @param elevate Boolean to set if command should be run elevated. If true Output object will be empty.
     * @return Output object
     */
    public static Output Run(String command, String args, boolean elevate) {
        if(elevate) {
            return Run(command, args, true, false, true);
        }
        return Run(command, args);
    }

    /**
     * Runs command according to parameters. Will only open cmd window if OS is Windows.
     * If OS is not Windows, all boolean parameters are ignored and results will be saved to Output object.
     *
     * @param command        Command to run.
     * @param args           Arguments to pass to command.
     * @param elevate        Boolean to set if command should be run elevated. If true Output object will be empty.
     * @param hideWindow     If true, cmd window will be hidden. If true, and elevate is false, results will be saved to
     *                       Output object.
     * @param keepWindowOpen If true, pauses cmd window and forces it to stay open after command is completed. If false
     *                       and "elevate" is true, cmd window will close after command is completed.
     *                       <p>
     *                       This parameter is ignored if "hidewindow" is true. This prevents cmd window from staying
     *                       open when hidden and unnecessarily using RAM.
     * @return Output object
     */
    @Nullable
    public static Output Run(String command, String args, boolean elevate, boolean hideWindow, boolean keepWindowOpen) {
        Output newOutput = new Output();

        if((elevate || !hideWindow) && CheckIf.isWindows()) {
            ShellExecute(command, args, elevate, hideWindow, keepWindowOpen);
        } else {
            final Process process;
            try {
                if(CheckIf.isWindows()) {
                    process = Runtime.getRuntime().exec("cmd /C \"" + command + " " + args + "\"");
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
        /** Returns the text result of the command. */
        public final ArrayList<String> Result = new ArrayList<>();

        /** Returns the exit code. Returns 0 if no error occurred. */
        public int ExitCode = 0;

        public void print() {
            for(String line : this.Result) {
                System.out.println(line);
            }
        }
    }
}
