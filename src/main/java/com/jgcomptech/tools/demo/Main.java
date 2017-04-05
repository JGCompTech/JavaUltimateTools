package com.jgcomptech.tools.demo;

import com.jgcomptech.tools.ComputerInfo;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;

public class Main extends Application {
    private static void Print(String str) { System.out.println(str); }

    private static void Print(Boolean str) { System.out.println(str); }

    private static void Print(Integer str) { System.out.println(str); }

    private static void Print(KeyPair keyPair) {
        System.out.println("Private Key: " + keyPair.getPrivate());
        System.out.println("Public Key: " + keyPair.getPublic());
    }

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        try {
            final MavenXpp3Reader reader = new MavenXpp3Reader();
            final Model model = reader.read(new FileReader("pom.xml"));


            Print("------------------------------------");
            Print("------Java Ultimate Tools " + model.getVersion() + "------");
            Print("-------Created by " + model.getOrganization().getName() + "------");
            Print("------------------------------------");
            Print("Loading Computer Info Please Wait...");
            final ComputerInfo compInfo = new ComputerInfo();
            Print("");
            Print(compInfo.OS.InstallInfo().NameExpandedFromRegistry());
            Print("------------------------------------");
        } catch(XmlPullParserException | IOException | InterruptedException e) {
            MessageBox.show(e.getMessage(), "Error", MessageBoxIcon.ERROR);
        }
    }
}

