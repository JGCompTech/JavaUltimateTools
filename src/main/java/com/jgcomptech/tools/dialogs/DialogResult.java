package com.jgcomptech.tools.dialogs;

import javafx.scene.control.ButtonType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Specifies identifiers to indicate the return value of a message box.
 * @since 1.3.0
 */
public final class DialogResult {
    /** The message box return value is OK (usually sent from a button labeled OK). */
    public static final DialogResult OK = new DialogResult(MessageBoxButtonType.OK);
    /** The message box return value is APPLY (usually sent from a button labeled APPLY). */
    public static final DialogResult APPLY = new DialogResult(MessageBoxButtonType.APPLY);
    /** The message box return value is CANCEL (usually sent from a button labeled CANCEL). */
    public static final DialogResult CANCEL = new DialogResult(MessageBoxButtonType.CANCEL);
    /** The message box return value is CLOSE (usually sent from a button labeled CLOSE). */
    public static final DialogResult CLOSE = new DialogResult(MessageBoxButtonType.CLOSE);
    /** The message box return value is FINISH (usually sent from a button labeled FINISH). */
    public static final DialogResult FINISH = new DialogResult(MessageBoxButtonType.FINISH);
    /** The message box return value is NEXT (usually sent from a button labeled NEXT). */
    public static final DialogResult NEXT = new DialogResult(MessageBoxButtonType.NEXT);
    /** The message box return value is NO (usually sent from a button labeled NO). */
    public static final DialogResult NO = new DialogResult(MessageBoxButtonType.NO);
    /** The message box return value is PREVIOUS (usually sent from a button labeled PREVIOUS). */
    public static final DialogResult PREVIOUS = new DialogResult(MessageBoxButtonType.PREVIOUS);
    /** The message box return value is YES (usually sent from a button labeled YES). */
    public static final DialogResult YES = new DialogResult(MessageBoxButtonType.YES);
    /** The message box return value is ABORT (usually sent from a button labeled ABORT). */
    public static final DialogResult ABORT = new DialogResult(MessageBoxButtonType.ABORT);
    /** The message box return value is RETRY (usually sent from a button labeled RETRY). */
    public static final DialogResult RETRY = new DialogResult(MessageBoxButtonType.RETRY);
    /** The message box return value is TRYAGAIN (usually sent from a button labeled TRYAGAIN). */
    public static final DialogResult TRYAGAIN = new DialogResult(MessageBoxButtonType.TRYAGAIN);
    /** The message box return value is IGNORE (usually sent from a button labeled IGNORE). */
    public static final DialogResult IGNORE = new DialogResult(MessageBoxButtonType.IGNORE);
    /** The message box return value is CONTINUE (usually sent from a button labeled CONTINUE). */
    public static final DialogResult CONTINUE = new DialogResult(MessageBoxButtonType.CONTINUE);
    /** The message box return value is SUBMIT (usually sent from a button labeled SUBMIT). */
    public static final DialogResult SUBMIT = new DialogResult(MessageBoxButtonType.SUBMIT);
    /** Nothing is returned from the message box. */
    public static final DialogResult NONE = new DialogResult(null);

    private final ButtonType value;

    public DialogResult() { value = MessageBoxButtonType.NONE; }

    public DialogResult(final ButtonType value) { this.value = value; }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof DialogResult)) return false;

        final var dialogResult = (DialogResult) o;

        return new EqualsBuilder()
                .append(value, dialogResult.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(value)
                .toHashCode();
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object
     */
    @Override
    public String toString() { return value.getText(); }
}
