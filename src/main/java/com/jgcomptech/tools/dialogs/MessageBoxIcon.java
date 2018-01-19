package com.jgcomptech.tools.dialogs;

/**
 * Used to specify which icon is displayed on a message box.
 * @since 1.3.0
 */
public enum MessageBoxIcon {
    /**
     * The NONE alert type has the effect of not setting any default properties
     * in the MessageBox.
     */
    NONE,

    /**
     * The INFORMATION alert type configures the MessageBox to appear in a
     * way that suggests the content of the MessageBox is informing the user of
     * a piece of information. This includes an 'information' image, an
     * appropriate title and header, and just an OK button for the user to
     * click on to dismiss the MessageBox.
     */
    INFORMATION,

    /**
     * The WARNING alert type configures the MessageBox to appear in a
     * way that suggests the content of the MessageBox is warning the user about
     * some fact or action. This includes a 'warning' image, an
     * appropriate title and header, and just an OK button for the user to
     * click on to dismiss the MessageBox.
     */
    WARNING,

    /**
     * The CONFIRMATION alert type configures the MessageBox to appear in a
     * way that suggests the content of the MessageBox is seeking confirmation from
     * the user. This includes a 'confirmation' image, an
     * appropriate title and header, and both OK and Cancel buttons for the
     * user to click on to dismiss the MessageBox.
     */
    CONFIRMATION,

    /**
     * The ERROR alert type configures the MessageBox to appear in a
     * way that suggests that something has gone wrong. This includes an
     * 'error' image, an appropriate title and header, and just an OK button
     * for the user to click on to dismiss the MessageBox.
     */
    ERROR
}
