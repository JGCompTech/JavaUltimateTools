package com.jgcomptech.tools.dialogs;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Dialog;
import javafx.util.Pair;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Creates a Login Dialog for use to authenticate passwords.
 * <p>
 *     Use setTitle to set the title of the window<br>
 *     Use setHeaderText to set the header of the window<br>
 *     Use setIconPath to set the icon of the window, path must be in the resource folder
 * </p>
 * @since 1.3.0
 * @since 1.5.0 changed to wrapper class removing Platform.runLater() requirement.
 */
public final class LoginDialog {
    private final ObjectProperty<LoginDialogImpl> dialog = new SimpleObjectProperty<>();

    public LoginDialog(final String warningText, final boolean redText) {
        if(Platform.isFxApplicationThread()) dialog.set(new LoginDialogImpl(warningText, redText));
        else {
            Platform.runLater(() -> dialog.set(new LoginDialogImpl(warningText, redText)));

            while(dialog.get() == null) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Shows the dialog and waits for the user response (in other words, brings
     * up a blocking dialog, with the returned value the users input).
     * The first string value is the username and the second is the password.
     * @return An {@link Optional} that contains the result.
     *         Refer to the {@link Dialog} class documentation for more detail.
     * @throws IllegalStateException if this method is called during
     *     animation or layout processing.
     */
    public Optional<Pair<String, String>> showAndWait() {
        if(Platform.isFxApplicationThread()) return dialog.get().showAndWait();
        else {
            final ObjectProperty<Optional<Pair<String, String>>> result = new SimpleObjectProperty<>();
            Platform.runLater(() -> result.set(dialog.get().showAndWait()));

            while(!result.get().isPresent()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return result.get();
        }
    }

    /**
     * Returns the path in the resource folder of the window icon.
     * @return the icon path
     */
    public String getIconPath() { return dialog.get().getIconPath(); }

    /**
     * Sets the path in the resource folder of the window icon.
     * @param iconPath the icon path
     */
    public void setIconPath(final String iconPath) {
        if(Platform.isFxApplicationThread()) dialog.get().setIconPath(iconPath);
        else Platform.runLater(() -> dialog.get().setIconPath(iconPath));
    }

    /** Closes the dialog. */
    public void close() {
        if(Platform.isFxApplicationThread()) dialog.get().close();
        else Platform.runLater(() -> dialog.get().close());
    }

    /** Hides the dialog. */
    public void hide() {
        if(Platform.isFxApplicationThread()) dialog.get().hide();
        else Platform.runLater(() -> dialog.get().hide());
    }

    /**
     * Return the title of the dialog.
     * @return the title of the dialog
     */
    public String getTitle() { return dialog.get().getTitle(); }

    /**
     * Change the title of the dialog.
     * @param title the title to set
     */
    public void setTitle(final String title) {
        if(Platform.isFxApplicationThread()) dialog.get().setTitle(title);
        else Platform.runLater(() -> dialog.get().setTitle(title));
    }

    /**
     * Returns the string to show in the dialog header area.
     * @return the string to show in the dialog header area
     */
    public String getHeaderText() { return dialog.get().getHeaderText(); }

    /**
     * Sets the string to show in the dialog header area.
     * @param headerText the header text to set
     */
    public void setHeaderText(final String headerText) {
        if(Platform.isFxApplicationThread()) dialog.get().setHeaderText(headerText);
        else Platform.runLater(() -> dialog.get().setHeaderText(headerText));
    }

    /**
     * Returns whether or not the dialog is showing.
     * @return true if dialog is showing
     */
    public boolean isShowing() { return dialog.get().isShowing(); }


    /**
     * Returns the LoginDialog Dialog implementation object.
     * @return the LoginDialog Dialog implementation object
     */
    public LoginDialogImpl getDialog() { return dialog.get(); }
}
