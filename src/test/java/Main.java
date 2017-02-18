import com.jgcomptech.tools.OSInfo;

import java.security.KeyPair;

public class Main {
    private static void Print(String str) { System.out.println(str); }

    private static void Print(Boolean str) { System.out.println(str); }

    private static void Print(Integer str) { System.out.println(str); }

    private static void Print(KeyPair keyPair) {
        System.out.println("Private Key: " + keyPair.getPrivate());
        System.out.println("Public Key: " + keyPair.getPublic());
    }

    public static void main(String[] args) {
        Print(OSInfo.Name.StringExpandedFromRegistry());
        //Print(CommandInfo.Run("ipconfig", "/all").Result.toString());
    }
}
