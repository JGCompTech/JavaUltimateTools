package com.jgcomptech.tools.demo;

import com.jgcomptech.tools.ComputerInfo;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;

import java.io.IOException;
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
        try {
            Print("------------------------------------");
            Print("------Java Ultimate Tools v1.3------");
            Print("-------Created by J&G CompTech------");
            Print("------------------------------------");
            Print("Loading Computer Info Please Wait...");
            final ComputerInfo compInfo = new ComputerInfo();
            Print("");
            Print(compInfo.OS.InstallInfo().NameExpandedFromRegistry());
            Print("------------------------------------");
        } catch(IOException | InterruptedException e) {
            MessageBox.show(e.getMessage(), "Error", MessageBoxIcon.ERROR);
        }
    }
}

