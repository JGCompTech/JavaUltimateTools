package com.jgcomptech.tools.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Loads a dialog using the specified JavaFX fxml file.
 * @param <ReturnType> the class type to be returned when the dialog is closed.
 * @param <Controller> the controller class to be used for the dialog.
 */
public final class FXMLDialogWrapper<ReturnType, Controller extends Initializable> {
    private final Dialog<ReturnType> dialog;
    private final FXMLLoader loader;

    //region Constructor Overflows
    /**
     * Creates an instance of a dialog and sets the specified fxml file path.
     * @param fxmlPath the path of the fxml file
     * @throws IOException if fxml file fails to load
     */
    public FXMLDialogWrapper(String fxmlPath) throws IOException {
        this(null, null, null, fxmlPath);
    }

    /**
     * Creates an instance of a dialog and sets the specified title and fxml file path.
     * @param title the title
     * @param fxmlPath the path of the fxml file
     * @throws IOException if fxml file fails to load
     */
    public FXMLDialogWrapper(String title, String fxmlPath) throws IOException {
        this(title, null, null, fxmlPath);
    }

    /**
     * Creates an instance of a dialog and sets the specified icon and fxml file path.
     * @param icon the icon image for the to be used in the window decorations and when minimized
     * @param fxmlPath the path of the fxml file
     * @throws IOException if fxml file fails to load
     */
    public FXMLDialogWrapper(Image icon, String fxmlPath) throws IOException {
        this(null, icon, null, fxmlPath);
    }

    /**
     * Creates an instance of a dialog and sets the specified owner and fxml file path.
     * @param owner the owner Window
     * @param fxmlPath the path of the fxml file
     * @throws IOException if fxml file fails to load
     */
    public FXMLDialogWrapper(Stage owner, String fxmlPath) throws IOException {
        this(null, null, owner, fxmlPath);
    }

    /**
     * Creates an instance of a dialog and sets the specified icon, owner and fxml file path.
     * @param icon the icon image for the to be used in the window decorations and when minimized
     * @param owner the owner Window
     * @param fxmlPath the path of the fxml file
     * @throws IOException if fxml file fails to load
     */
    public FXMLDialogWrapper(Image icon, Stage owner, String fxmlPath) throws IOException {
        this(null, icon, owner, fxmlPath);
    }

    /**
     * Creates an instance of a dialog and sets the specified title, owner and fxml file path.
     * @param title the title
     * @param owner the owner Window
     * @param fxmlPath the path of the fxml file
     * @throws IOException if fxml file fails to load
     */
    public FXMLDialogWrapper(String title, Stage owner, String fxmlPath) throws IOException {
        this(title, null, owner, fxmlPath);
    }

    /**
     * Creates an instance of a dialog and sets the specified title, icon and fxml file path.
     * @param title the title
     * @param icon the icon image for the to be used in the window decorations and when minimized
     * @param fxmlPath the path of the fxml file
     * @throws IOException if fxml file fails to load
     */
    public FXMLDialogWrapper(String title, Image icon, String fxmlPath) throws IOException {
        this(title, icon, null, fxmlPath);
    }
    //endregion Constructor Overflows

    /**
     * Creates an instance of a dialog and sets the specified title, icon, owner and fxml file path.
     * @param title the title
     * @param icon the icon image for the to be used in the window decorations and when minimized
     * @param owner the owner Window
     * @param fxmlPath the path of the fxml file
     * @throws IOException if fxml file fails to load
     */
    public FXMLDialogWrapper(String title, Image icon, Stage owner, String fxmlPath) throws IOException {
        if(fxmlPath == null || fxmlPath.isEmpty()) {
            throw new IllegalArgumentException("FXML path cannot be null or an empty string.");
        }
        else loader = new FXMLLoader(getClass().getResource(fxmlPath));

        dialog = loader.load();

        final Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.setResizable(false);
        dialogStage.initOwner(owner);

        if(title != null) {
            if(title.isEmpty()) throw new IllegalArgumentException("Title cannot be an empty string.");
            else dialogStage.setTitle(title);
        }
        if(title != null) {
            if(icon.isError()) throw new IllegalArgumentException("Icon cannot have loading errors.");
            else dialogStage.getIcons().add(icon);
        }
    }

    /**
     * Returns the {@code FXMLLoader} that was used to load the {@code Dialog}.
     * @return the {@code FXMLLoader} object
     */
    public FXMLLoader getLoader() { return loader; }

    /**
     * Returns the {@code Dialog} as a {@code Dialog} object.
     * @return the {@code Dialog} object
     */
    public Dialog<ReturnType> getDialog() { return dialog; }

    /**
     * Returns the controller associated with the {@code Dialog}
     * @return the {@code Controller} object
     */
    public Controller getController() { return loader.getController(); }

    /**
     * Defines the controller associated with the {@code Dialog}
     * @param controller the {@code Controller} object
     */
    public void getController(Initializable controller) { loader.setController(controller); }

    /**
     * Shows the dialog and waits for the user response (in other words, brings
     * up a blocking dialog, with the returned value the users input).
     * <p>
     * This method must be called on the JavaFX Application thread.
     * Additionally, it must either be called from an input event handler or
     * from the run method of a Runnable passed to
     * {@link javafx.application.Platform#runLater Platform.runLater}.
     * It must not be called during animation or layout processing.
     * </p>
     *
     * @return An {@link Optional} that contains the result.
     *         Refer to the {@link Dialog} class documentation for more detail.
     * @throws IllegalStateException if this method is called on a thread
     *     other than the JavaFX Application Thread.
     * @throws IllegalStateException if this method is called during
     *     animation or layout processing.
     */
    public Optional<ReturnType> showAndWait() { return dialog.showAndWait(); }

    /**
     * Defines the title of the {@code Dialog}.
     *
     * @param value the title to set.
     */
    public void setTitle(String value) {
        if(value == null || value.isEmpty()) throw new IllegalArgumentException("Title cannot be null or an empty string.");
        else {
            final Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            dialogStage.setTitle(value);
        }
    }

    /**
     * Defines the icon image for the {@code Dialog} to be used in the window decorations and when minimized.
     * @param value the icon image
     */
    public void setIcon(Image value) {
        if(value == null || value.isError()) throw new IllegalArgumentException("Icon cannot be null or have loading errors.");
        else {
            final Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(value);
        }
    }

    /**
     * Defines the owner Window for this {@code Dialog}, or null for a top-level,
     * unowned stage. This must be done prior to making the stage visible.
     *
     * @param value the owner for this dialog.
     *
     * @throws IllegalStateException if this property is set after the stage
     * has ever been made visible.
     *
     * @throws IllegalStateException if this stage is the primary stage.
     */
    public void setOwner(Stage value) {
        final Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.initOwner(value);
    }

    /**
     * Defines whether this {@code Dialog} is kept on top of other windows.
     * <p>
     * If some other window is already always-on-top then the
     * relative order between these windows is unspecified (depends on
     * platform).
     * </p>
     * <p>
     * There are differences in behavior between applications if a security
     * manager is present. Applications with permissions are allowed to set
     * "always on top" flag on a Stage. In applications without the proper
     * permissions, an attempt to set the flag will be ignored and the property
     * value will be restored to "false".
     * </p>
     *
     * @param value the value to set.
     */
    public void setAlwaysOnTop(boolean value) {
        final Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.setAlwaysOnTop(value);
    }

    /**
     * Returns the title of the {@code Dialog}.
     *
     * @return the title
     */
    public String getTitle() {
        final Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        return dialogStage.getTitle();
    }

    /**
     * Returns the icon image for the {@code Dialog} that is used in the window decorations and when minimized.
     * @return the icon image, null if no icon exists
     */
    public Image getIcon() {
        final Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        return dialogStage.getIcons().isEmpty() ? null : dialogStage.getIcons().get(0);
    }

    /**
     * Returns the owner Window for this {@code Dialog}, or null for a top-level,
     * unowned stage.
     *
     * @return the owner for this dialog.
     */
    public Stage getOwner() {
        final Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        return (Stage) dialogStage.getOwner();
    }

    /**
     * Returns whether this {@code Dialog} is kept on top of other windows.
     * <p>
     * If some other window is already always-on-top then the
     * relative order between these windows is unspecified (depends on
     * platform).
     * </p>
     * <p>
     * There are differences in behavior between applications if a security
     * manager is present. Applications with permissions are allowed to set
     * "always on top" flag on a Stage. In applications without the proper
     * permissions, an attempt to set the flag will be ignored and the property
     * value will be restored to "false".
     * </p>
     *
     * @return if dialog is kept on top of other windows
     */
    public boolean isAlwaysOnTop() {
        final Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        return dialogStage.isAlwaysOnTop();
    }
}
