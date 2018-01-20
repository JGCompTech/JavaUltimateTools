package com.jgcomptech.tools.demo;

import com.jgcomptech.tools.authenication.*;
import com.jgcomptech.tools.databasetools.jbdc.Database;
import com.jgcomptech.tools.databasetools.jbdc.DatabaseType;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import com.jgcomptech.tools.events.EventHandler;
import com.jgcomptech.tools.events.PermissionEvent;
import com.jgcomptech.tools.permissions.Permission;
import com.jgcomptech.tools.permissions.PermissionManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;

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
    public void start(final Stage primaryStage) throws Exception {
        //try {
            final MavenXpp3Reader reader = new MavenXpp3Reader();
            final Model model;

            if((new File("pom.xml")).exists())
                model = reader.read(new FileReader("pom.xml"));
            else {
                InputStream file = getClass().getResourceAsStream(
                        "/META-INF/maven/com.jgcomptech.tools/java-ultimate-tools/pom.xml"
                );
                if(file == null) MessageBox.show("File = null", "Error", MessageBoxIcon.ERROR);
                model = reader.read(new InputStreamReader(file));
            }

            print("------------------------------------");
            print("-----Java Ultimate Tools " + model.getVersion() + "------");
            print("-------Created by " + model.getOrganization().getName() + "------");
            print("------------------------------------");
            /*print("Loading Computer Info Please Wait...");
            final ComputerInfo compInfo = new ComputerInfo();
            print("");
            print(compInfo.OS.InstallInfo().NameExpandedFromRegistry());*/
            //print("------------------------------------");
            print("");
            print("Creating New Database userdb.db...");
            try(Database db = new Database("./userdb.db", DatabaseType.H2)) {
                UserManager userManager = new UserManager(db);
                userManager.createUser("admin", "1234", UserRoleManager.SystemUserRoles.ADMIN);
                userManager.createUser("editor", "1234", UserRoleManager.SystemUserRoles.EDITOR);

                print("Admin User Exists: " + userManager.userExists("admin"));

                UserAccount account = userManager.getUser("admin");
                print("Admin User Role: " + account.getUserRole());
                print("Admin Password Matches \"1234\": " + account.checkPasswordMatches("1234"));

                print("Changed Admin Password To \"pass\": "
                        + userManager.setPassword("admin", "pass"));

                print("Initializing Permissions Manager...");
                PermissionManager permissionManager = PermissionManager.getInstance();
                permissionManager.setOnPermissionsApplied(e -> {
                    UserRole userRole = (UserRole) e.getArgs().get(0);
                    System.out.println("EVENT: " + userRole.getName() + " Permissions Applied!");
                });
                permissionManager.setOnAllPermissionsEnabled(e ->
                        System.out.println("EVENT: All permissions enabled!"));
                permissionManager.setOnAllPermissionsDisabled(e ->
                        System.out.println("EVENT: All permissions disabled!"));
                EventHandler<PermissionEvent> permissionEnabled = e -> {
                    Permission permission = e.getPermission();
                    System.out.println("EVENT: Permission " + permission.getName() + " Enabled!");
                };
                EventHandler<PermissionEvent> permissionDisabled = e -> {
                    Permission permission = e.getPermission();
                    System.out.println("EVENT: Permission " + permission.getName() + " Disabled!");
                };
                permissionManager.getAdminPermission().setOnEnabled(permissionEnabled);
                permissionManager.getAdminPermission().setOnDisabled(permissionDisabled);

                permissionManager.getEditPermission().setOnEnabled(permissionEnabled);
                permissionManager.getEditPermission().setOnDisabled(permissionDisabled);

                permissionManager.getCreatePermission().setOnEnabled(permissionEnabled);
                permissionManager.getCreatePermission().setOnDisabled(permissionDisabled);

                permissionManager.getReadPermission().setOnEnabled(permissionEnabled);
                permissionManager.getReadPermission().setOnDisabled(permissionDisabled);

                userManager.getSessionManager().setOnLoginSuccess(e -> {
                    String username = e.getUser().getUsername();
                    System.out.println("EVENT: Access Granted " + username + '!');
                });
                userManager.getSessionManager().setOnLoginFailure(e ->
                        MessageBox.show("EVENT: Invalid Username Or Password!"));
                userManager.getSessionManager().setOnSessionOpened(e -> {
                    String username = e.getSession().getUsername();
                    System.out.println("EVENT: " + username + " Logged In Successfully!");
                });
                userManager.getSessionManager().setOnSessionClosed(e -> {
                    String username = e.getSession().getUsername();
                    System.out.println("EVENT: " + username + " Logged Out Successfully!");
                });

                permissionManager.loadPermissions(false);

                print("Attempting to login user...");
                boolean result = userManager.getSessionManager().showLoginWindow(
                        "Please Login! (username=admin, pass=pass)",
                        "Invalid Login! Please Try Again!", true);
                if(!result) print("Login Canceled!");

                print("Attempting User Logout...");
                if(userManager.getSessionManager().logoutUser()) print("Logout Succeeded!");
                else print("Logout Failed!");

                print("Enabling \"admin\" Permission...");
                permissionManager.setAdminPermission(true);
                print("Adding Custom Permission \"change_settings\"...");
                permissionManager.addCustomPermission("change_settings");
                permissionManager.enablePermission("change_settings");
                print("Adding \"change_settings\" as child to \"admin\" permission...");
                permissionManager.addExistingChildPermission("change_settings", "admin");
                print("Is \"change_settings\" enabled: "
                        + permissionManager.isPermissionEnabled("change_settings"));
                permissionManager.setPermissionOnEnabled("change_settings", e -> {
                    Permission permission = e.getPermission();
                    System.out.println("EVENT: Permission " + permission.getName() + " Enabled!");
                });
                permissionManager.setPermissionOnDisabled("change_settings", e -> {
                    Permission permission = e.getPermission();
                    System.out.println("EVENT: Permission " + permission.getName() + " Disabled!");
                });

                print("Disabling \"admin\" Permission...");
                permissionManager.setAdminPermission(false);
                print("Is change_settings enabled: " + permissionManager.isPermissionEnabled("change_settings"));

                print("Initializing MultiSession Manager...");
                MultiSessionManager multiSessionManager = new MultiSessionManager(userManager);

                multiSessionManager.setOnSessionOpened(e -> {
                    String username = e.getSession().getUsername();
                    System.out.println("EVENT: " + username + " Logged In Successfully!");
                });

                multiSessionManager.setOnSessionClosed(e -> {
                    String username = e.getSession().getUsername();
                    System.out.println("EVENT: " + username + " Logged Out Successfully!");
                });

                print("Logging In \"admin\" and \"editor\" Users...");
                multiSessionManager.loginUser("admin");
                multiSessionManager.loginUser("editor");
                print("Number Of Logged In Users: " + multiSessionManager.getSessionsCount());
                print("Logging Out \"admin\" and \"editor\" Users...");
                multiSessionManager.logoutUser("admin");
                multiSessionManager.logoutUser("editor");
                print("Number Of Logged In Users: " + multiSessionManager.getSessionsCount());
            }
            print("------------------------------------");

            Platform.exit();
        //} catch(XmlPullParserException | IOException | InterruptedException e) {
        /*} catch(Exception e) {
            MessageBox.show(e.getMessage(), "Error", MessageBoxIcon.ERROR);
        }*/
    }
}

