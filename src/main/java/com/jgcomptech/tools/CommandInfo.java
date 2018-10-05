package com.jgcomptech.tools;

import com.sun.jna.platform.win32.WinDef;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.jgcomptech.tools.OSInfo.CheckIf.isWindows;
import static java.nio.charset.StandardCharsets.UTF_8;

/** Allows you to run console commands and either run them elevated or not and return the result to a string. */
public final class CommandInfo {

    /**
     * Runs command and returns results to ArrayList in Output object.
     *
     * @param command Command to run
     * @param args    Arguments to pass to command
     * @return Output object
     * @throws IOException if error occurs
     * @throws InterruptedException if command is interrupted
     */
    public static Output Run(final String command, final String args)
            throws IOException, InterruptedException {
        return Run(command, args, false, true, false);
    }

    /**
     * Runs command elevated, shows cmd window and pauses window when command is complete. <p>
     * If "elevate" parameter is false, it is ignored and and results will be saved to Output object <p>
     * If OS is not Windows, "elevate" parameter is ignored and results will be saved to Output object
     *
     * @param command Command to run
     * @param args    Arguments to pass to command
     * @param elevate Boolean to set if command should be run elevated, if true Output object will be empty
     * @return Output object
     * @throws IOException if error occurs
     * @throws InterruptedException if command is interrupted
     */
    public static Output Run(final String command, final String args, final boolean elevate)
            throws IOException, InterruptedException {
        if(elevate) return Run(command, args, true, false, true);
        return Run(command, args);
    }

    /**
     * Runs command according to parameters, will only open cmd window if OS is Windows. <p>
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
     *                       This parameter is ignored if "hideWindow" is true, this prevents cmd window from staying
     *                       open when hidden and unnecessarily using RAM
     * @return Output object
     * @throws IOException if error occurs
     * @throws InterruptedException if command is interrupted
     */
    public static Output Run(final String command, final String args, final boolean elevate,
                             final boolean hideWindow, final boolean keepWindowOpen)
            throws IOException, InterruptedException {
        final var newOutput = new Output();

        if((elevate || !hideWindow) && isWindows()) {
            ShellExecute(command, args, elevate, hideWindow, keepWindowOpen);
        } else {
            final Process process;
            if(isWindows()) {
                final var cmdString = String.format("cmd /C \"%s %s\"", command, args);
                process = Runtime.getRuntime().exec(cmdString);
            } else {
                process = Runtime.getRuntime().exec(command);
            }

            assert process != null;
            try(final var br = new BufferedReader(new InputStreamReader(process.getInputStream(), UTF_8))) {
                String line;
                while((line = br.readLine()) != null) {
                    newOutput.Result.add(line);
                }
            }

            process.waitFor();

            newOutput.ExitCode = process.exitValue();
        }
        return newOutput;
    }

    private static void ShellExecute(final String command, final String args, final boolean elevate,
                                     final boolean hideWindow, final boolean keepWindowOpen)
            throws IOException, InterruptedException {
        final var filename = "my.bat";

        try(final Writer writer = Files.newBufferedWriter(Paths.get(filename), UTF_8)) {
            writer.write("@Echo off" + System.lineSeparator());
            writer.write('"' + command + "\" " + args + System.lineSeparator());
            if(keepWindowOpen && !hideWindow) { writer.write("pause"); }
        }

        final var windowStatus = hideWindow ? 0 : 1;
        final var operation = elevate ? "runas" : "open";

        final WinDef.HWND hw = null;
        NativeMethods.Shell32.INSTANCE.ShellExecute(hw, operation, filename, null, null, windowStatus);

        Thread.sleep(2000);

        Files.delete(Paths.get(filename));
    }

    /** Output object that is returned after the command has completed. */
    public static class Output {
        /** Returns the text result of the command. */
        public final ArrayList<String> Result = new ArrayList<>() {
            @Override
            public String toString() {
                return Result.stream()
                        .filter(line -> !line.contains("Windows Script Host Version")
                                && !line.contains("Microsoft Corporation. All rights reserved.")
                                && !line.trim().isEmpty())
                        .map(line -> line + System.lineSeparator()).collect(Collectors.joining());
            }
        };

        /** Returns the exit code, returns 0 if no error occurred. */
        public int ExitCode;

        public void print() { Result.forEach(System.out::println); }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;

            if (!(o instanceof Output)) return false;

            final var output = (Output) o;

            return new EqualsBuilder()
                    .append(ExitCode, output.ExitCode)
                    .append(Result, output.Result)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(Result)
                    .append(ExitCode)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("Result", Result)
                    .append("ExitCode", ExitCode)
                    .toString();
        }
    }

    /** Prevents instantiation of this utility class. */
    private CommandInfo() { }
}
