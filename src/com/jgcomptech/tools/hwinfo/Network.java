package com.jgcomptech.tools.hwinfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

/**
 * Network Information
 */
public class Network {
    /**
     * Returns the Internal IP Address.
     */
    public static String getInternalIPAddress() {
        try {
            String ip = (InetAddress.getLocalHost().getHostAddress()).trim();
            if(ip.equals("127.0.0.1")) return "N/A";
            return ip;
        } catch(Exception ex) { return "ERROR"; }
    }

    /**
     * Returns the External IP Address by connecting to "http://api.ipify.org".
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
     */
    public static boolean isConnectedToInternet() { return !getExternalIPAddress().equals("N/A"); }
}
