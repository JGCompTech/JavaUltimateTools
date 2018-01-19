package com.jgcomptech.tools.dialogs;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * Used to specify which buttons should be shown to users in the message box.
 * @since 1.3.0
 */
public final class MessageBoxButtonType {
    /**
     * A pre-defined {@code ButtonType} that displays "Apply" and has a
     * {@code ButtonBar.ButtonData} of {@code ButtonBar.ButtonData.APPLY}.
     */
    public static final ButtonType APPLY = ButtonType.APPLY;

    /**
     * A pre-defined {@code ButtonType} that displays "OK" and has a
     * {@code ButtonBar.ButtonData} of {@code ButtonBar.ButtonData.OK_DONE}.
     */
    public static final ButtonType OK = ButtonType.OK;

    /**
     * A pre-defined {@code ButtonType} that displays "Cancel" and has a
     * {@code ButtonBar.ButtonData} of {@code ButtonBar.ButtonData.CANCEL_CLOSE}.
     */
    public static final ButtonType CANCEL = ButtonType.CANCEL;

    /**
     * A pre-defined {@code ButtonType} that displays "Close" and has a
     * {@code ButtonBar.ButtonData} of {@code ButtonBar.ButtonData.CANCEL_CLOSE}.
     */
    public static final ButtonType CLOSE = ButtonType.CLOSE;

    /**
     * A pre-defined {@code ButtonType} that displays "Yes" and has a
     * {@code ButtonBar.ButtonData} of {@code ButtonBar.ButtonData.YES}.
     */
    public static final ButtonType YES = ButtonType.YES;

    /**
     * A pre-defined {@code ButtonType} that displays "No" and has a
     * {@code ButtonBar.ButtonData} of {@code ButtonBar.ButtonData.NO}.
     */
    public static final ButtonType NO = ButtonType.NO;

    /**
     * A pre-defined {@code ButtonType} that displays "Finish" and has a
     * {@code ButtonBar.ButtonData} of {@code ButtonBar.ButtonData.FINISH}.
     */
    public static final ButtonType FINISH = ButtonType.FINISH;

    /**
     * A pre-defined {@code ButtonType} that displays "Next" and has a
     * {@code ButtonBar.ButtonData} of {@code ButtonBar.ButtonData.NEXT_FORWARD}.
     */
    public static final ButtonType NEXT = ButtonType.NEXT;

    /**
     * A pre-defined {@code ButtonType} that displays "Previous" and has a
     * {@code ButtonBar.ButtonData} of {@code ButtonBar.ButtonData.BACK_PREVIOUS}.
     */
    public static final ButtonType PREVIOUS = ButtonType.PREVIOUS;

    public static final ButtonType ABORT = new ButtonType("Abort", ButtonBar.ButtonData.CANCEL_CLOSE);

    public static final ButtonType RETRY = new ButtonType("Retry", ButtonBar.ButtonData.BACK_PREVIOUS);

    public static final ButtonType TRYAGAIN = new ButtonType("Try Again", ButtonBar.ButtonData.BACK_PREVIOUS);

    public static final ButtonType IGNORE = new ButtonType("Ignore", ButtonBar.ButtonData.NEXT_FORWARD);

    public static final ButtonType CONTINUE = new ButtonType("Continue", ButtonBar.ButtonData.NEXT_FORWARD);

    public static final ButtonType SUBMIT = new ButtonType("Submit", ButtonBar.ButtonData.APPLY);

    public static final ButtonType NONE = new ButtonType("");

    /** Prevents instantiation of this utility class. */
    private MessageBoxButtonType() { }
}
