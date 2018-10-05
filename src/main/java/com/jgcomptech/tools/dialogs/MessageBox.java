package com.jgcomptech.tools.dialogs;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.concurrent.TimeUnit;

/**
 * Displays message box with specified options.
 * @since 1.3.0
 * @since 1.5.0 changed to wrapper class removing Platform.runLater() requirement.
 */
public final class MessageBox {
    /**
     * Displays message box with specified text, title, header text, buttons, icon and default button.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button
     *                      for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title, final String headerText,
                                    final MessageBoxButtons buttons, final MessageBoxIcon icon,
                                    final MessageBoxDefaultButton defaultButton) {
        if(Platform.isFxApplicationThread()) {
            return MessageBoxImpl.show(text, title, headerText, buttons, icon, defaultButton);
        }
        else {
            final ObjectProperty<DialogResult> result = new SimpleObjectProperty<>();
            Platform.runLater(() ->
                    result.set(MessageBoxImpl.show(text, title, headerText, buttons, icon, defaultButton)));

            while (result.get() == null) {
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
     * Displays message box with specified text, title, header text, buttons and icon.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title, final String headerText,
                                    final MessageBoxButtons buttons, final MessageBoxIcon icon) {
        return show(text, title, headerText, buttons, icon, MessageBoxDefaultButton.Button1);
    }

    /**
     * Displays message box with specified text, title, header text, buttons and default button.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button
     *                      for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title, final String headerText,
                                    final MessageBoxButtons buttons, final MessageBoxDefaultButton defaultButton) {
        return show(text, title, headerText, buttons, MessageBoxIcon.NONE, defaultButton);
    }

    /**
     * Displays message box with specified text, title, header text, icon and default button.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button
     *                      for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title, final String headerText,
                                    final MessageBoxIcon icon, final MessageBoxDefaultButton defaultButton) {
        var buttons = MessageBoxButtons.OK;
        switch(icon) {
            case NONE:
                buttons = MessageBoxButtons.OK;
                break;
            case INFORMATION:
                buttons = MessageBoxButtons.OK;
                break;
            case WARNING:
                buttons = MessageBoxButtons.OK;
                break;
            case CONFIRMATION:
                buttons = MessageBoxButtons.OKCancel;
                break;
            case ERROR:
                buttons = MessageBoxButtons.OK;
                break;
        }
        return show(text, title, headerText, buttons, icon, defaultButton);
    }

    /**
     * Displays message box with specified text, title, buttons, icon and default button.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button
     *                      for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title, final MessageBoxButtons buttons,
                                    final MessageBoxIcon icon, final MessageBoxDefaultButton defaultButton) {
        return show(text, title, "", buttons, icon, defaultButton);
    }

    /**
     * Displays message box with specified text, title, buttons and default button.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button
     *                      for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title, final MessageBoxButtons buttons,
                                    final MessageBoxDefaultButton defaultButton) {
        return show(text, title, buttons, MessageBoxIcon.NONE, defaultButton);
    }

    /**
     * Displays message box with specified text, title, icon and default button.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button
     *                      for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title, final MessageBoxIcon icon,
                                    final MessageBoxDefaultButton defaultButton) {
        var buttons = MessageBoxButtons.OK;
        switch(icon) {
            case NONE:
                buttons = MessageBoxButtons.OK;
                break;
            case INFORMATION:
                buttons = MessageBoxButtons.OK;
                break;
            case WARNING:
                buttons = MessageBoxButtons.OK;
                break;
            case CONFIRMATION:
                buttons = MessageBoxButtons.OKCancel;
                break;
            case ERROR:
                buttons = MessageBoxButtons.OK;
                break;
        }
        return show(text, title, buttons, icon, defaultButton);
    }

    /**
     * Displays message box with specified text, title, header text and buttons.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title,
                                    final String headerText, final MessageBoxButtons buttons) {
        return show(text, title, headerText, buttons, MessageBoxIcon.NONE, MessageBoxDefaultButton.Button1);
    }

    /**
     * Displays message box with specified text, title, header text and icon.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title,
                                    final String headerText, final MessageBoxIcon icon) {
        var buttons = MessageBoxButtons.OK;
        switch(icon) {
            case NONE:
                buttons = MessageBoxButtons.OK;
                break;
            case INFORMATION:
                buttons = MessageBoxButtons.OK;
                break;
            case WARNING:
                buttons = MessageBoxButtons.OK;
                break;
            case CONFIRMATION:
                buttons = MessageBoxButtons.OKCancel;
                break;
            case ERROR:
                buttons = MessageBoxButtons.OK;
                break;
        }
        return show(text, title, headerText, buttons, icon, MessageBoxDefaultButton.Button1);
    }

    /**
     * Displays message box with specified text, title and buttons.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title, final MessageBoxButtons buttons) {
        return show(text, title, "", buttons);
    }

    /**
     * Displays message box with specified text, title and icon.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title, final MessageBoxIcon icon) {
        return show(text, title, "", icon);
    }

    /**
     * Displays message box with specified text, title and header text.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title, final String headerText) {
        return show(text, title, headerText, MessageBoxButtons.OK);
    }

    /**
     * Displays message box with specified text and title.
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text, final String title) { return show(text, title, ""); }

    /**
     * Displays message box with specified text.
     * @param text The text to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(final String text) { return show(text, ""); }

    /** Prevents instantiation of this utility class. */
    private MessageBox() { }
}
