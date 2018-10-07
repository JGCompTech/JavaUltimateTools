package com.jgcomptech.tools.demo;

import com.jgcomptech.tools.ComputerInfo;
import com.jgcomptech.tools.authc.AuthManager;
import com.jgcomptech.tools.authc.UserManager;
import com.jgcomptech.tools.authc.UserRoleManager;
import com.jgcomptech.tools.authc.UsernamePasswordToken;
import com.jgcomptech.tools.authz.PermissionManager;
import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.DatabaseType;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main extends Application {
    private static void print(final String str) { System.out.println(str); }

    private static void print(final Boolean str) { System.out.println(str); }

    private static void print(final Integer num) { System.out.println(num); }

    private static void print(final Long num) { System.out.println(num); }

    private static void print(final KeyPair keyPair) {
        System.out.println("Private Key: " + keyPair.getPrivate());
        System.out.println("Public Key: " + keyPair.getPublic());
    }

    public static void main(final String[] args) { launch(args); }

    @Override
    public void start(final Stage primaryStage) throws IOException, XmlPullParserException, SQLException, InterruptedException {
        final var reader = new MavenXpp3Reader();
        final Model model;

        if((new File("pom.xml")).exists())
            model = reader.read(Files.newBufferedReader(Paths.get("pom.xml"), UTF_8));
        else {
            final var file = getClass().getResourceAsStream(
                    "/META-INF/maven/com.jgcomptech.tools/java-ultimate-tools/pom.xml"
            );
            if(file == null) MessageBox.show("File = null", "Error", MessageBoxIcon.ERROR);
            model = reader.read(new InputStreamReader(file, UTF_8));
        }

        print("------------------------------------");
        print("-----Java Ultimate Tools " + model.getVersion() + "------");
        print("-------Created by " + model.getOrganization().getName() + "------");
        print("------------------------------------");
        print("Loading Computer Info Please Wait...");
        final var compInfo = new ComputerInfo();
        print("");
        print(compInfo.OS.InstallInfo().NameExpandedFromRegistry());
        print("------------------------------------");
        print("");
        print("Creating New Database userdb.db...");
        print("------------------------------------");

        permissionsManager();

        basicAuth();

        subjectAuth();

        Platform.exit();
    }

    private void permissionsManager() {
        print("Initializing Permissions Manager...");
        final var permissionManager = PermissionManager.getInstance();

        permissionManager.enableDebugLogging();

        permissionManager.loadPermissions(false);

        print("Enabling \"admin\" Permission...");
        permissionManager.setAdminPermission(true);

        print("Adding Custom Permission \"change_settings\"...");
        final var custom = "admin:change_settings";
        permissionManager.addAndEnableCustomPermission("change_settings", "admin");
        permissionManager.getPermission("admin:change_settings").enableDebugLogging();
        permissionManager.addAndEnableCustomPermission("color", "admin:change_settings");
        permissionManager.getPermission("admin:change_settings:color").enableDebugLogging();
        permissionManager.getPermission("admin:change_settings").copyToNewParent("edit");
        permissionManager.getPermission("edit:change_settings").enableDebugLogging();
        permissionManager.getPermission("edit:change_settings:color").enableDebugLogging();

        print("Is \"admin:change_settings\" enabled: " + permissionManager.isPermissionEnabled(custom));

        permissionManager.setPermissionOnEnabled(custom, e -> {
            System.out.println("EVENT: Permission " + e.getPermission().getName() + " Enabled!");
        });
        permissionManager.setPermissionOnDisabled(custom, e -> {
            System.out.println("EVENT: Permission " + e.getPermission().getName() + " Disabled!");
        });

        print("Disabling \"admin\" Permission...");
        permissionManager.setAdminPermission(false);
        print("Is \"admin:change_settings\" enabled: " + permissionManager.isPermissionEnabled(custom));
        print("------------------------------------");
    }

    private void subjectAuth() throws SQLException {
        print("");
        print("Running Subject Auth Demo...");
        print("------------------------------------");

        try(final var db = new Database("./userdb.db", DatabaseType.H2)) {
            final var manager =
                    AuthManager.getNewInstance(db, null, "Java Ultimate Tools");
            final var subject = manager.getSubject();

            manager.createUser("admin", "1234", UserRoleManager.SystemUserRoles.ADMIN);
            manager.createUser("editor", "1234", UserRoleManager.SystemUserRoles.EDITOR);

            manager.enableDebugLogging();

            final var creationDate = manager.getUserCreationDate("admin", "yyyy-MM-dd hh:mm:ss a");

            print("Admin User Exists: " + manager.userExists("admin"));

            print("Admin User Creation Date: " + creationDate);

            print("Admin User Role: " + manager.getUserRole("admin"));

            print("Admin User Locked Status: " + manager.isUserLocked("admin"));

            //manager.setPasswordExpirationDate("admin", LocalDateTime.now());
            manager.disablePasswordExpiration("admin");
            //manager.lockUser("admin");
            manager.unlockUser("admin");

            print("Admin Password Expired: " + manager.isPasswordExpired("admin"));

            print("Admin Password Set To Expire: " + manager.isPasswordSetToExpire("admin"));

            print("Admin Password Expiration Date: "
                    + manager.getPasswordExpirationDate("admin", "yyyy-MM-dd hh:mm:ss a"));

            print("Admin Password Matches \"1234\": " +
                    manager.checkPasswordMatches("admin", "1234"));

            print("Attempting to login user...");
            var token = new UsernamePasswordToken("admin", "1234".toCharArray());
            final var result = subject.login(token);
            if(!result) print("Login Failed!");
            else {
                print("Changed Admin Password To \"pass\": " + subject.setPassword("pass"));

                print("Attempting to logout user...");
                if(subject.logout()) print("Logout Succeeded!");
                else print("Logout Failed!");
            }

            print(subject.getLastSessionDurationString());

            print("Attempting to login user...");
            token.setUsername("admin");
            token.setPassword("pass".toCharArray());
            print(token.toString());
            final var result2 = subject.login(token);
            if(!result2) print("Login Failed!");

//            //for (int i = 0; i < 60; i++) {
//                Thread.sleep(1000);
//
//                print(subject.getSession().getDurationString());
//            //}

            print("Admin has admin permission: " +
                    subject.hasPermission("admin"));

            print("Admin has admin:change_settings permission: " +
                    subject.hasPermission("admin:change_settings"));

            print(subject.hasPermissions(new HashSet<>(Arrays.asList("admin", "admin:change_settings"))));

            print(String.valueOf(subject.getUserRole().getPermissions()));

            print("Changed Admin Password To \"1234\": " + subject.setPassword("1234"));

            print("Attempting to logout user...");
            if(subject.logout()) print("Logout Succeeded!");
            else print("Logout Failed!");

            print(subject.getLastSessionDurationString());

            token.clear();
            token = null;
        }
        print("------------------------------------");
    }

    private void basicAuth() throws SQLException {
        print("");
        print("Running Basic Auth Demo with Login Dialog...");
        print("------------------------------------");

        try(final var db = new Database("./userdb.db", DatabaseType.H2)) {
            final var userManager = new UserManager(db, null, "Java Ultimate Tools");
            userManager.createUser("admin", "1234", UserRoleManager.SystemUserRoles.ADMIN);
            userManager.createUser("editor", "1234", UserRoleManager.SystemUserRoles.EDITOR);

            print("Admin User Exists: " + userManager.userExists("admin"));

            final var account = userManager.getUser("admin");
            print("Admin User Role: " + userManager.getUserRole("admin"));
            print("Admin Password Matches \"1234\": "
                    + userManager.checkPasswordMatches("admin", "1234"));

            userManager.getSessionManager().enableDebugLogging();
            userManager.getSessionManager().enableDefaultErrorMessages();

            print("Attempting to login user...");
            final var result = userManager.getSessionManager().showLoginWindow(true);
            if(!result) print("Login Canceled!");

            print("Attempting to logout user...");
            if(userManager.getSessionManager().logoutUser()) print("Logout Succeeded!");
            else print("Logout Failed!");

            print("Logging In \"admin\" and \"editor\" Users As MultiSession...");
            userManager.getSessionManager().loginUser("admin", true);
            userManager.getSessionManager().loginUser("editor", true);
            print("Number Of Logged In Users: " + userManager.getSessionManager().getSessionsCount());
            print("Logging Out \"admin\" and \"editor\" Users As MultiSession...");
            userManager.getSessionManager().logoutUser("admin", true);
            userManager.getSessionManager().logoutUser("editor", true);
            print("Number Of Logged In Users: " + userManager.getSessionManager().getSessionsCount());
        }
        print("------------------------------------");
    }
}

