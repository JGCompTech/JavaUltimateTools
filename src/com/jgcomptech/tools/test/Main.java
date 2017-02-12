package com.jgcomptech.tools.test;

import com.jgcomptech.tools.Misc;

import java.security.KeyPair;

class Main {
    private static void Print(String str) { System.out.println(str); }
    private static void Print(Boolean str) { System.out.println(str); }
    private static void Print(Integer str) { System.out.println(str); }
    private static void Print(KeyPair keyPair) {
        System.out.println("Private Key: " + keyPair.getPrivate());
        System.out.println("Public Key: " + keyPair.getPublic());
    }

    public static void main(String[] args) {
//        Print(Windows.UserInfo.RegisteredOrganization());
//        Print(Windows.UserInfo.RegisteredOwner());
//        Print(Windows.UserInfo.LoggedInUserName());
//        Print(Windows.UserInfo.CurrentDomainName());
//        Print(Windows.UserInfo.CurrentMachineName());
//        Print(Windows.CheckIf.isDomainJoined());
//        Print(OSInfo.Name.DisplayVersion2());
//        Print(HardwareInfo.Storage.getSystemDrivePath());
//        Print(Windows.CheckIf.isCurrentUserAdmin());
//        Print(HardwareInfo.Storage.getDriveSize('G'));
//        Print(HardwareInfo.Storage.getDriveFreeSpace('A'));
//        Print(HardwareInfo.Network.getConnectionStatus());
//        Print(HardwareInfo.OEM.Name());
//        Print(HardwareInfo.OEM.ProductName());
//        Print(HardwareInfo.Processor.Cores());
//        Print(HardwareInfo.Processor.Name());
//        Print(HardwareInfo.RAM.GetTotalRam());
        //Print(Windows.CheckIf.isActivated());
        //String hash = FileHashes.getFileHash(FileHashes.HashTypes.SHA1, "H:\\Programing\\Java\\Current Projects\\OSInfo.zip");
        //Print(hash);
        //Print(RSAHashes.testRSA());
        //FileHashes.saveToFile(hash, "hash");
        //CommandInfo.Output result = CommandInfo.Run("ipconfig", "", false);
        //result.print();
        Print(Misc.ConvertBytes(1024D));

        /*Print(Windows.Version.getVersionInfo(Windows.Version.Type.Main));
        Print(OSInfo.Architecture.String());
        Print(OSInfo.OSCheck.getOperatingSystemType());

        Print(OSInfo.Name.DisplayVersion());*/

        //Print("---------------------------------------------");
        //Print("Loading Windows WMI...");
        //Print(OSInfo.WindowsSystemInformation.get());
        //Print("---------------------------------------------");

        //Print().list(System.out);

        Print("---------------------------------------------");
    }
}