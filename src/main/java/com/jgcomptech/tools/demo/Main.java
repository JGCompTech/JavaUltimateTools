package com.jgcomptech.tools.demo;

import com.jgcomptech.tools.authenication.*;
import com.jgcomptech.tools.databasetools.jbdc.Database;
import com.jgcomptech.tools.databasetools.jbdc.DatabaseType;
import com.jgcomptech.tools.databasetools.jbdc.QueryBuilder;
import com.jgcomptech.tools.databasetools.jbdc.TypedStatement;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import com.jgcomptech.tools.events.ActionEvent;
import com.jgcomptech.tools.events.EventHandler;
import com.jgcomptech.tools.events.EventManager;
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
import java.sql.ResultSet;

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

            try(Database db = new Database("./userdb.db", DatabaseType.H2)) {
                UserManager userManager = new UserManager(db);
                userManager.createUser("jlgager", "1234", UserRoleManager.SystemUserRoles.EDITOR);
                userManager.createUser("admin", "1234", UserRoleManager.SystemUserRoles.EDITOR);

                TypedStatement statement = TypedStatement.newQuery()
                                                    .SELECT("Username", "Password", "Type")
                                                    .FROM("Users")
                                                    .WHERE("Username", "admin")
                                                    .WHERE_IN("Type", "ADMIN", "EDITOR")
                                                    .ORDER_BY("Username", true)
                                                    .build(db);

                print(statement.toString());

                try(ResultSet resultSet = statement.executeQuery()) {
                    if(resultSet.next()) {
                        print(resultSet.getString(
                                "Username") + " = " + resultSet.getString("Type"));
                    }
                }

                try(ResultSet resultSet = new QueryBuilder()
                        .SELECT_COUNT_ALL_FROM(
                                new QueryBuilder()
                                        .SELECT("Type")
                                        .FROM("Users")
                                        .WHERE("Username", "admin")
                        )
                        .buildAndExecute(db)) {
                    if(resultSet.next()) {
                        print(resultSet.getInt(1));
                    }
                }

                print(userManager.userExists("admin"));
                print(userManager.getUserRole("admin").toString());
                print(userManager.checkPasswordMatches("admin", "1234"));

                UserAccount account = userManager.getUser("admin");
                print(account.getUsername() + " = " + account.getUserRole());
                print(account.setPassword("5678"));
                print(account.checkPasswordMatches("5678"));

                print("changed " + userManager.setPassword("admin", "pass"));

                db.getSettings().createTable();
                db.getSettings().setValue("Global", "1234");
                print(db.getSettings().getValue("Global"));

                print(userManager.userExists("admin"));

                print(userManager.getUsernameList().contains("admin"));

                userManager.setUserRole("admin", UserRoleManager.SystemUserRoles.ADMIN);

                print(userManager.getUser("admin").getUserRole().toString());

                PermissionManager permissionManager = PermissionManager.getInstance();
                permissionManager.setOnPermissionsApplied(e -> {
                    UserRole userRole = (UserRole) e.getArgs().get(0);
                    System.out.println(userRole.getName() + " Permissions Applied!");
                });
                permissionManager.setOnAllPermissionsEnabled(e -> System.out.println("All permissions enabled!"));
                permissionManager.setOnAllPermissionsDisabled(e -> System.out.println("All permissions disabled!"));
                EventHandler<PermissionEvent> permissionEnabled = e -> {
                    Permission permission = e.getPermission();
                    System.out.println("Permission " + permission.getName() + " Enabled!");
                };
                EventHandler<PermissionEvent> permissionDisabled = e -> {
                    Permission permission = e.getPermission();
                    System.out.println("Permission " + permission.getName() + " Disabled!");
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
                    System.out.println("Access Granted " + username + '!');
                });
                userManager.getSessionManager().setOnLoginFailure(e ->
                        MessageBox.show("Invalid Username Or Password!"));
                userManager.getSessionManager().setOnSessionOpened(e -> {
                    String username = e.getSession().getUsername();
                    System.out.println(username + " Logged In Successfully!");
                });
                userManager.getSessionManager().setOnSessionClosed(e -> {
                    String username = e.getSession().getUsername();
                    System.out.println(username + " Logged Out Successfully!");
                });

                permissionManager.loadPermissions(false);

                boolean result = userManager.getSessionManager().showLoginWindow(
                        "Please Login! (username=admin, pass=pass)",
                        "Invalid Login! Please Try Again!", true);
                if(!result) print("Login Canceled!");
                if(userManager.getSessionManager().logoutUser()) print("Logout Succeeded!");
                else print("Logout Failed!");

                EventManager eventManager = EventManager.getInstance();
                ActionEvent newEvent = eventManager.registerNewEvent("actionEvent",
                        ActionEvent.class, ActionEvent.ACTION);
                newEvent.getArgs().add("Hello World!");
                newEvent.getTarget().addEventHandler(ActionEvent.ANY, e ->
                {
                    System.out.println(e.getArgs().get(0));
                    System.out.println(e.getEventType());
                    System.out.println(e.getSource());
                    System.out.println(e.getTarget());
                });

                newEvent.fireEvent(this);

                permissionManager.setAdminPermission(true);
                permissionManager.addCustomPermission("change_settings");
                permissionManager.enablePermission("change_settings");
                permissionManager.addExistingChildPermission("change_settings", "admin");
                print("Is change_settings enabled: " + permissionManager.isPermissionEnabled("change_settings"));
                permissionManager.setPermissionOnEnabled("change_settings", e -> {
                    Permission permission = e.getPermission();
                    System.out.println("Permission " + permission.getName() + " Enabled!");
                });
                permissionManager.setPermissionOnDisabled("change_settings", e -> {
                    Permission permission = e.getPermission();
                    System.out.println("Permission " + permission.getName() + " Disabled!");
                });
                permissionManager.setAdminPermission(false);
                print("Is change_settings enabled: " + permissionManager.isPermissionEnabled("change_settings"));
                print(permissionManager.removePermission("admin"));
                print(permissionManager.removePermission("change_settings"));

                MultiSessionManager multiSessionManager = new MultiSessionManager(userManager);

                multiSessionManager.setOnSessionOpened(e -> {
                    String username = e.getSession().getUsername();
                    System.out.println(username + " Logged In Successfully!");
                });

                multiSessionManager.setOnSessionClosed(e -> {
                    String username = e.getSession().getUsername();
                    System.out.println(username + " Logged Out Successfully!");
                });


                multiSessionManager.setMaxSessions(-1);

                print(multiSessionManager.loginUser("admin"));
                print(multiSessionManager.loginUser("jlgager"));
                print(multiSessionManager.getSessionsCount());
                print(multiSessionManager.logoutUser("admin"));
                print(multiSessionManager.logoutUser("jlgager"));
                print(multiSessionManager.getSessionsCount());
            }
            print("------------------------------------");

            Platform.exit();
        //} catch(XmlPullParserException | IOException | InterruptedException e) {
        /*} catch(Exception e) {
            MessageBox.show(e.getMessage(), "Error", MessageBoxIcon.ERROR);
        }*/
    }
}

