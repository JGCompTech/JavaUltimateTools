package com.jgcomptech.tools.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Displays message box with specified options.
 * @since 1.3.0
 * @since 1.5.0 changed to wrapper class removing Platform.runLater() requirement.
 */
final class MessageBoxImpl {
    public static DialogResult show(final String text, final String title, final String headerText,
                                    final MessageBoxButtons buttons, final MessageBoxIcon icon,
                                    final MessageBoxDefaultButton defaultButton) {
        switch(buttons) {
            case AbortRetryIgnore:
                return MessageBoxImpl.showAbortRetryIgnore(text, title, headerText, icon, defaultButton);
            case Apply:
                return MessageBoxImpl.showApply(text, title, headerText, icon);
            case ApplyCancel:
                return MessageBoxImpl.showApplyCancel(text, title, headerText, icon, defaultButton);
            case CancelTryAgainContinue:
                return MessageBoxImpl.showCancelTryAgainContinue(text, title, headerText, icon, defaultButton);
            case Close:
                return MessageBoxImpl.showClose(text, title, headerText, icon);
            case Finish:
                return MessageBoxImpl.showFinish(text, title, headerText, icon);
            case FinishCancel:
                return MessageBoxImpl.showFinishCancel(text, title, headerText, icon, defaultButton);
            case NextPrevious:
                return MessageBoxImpl.showNextPrevious(text, title, headerText, icon, defaultButton);
            case NextPreviousCancel:
                return MessageBoxImpl.showNextPreviousCancel(text, title, headerText, icon, defaultButton);
            case OK:
                return MessageBoxImpl.showOK(text, title, headerText, icon);
            case OKCancel:
                return MessageBoxImpl.showOKCancel(text, title, headerText, icon, defaultButton);
            case RetryCancel:
                return MessageBoxImpl.showRetryCancel(text, title, headerText, icon, defaultButton);
            case SubmitCancel:
                return MessageBoxImpl.showSubmitCancel(text, title, headerText, icon, defaultButton);
            case YesNo:
                return MessageBoxImpl.showYesNo(text, title, headerText, icon, defaultButton);
            case YesNoCancel:
                return MessageBoxImpl.showYesNoCancel(text, title, headerText, icon, defaultButton);
        }

        return DialogResult.NONE;
    }

    private static DialogResult showAbortRetryIgnore(final String text,
                                                     final String title,
                                                     final String headerText,
                                                     final MessageBoxIcon icon,
                                                     final MessageBoxDefaultButton defaultButton) {
        final var type = setAlertType(icon);
        Optional<ButtonType> result = Optional.empty();
        final var alert = new Alert(type, text,
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
                default:
            }
        }

        return DialogResult.NONE;
    }

    private static DialogResult showApply(final String text,
                                          final String title,
                                          final String headerText,
                                          final MessageBoxIcon icon) {
        final var type = setAlertType(icon);
        final Optional<ButtonType> result;
        final var alert = new Alert(type, text,
                MessageBoxButtonType.APPLY);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        result = alert.showAndWait();
        if(result.isPresent() && result.get() == MessageBoxButtonType.APPLY) {
            return DialogResult.APPLY;
        }

        return DialogResult.NONE;
    }

    private static DialogResult showApplyCancel(final String text,
                                                final String title,
                                                final String headerText,
                                                final MessageBoxIcon icon,
                                                final MessageBoxDefaultButton defaultButton) {
        final var type = setAlertType(icon);
        Optional<ButtonType> result = Optional.empty();
        final var alert = new Alert(type, text,
                MessageBoxButtonType.APPLY,
                MessageBoxButtonType.CANCEL);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        switch(defaultButton) {
            case Button1:
            case Button2:
                result = setDefaultButton(alert, MessageBoxButtonType.APPLY).showAndWait();
                break;
            case Button3:
                result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                break;
        }
        if(result.isPresent()) {
            switch(result.get().getButtonData()) {
                case APPLY:
                    return DialogResult.APPLY;
                case CANCEL_CLOSE:
                    return DialogResult.CANCEL;
                default:
            }
        }

        return DialogResult.NONE;
    }

    private static DialogResult showCancelTryAgainContinue(final String text,
                                                           final String title,
                                                           final String headerText,
                                                           final MessageBoxIcon icon,
                                                           final MessageBoxDefaultButton defaultButton) {
        final var type = setAlertType(icon);
        Optional<ButtonType> result = Optional.empty();
        final var alert = new Alert(type, text,
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
                default:
            }
        }

        return DialogResult.NONE;
    }

    private static DialogResult showClose(final String text,
                                          final String title,
                                          final String headerText,
                                          final MessageBoxIcon icon) {
        final var type = setAlertType(icon);
        final Optional<ButtonType> result;
        final var alert = new Alert(type, text,
                MessageBoxButtonType.CLOSE);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        result = alert.showAndWait();
        if(result.isPresent() && result.get() == MessageBoxButtonType.CLOSE) {
            return DialogResult.CLOSE;
        }

        return DialogResult.NONE;
    }

    private static DialogResult showFinish(final String text,
                                           final String title,
                                           final String headerText,
                                           final MessageBoxIcon icon) {
        final var type = setAlertType(icon);
        final Optional<ButtonType> result;
        final var alert = new Alert(type, text,
                MessageBoxButtonType.FINISH);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        result = alert.showAndWait();
        if(result.isPresent() && result.get() == MessageBoxButtonType.FINISH) {
            return DialogResult.FINISH;
        }

        return DialogResult.NONE;
    }

    private static DialogResult showFinishCancel(final String text,
                                                 final String title,
                                                 final String headerText,
                                                 final MessageBoxIcon icon,
                                                 final MessageBoxDefaultButton defaultButton) {
        final var type = setAlertType(icon);
        Optional<ButtonType> result = Optional.empty();
        final var alert = new Alert(type, text,
                MessageBoxButtonType.FINISH,
                MessageBoxButtonType.CANCEL);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        switch(defaultButton) {
            case Button1:
            case Button2:
                result = setDefaultButton(alert, MessageBoxButtonType.FINISH).showAndWait();
                break;
            case Button3:
                result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                break;
        }
        if(result.isPresent()) {
            switch(result.get().getButtonData()) {
                case FINISH:
                    return DialogResult.FINISH;
                case CANCEL_CLOSE:
                    return DialogResult.CANCEL;
                default:
            }
        }

        return DialogResult.NONE;
    }

    private static DialogResult showNextPrevious(final String text,
                                                 final String title,
                                                 final String headerText,
                                                 final MessageBoxIcon icon,
                                                 final MessageBoxDefaultButton defaultButton) {
        final var type = setAlertType(icon);
        Optional<ButtonType> result = Optional.empty();
        final var alert = new Alert(type, text,
                MessageBoxButtonType.NEXT,
                MessageBoxButtonType.PREVIOUS);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        switch(defaultButton) {
            case Button1:
            case Button2:
                result = setDefaultButton(alert, MessageBoxButtonType.NEXT).showAndWait();
                break;
            case Button3:
                result = setDefaultButton(alert, MessageBoxButtonType.PREVIOUS).showAndWait();
                break;
        }
        if(result.isPresent()) {
            switch(result.get().getButtonData()) {
                case NEXT_FORWARD:
                    return DialogResult.NEXT;
                case BACK_PREVIOUS:
                    return DialogResult.PREVIOUS;
                default:
            }
        }

        return DialogResult.NONE;
    }

    private static DialogResult showNextPreviousCancel(final String text,
                                                       final String title,
                                                       final String headerText,
                                                       final MessageBoxIcon icon,
                                                       final MessageBoxDefaultButton defaultButton) {
        final var type = setAlertType(icon);
        Optional<ButtonType> result = Optional.empty();
        final var alert = new Alert(type, text,
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
                default:
            }
        }

        return DialogResult.NONE;
    }

    private static DialogResult showOK(final String text,
                                       final String title,
                                       final String headerText,
                                       final MessageBoxIcon icon) {
        final var type = setAlertType(icon);
        final Optional<ButtonType> result;
        final var alert = new Alert(type, text,
                MessageBoxButtonType.OK);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        result = alert.showAndWait();
        if(result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            return DialogResult.OK;
        }

        return DialogResult.NONE;
    }

    private static DialogResult showOKCancel(final String text,
                                             final String title,
                                             final String headerText,
                                             final MessageBoxIcon icon,
                                             final MessageBoxDefaultButton defaultButton) {
        final var type = setAlertType(icon);
        Optional<ButtonType> result = Optional.empty();
        final var alert = new Alert(type, text,
                MessageBoxButtonType.OK,
                MessageBoxButtonType.CANCEL);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        switch(defaultButton) {
            case Button1:
            case Button2:
                result = setDefaultButton(alert, MessageBoxButtonType.OK).showAndWait();
                break;
            case Button3:
                result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                break;
        }
        if(result.isPresent()) {
            switch(result.get().getButtonData()) {
                case OK_DONE:
                    return DialogResult.OK;
                case CANCEL_CLOSE:
                    return DialogResult.CANCEL;
                default:
            }
        }

        return DialogResult.NONE;
    }

    private static DialogResult showRetryCancel(final String text,
                                                final String title,
                                                final String headerText,
                                                final MessageBoxIcon icon,
                                                final MessageBoxDefaultButton defaultButton) {
        final var type = setAlertType(icon);
        Optional<ButtonType> result = Optional.empty();
        final var alert = new Alert(type, text,
                MessageBoxButtonType.RETRY,
                MessageBoxButtonType.CANCEL);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        switch(defaultButton) {
            case Button1:
            case Button2:
                result = setDefaultButton(alert, MessageBoxButtonType.RETRY).showAndWait();
                break;
            case Button3:
                result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                break;
        }
        if(result.isPresent()) {
            switch(result.get().getButtonData()) {
                case BACK_PREVIOUS:
                    return DialogResult.RETRY;
                case CANCEL_CLOSE:
                    return DialogResult.CANCEL;
                default:
            }
        }

        return DialogResult.NONE;
    }

    private static DialogResult showSubmitCancel(final String text,
                                                 final String title,
                                                 final String headerText,
                                                 final MessageBoxIcon icon,
                                                 final MessageBoxDefaultButton defaultButton) {
        final var type = setAlertType(icon);
        Optional<ButtonType> result = Optional.empty();
        final var alert = new Alert(type, text,
                MessageBoxButtonType.SUBMIT,
                MessageBoxButtonType.CANCEL);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        switch(defaultButton) {
            case Button1:
            case Button2:
                result = setDefaultButton(alert, MessageBoxButtonType.SUBMIT).showAndWait();
                break;
            case Button3:
                result = setDefaultButton(alert, MessageBoxButtonType.CANCEL).showAndWait();
                break;
        }
        if(result.isPresent()) {
            switch(result.get().getButtonData()) {
                case APPLY:
                    return DialogResult.SUBMIT;
                case CANCEL_CLOSE:
                    return DialogResult.CANCEL;
                default:
            }
        }

        return DialogResult.NONE;
    }

    private static DialogResult showYesNo(final String text,
                                          final String title,
                                          final String headerText,
                                          final MessageBoxIcon icon,
                                          final MessageBoxDefaultButton defaultButton) {
        final var type = setAlertType(icon);
        Optional<ButtonType> result = Optional.empty();
        final var alert = new Alert(type, text,
                MessageBoxButtonType.YES,
                MessageBoxButtonType.NO);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        switch(defaultButton) {
            case Button1:
            case Button2:
                result = setDefaultButton(alert, MessageBoxButtonType.YES).showAndWait();
                break;
            case Button3:
                result = setDefaultButton(alert, MessageBoxButtonType.NO).showAndWait();
                break;
        }
        if(result.isPresent()) {
            switch(result.get().getButtonData()) {
                case YES:
                    return DialogResult.YES;
                case NO:
                    return DialogResult.NO;
                default:
            }
        }

        return DialogResult.NONE;
    }

    private static DialogResult showYesNoCancel(final String text,
                                                final String title,
                                                final String headerText,
                                                final MessageBoxIcon icon,
                                                final MessageBoxDefaultButton defaultButton) {
        final var type = setAlertType(icon);
        Optional<ButtonType> result = Optional.empty();
        final var alert = new Alert(type, text,
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
                default:
            }
        }

        return DialogResult.NONE;
    }

    private static Alert setDefaultButton(final Alert alert, final ButtonType type) {
        final var pane = alert.getDialogPane();
        for (final var t : alert.getButtonTypes())
            ((Button) pane.lookupButton(t)).setDefaultButton(t.equals(type));
        return alert;
    }

    private static Alert.AlertType setAlertType(final MessageBoxIcon icon) {
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
}
