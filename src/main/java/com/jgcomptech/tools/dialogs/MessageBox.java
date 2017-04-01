package com.jgcomptech.tools.dialogs;

import javafx.scene.control.*;

import java.util.Optional;

/**
 * Displays message box with specified options
 */
public class MessageBox {
    /**
     * Displays message box with specified text, title, header text, buttons, icon and default button
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, String headerText, MessageBoxButtons buttons,
                                    MessageBoxIcon icon, MessageBoxDefaultButton defaultButton) {
        final Alert alert;
        final Alert.AlertType type = setAlertType(icon);
        Optional<ButtonType> result = null;

        switch(buttons) {
            case AbortRetryIgnore:
                alert = new Alert(type, text,
                        MessageBoxButtonType.ABORT,
                        MessageBoxButtonType.RETRY,
                        MessageBoxButtonType.IGNORE);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                switch(defaultButton) {
                    case Button1:
                        result = setDefaultButton(alert, MessageBoxButtonType.ABORT).showAndWait();
                        break;
                    case Button2:
                        result = setDefaultButton(alert, MessageBoxButtonType.RETRY).showAndWait();
                        break;
                    case Button3:
                        result = setDefaultButton(alert, MessageBoxButtonType.IGNORE).showAndWait();
                        break;
                }
                if(result.isPresent()) {
                    switch(result.get().getButtonData()) {
                        case CANCEL_CLOSE:
                            return DialogResult.ABORT;
                        case BACK_PREVIOUS:
                            return DialogResult.RETRY;
                        case NEXT_FORWARD:
                            return DialogResult.IGNORE;
                    }
                }
                break;
            case Apply:
                alert = new Alert(type, text,
                        MessageBoxButtonType.APPLY);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                result = alert.showAndWait();
                if(result.isPresent() && result.get() == MessageBoxButtonType.APPLY) {
                    return DialogResult.APPLY;
                }
                break;
            case ApplyCancel:
                alert = new Alert(type, text,
                        MessageBoxButtonType.APPLY,
                        MessageBoxButtonType.CANCEL);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                switch(defaultButton) {
                    case Button1:
                    case Button3:
                        result = setDefaultButton(alert, MessageBoxButtonType.APPLY).showAndWait();
                        break;
                    case Button2:
                        result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                        break;
                }
                if(result.isPresent()) {
                    switch(result.get().getButtonData()) {
                        case APPLY:
                            return DialogResult.APPLY;
                        case CANCEL_CLOSE:
                            return DialogResult.CANCEL;
                    }
                }
                break;
            case CancelTryAgainContinue:
                alert = new Alert(type, text,
                        MessageBoxButtonType.CANCEL,
                        MessageBoxButtonType.TRYAGAIN,
                        MessageBoxButtonType.CONTINUE);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                switch(defaultButton) {
                    case Button1:
                        result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                        break;
                    case Button2:
                        result = setDefaultButton(alert, MessageBoxButtonType.TRYAGAIN).showAndWait();
                        break;
                    case Button3:
                        result = setDefaultButton(alert, MessageBoxButtonType.CONTINUE).showAndWait();
                        break;
                }
                if(result.isPresent()) {
                    switch(result.get().getButtonData()) {
                        case CANCEL_CLOSE:
                            return DialogResult.CANCEL;
                        case BACK_PREVIOUS:
                            return DialogResult.TRYAGAIN;
                        case NEXT_FORWARD:
                            return DialogResult.CONTINUE;
                    }
                }
                break;
            case Close:
                alert = new Alert(type, text,
                        MessageBoxButtonType.CLOSE);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                result = alert.showAndWait();
                if(result.isPresent() && result.get() == MessageBoxButtonType.CLOSE) {
                    return DialogResult.CLOSE;
                }
                break;
            case Finish:
                alert = new Alert(type, text,
                        MessageBoxButtonType.FINISH);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                result = alert.showAndWait();
                if(result.isPresent() && result.get() == MessageBoxButtonType.FINISH) {
                    return DialogResult.FINISH;
                }
                break;
            case FinishCancel:
                alert = new Alert(type, text,
                        MessageBoxButtonType.FINISH,
                        MessageBoxButtonType.CANCEL);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                switch(defaultButton) {
                    case Button1:
                    case Button3:
                        result = setDefaultButton(alert, MessageBoxButtonType.FINISH).showAndWait();
                        break;
                    case Button2:
                        result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                        break;
                }
                if(result.isPresent()) {
                    switch(result.get().getButtonData()) {
                        case FINISH:
                            return DialogResult.FINISH;
                        case CANCEL_CLOSE:
                            return DialogResult.CANCEL;
                    }
                }
                break;
            case NextPrevious:
                alert = new Alert(type, text,
                        MessageBoxButtonType.NEXT,
                        MessageBoxButtonType.PREVIOUS);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                switch(defaultButton) {
                    case Button1:
                    case Button3:
                        result = setDefaultButton(alert, MessageBoxButtonType.NEXT).showAndWait();
                        break;
                    case Button2:
                        result = setDefaultButton(alert, MessageBoxButtonType.PREVIOUS).showAndWait();
                        break;
                }
                if(result.isPresent()) {
                    switch(result.get().getButtonData()) {
                        case NEXT_FORWARD:
                            return DialogResult.NEXT;
                        case BACK_PREVIOUS:
                            return DialogResult.PREVIOUS;
                    }
                }
                break;
            case NextPreviousCancel:
                alert = new Alert(type, text,
                        MessageBoxButtonType.NEXT,
                        MessageBoxButtonType.PREVIOUS,
                        MessageBoxButtonType.CANCEL);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                switch(defaultButton) {
                    case Button1:
                        result = setDefaultButton(alert, MessageBoxButtonType.NEXT).showAndWait();
                        break;
                    case Button2:
                        result = setDefaultButton(alert, MessageBoxButtonType.PREVIOUS).showAndWait();
                        break;
                    case Button3:
                        result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                        break;
                }
                if(result.isPresent()) {
                    switch(result.get().getButtonData()) {
                        case NEXT_FORWARD:
                            return DialogResult.NEXT;
                        case BACK_PREVIOUS:
                            return DialogResult.PREVIOUS;
                        case CANCEL_CLOSE:
                            return DialogResult.CANCEL;
                    }
                }
                break;
            case OK:
                alert = new Alert(type, text,
                        MessageBoxButtonType.OK);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                result = alert.showAndWait();
                if(result.isPresent()) {
                    if(result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        return DialogResult.OK;
                    }
                }
                break;
            case OKCancel:
                alert = new Alert(type, text,
                        MessageBoxButtonType.OK,
                        MessageBoxButtonType.CANCEL);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                switch(defaultButton) {
                    case Button1:
                    case Button3:
                        result = setDefaultButton(alert, MessageBoxButtonType.OK).showAndWait();
                        break;
                    case Button2:
                        result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                        break;
                }
                if(result.isPresent()) {
                    switch(result.get().getButtonData()) {
                        case OK_DONE:
                            return DialogResult.OK;
                        case CANCEL_CLOSE:
                            return DialogResult.CANCEL;
                    }
                }
                break;
            case RetryCancel:
                alert = new Alert(type, text,
                        MessageBoxButtonType.RETRY,
                        MessageBoxButtonType.CANCEL);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                switch(defaultButton) {
                    case Button1:
                    case Button3:
                        result = setDefaultButton(alert, MessageBoxButtonType.RETRY).showAndWait();
                        break;
                    case Button2:
                        result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                        break;
                }
                if(result.isPresent()) {
                    switch(result.get().getButtonData()) {
                        case BACK_PREVIOUS:
                            return DialogResult.RETRY;
                        case CANCEL_CLOSE:
                            return DialogResult.CANCEL;
                    }
                }
                break;
            case SubmitCancel:
                alert = new Alert(type, text,
                        MessageBoxButtonType.SUBMIT,
                        MessageBoxButtonType.CANCEL);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                switch(defaultButton) {
                    case Button1:
                    case Button3:
                        result = setDefaultButton(alert, MessageBoxButtonType.SUBMIT).showAndWait();
                        break;
                    case Button2:
                        result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                        break;
                }
                if(result.isPresent()) {
                    switch(result.get().getButtonData()) {
                        case APPLY:
                            return DialogResult.SUBMIT;
                        case CANCEL_CLOSE:
                            return DialogResult.CANCEL;
                    }
                }
                break;
            case YesNo:
                alert = new Alert(type, text,
                        MessageBoxButtonType.YES,
                        MessageBoxButtonType.NO);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                switch(defaultButton) {
                    case Button1:
                    case Button3:
                        result = setDefaultButton(alert, MessageBoxButtonType.YES).showAndWait();
                        break;
                    case Button2:
                        result = setDefaultButton(alert, MessageBoxButtonType.NO).showAndWait();
                        break;
                }
                if(result.isPresent()) {
                    switch(result.get().getButtonData()) {
                        case YES:
                            return DialogResult.YES;
                        case NO:
                            return DialogResult.NO;
                    }
                }
                break;
            case YesNoCancel:
                alert = new Alert(type, text,
                        MessageBoxButtonType.YES,
                        MessageBoxButtonType.NO,
                        MessageBoxButtonType.CANCEL);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                switch(defaultButton) {
                    case Button1:
                        result = setDefaultButton(alert, MessageBoxButtonType.YES).showAndWait();
                        break;
                    case Button2:
                        result = setDefaultButton(alert, MessageBoxButtonType.NO).showAndWait();
                        break;
                    case Button3:
                        result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                        break;
                }
                if(result.isPresent()) {
                    switch(result.get().getButtonData()) {
                        case YES:
                            return DialogResult.YES;
                        case NO:
                            return DialogResult.NO;
                        case CANCEL_CLOSE:
                            return DialogResult.CANCEL;
                    }
                }
                break;
        }

        return DialogResult.NONE;
    }

    /**
     * Displays message box with specified text, title, header text, buttons and icon
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, String headerText, MessageBoxButtons buttons,
                                    MessageBoxIcon icon) {
        return show(text, title, headerText, buttons, icon, MessageBoxDefaultButton.Button1);
    }

    /**
     * Displays message box with specified text, title, header text, buttons and default button
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, String headerText, MessageBoxButtons buttons,
                                    MessageBoxDefaultButton defaultButton) {
        return show(text, title, headerText, buttons, MessageBoxIcon.NONE, defaultButton);
    }

    /**
     * Displays message box with specified text, title, header text, icon and default button
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, String headerText, MessageBoxIcon icon,
                                    MessageBoxDefaultButton defaultButton) {
        MessageBoxButtons buttons = MessageBoxButtons.OK;
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
     * Displays message box with specified text, title, buttons, icon and default button
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, MessageBoxButtons buttons,
                                    MessageBoxIcon icon, MessageBoxDefaultButton defaultButton) {
        return show(text, title, "", buttons, icon, defaultButton);
    }

    /**
     * Displays message box with specified text, title, buttons and default button
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, MessageBoxButtons buttons,
                                    MessageBoxDefaultButton defaultButton) {
        return show(text, title, buttons, MessageBoxIcon.NONE, defaultButton);
    }

    /**
     * Displays message box with specified text, title, icon and default button
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @param defaultButton One of the MessageBoxDefaultButton values that specifies the default button for the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, MessageBoxIcon icon,
                                    MessageBoxDefaultButton defaultButton) {
        MessageBoxButtons buttons = MessageBoxButtons.OK;
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
     * Displays message box with specified text, title, header text and buttons
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, String headerText, MessageBoxButtons buttons) {
        return show(text, title, headerText, buttons, MessageBoxIcon.NONE, MessageBoxDefaultButton.Button1);
    }

    /**
     * Displays message box with specified text, title, header text and icon
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, String headerText, MessageBoxIcon icon) {
        MessageBoxButtons buttons = MessageBoxButtons.OK;
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
     * Displays message box with specified text, title and buttons
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param buttons One of the MessageBoxButtons values that specifies which buttons to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, MessageBoxButtons buttons) {
        return show(text, title, "", buttons);
    }

    /**
     * Displays message box with specified text, title and icon
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param icon One of the MessageBoxIcons values that specifies which icon to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, MessageBoxIcon icon) {
        return show(text, title, "", icon);
    }

    /**
     * Displays message box with specified text, title and header text
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @param headerText The text to display in the header section of the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title, String headerText) {
        return show(text, title, headerText, MessageBoxButtons.OK);
    }

    /**
     * Displays message box with specified text and title
     * @param text The text to display in the message box
     * @param title The text to display in the title bar of the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text, String title) { return show(text, title, ""); }

    /**
     * Displays message box with specified text
     * @param text The text to display in the message box
     * @return DialogResult representing the return value of the message box
     */
    public static DialogResult show(String text) {
        return show(text, "");
    }

    private static Alert.AlertType setAlertType(MessageBoxIcon icon) {
        switch(icon) {
            case NONE:
                return Alert.AlertType.NONE;
            case INFORMATION:
                return Alert.AlertType.INFORMATION;
            case WARNING:
                return Alert.AlertType.WARNING;
            case CONFIRMATION:
                return Alert.AlertType.CONFIRMATION;
            case ERROR:
                return Alert.AlertType.ERROR;
        }
        return Alert.AlertType.NONE;
    }

    private static Alert setDefaultButton ( Alert alert, ButtonType type ) {
        final DialogPane pane = alert.getDialogPane();
        for ( final ButtonType t : alert.getButtonTypes() )
            ( (Button) pane.lookupButton(t) ).setDefaultButton( t == type );
        return alert;
    }
}
