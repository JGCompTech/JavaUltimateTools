package com.jgcomptech.tools.osinfo.windows;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.citumpe.ctpTools.jWMI.getWMIValue;

/**
 * Gets information about the Windows activation status.
 */
public class Activation {
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
            String LicenseStatus = getWMIValue("SELECT * FROM SoftwareLicensingProduct Where PartialProductKey <> null AND ApplicationId='55c92734-d682-4d71-983e-d6ec3f16059f' AND LicenseisAddon=False", "LicenseStatus");

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
