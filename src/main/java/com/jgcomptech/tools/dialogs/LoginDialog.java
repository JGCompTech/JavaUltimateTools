package com.jgcomptech.tools.dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Creates a Login Dialog for use to authenticate passwords
 * <p>
 *     Use setTitle to set the title of the window<br>
 *     Use setHeader to set the header of the window<br>
 *     Use setIconPath to set the icon of the window, path must be in the resource folder
 * </p>
 */
public class LoginDialog extends Dialog {
    private String iconPath = "";

    /**
     * Returns the path in the resource folder of the window icon
     * @return the icon path
     */
    public String getIconPath() { return iconPath; }

    /**
     * Sets the path in the resource folder of the window icon
     * @param iconPath the icon path
     */
    public void setIconPath(String iconPath)
    {
        this.iconPath = iconPath;

        final Stage stage = (Stage) getDialogPane().getScene().getWindow();
        if(!iconPath.isEmpty()) stage.getIcons().add(new Image(iconPath));
    }

    public LoginDialog(String warningText, boolean redText) {
        setTitle("Login");
        setHeaderText("Please Login to Continue!");

        // Create the username and password labels and fields.
        final GridPane mainGrid = new GridPane();

        final Label status = new Label(warningText);
        status.setPadding(new Insets(0, 0, 0, 10));
        status.setFont(new Font("Arial", 16));
        if(redText) status.setTextFill(Color.RED);
        if(redText) status.setGraphic(new ImageView(getClass().getResource("/img/Lock-Red.png").toString()));
        mainGrid.add(status, 0, 0);

        final GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setPadding(new Insets(0, 100, 10, 10));

        buttonGrid.getStylesheets().add(this.getClass().getResource("/css/LoginDialog.css").toExternalForm());

        // Set the icon (must be included in the project).
        setGraphic(new ImageView(getClass().getResource("/img/Data-Secure.png").toString()));

        final Label usernamelbl = new Label("Username:");
        usernamelbl.setFont(new Font("Arial", 16));
        final TextField username = new TextField();
        username.setId("txtUsername");
        username.setPromptText("Username");
        username.setFont(new Font("Arial", 16));

        final Label passwordlbl = new Label("Password:");
        passwordlbl.setFont(new Font("Arial", 16));
        final PasswordField password = new PasswordField();
        password.setId("txtPassword");
        password.setPromptText("Password");
        password.setFont(new Font("Arial", 16));

        buttonGrid.add(usernamelbl, 0, 1);
        buttonGrid.add(username, 1, 1);
        buttonGrid.add(passwordlbl, 0, 2);
        buttonGrid.add(password, 1, 2);

        mainGrid.add(buttonGrid, 0, 1);

        getDialogPane().setContent(mainGrid);

        //Set dialog result data for login button
        final ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        //By default disable the login button
        final Node loginButton = getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Enable/Disable login button depending on whether a username was entered.
        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) ->
                loginButton.setDisable(newValue.trim().isEmpty()));

        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });
    }
}
