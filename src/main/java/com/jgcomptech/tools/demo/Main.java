package com.jgcomptech.tools.demo;

import com.jgcomptech.tools.ComputerInfo;

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
        Print("------------------------------------");
        Print("------Java Ultimate Tools v1.2------");
        Print("-------Created by J&G CompTech------");
        Print("------------------------------------");
        Print("Loading Computer Info Please Wait...");
        ComputerInfo compinfo = new ComputerInfo();
        Print("");
        Print(compinfo.OS.InstallInfo().NameExpandedFromRegistry());
        Print("------------------------------------");
    }
}

