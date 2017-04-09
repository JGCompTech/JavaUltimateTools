package com.jgcomptech.tools.permissions;

import java.io.Serializable;

/** The base object for all permissions */
public class Permission implements Serializable {
    private boolean enabled = false;

    /** Enables the permission */
    public void enable() { this.enabled = true; }

    /** Disables the permission */
    public void disable() { this.enabled = false; }

    /**
     * Sets the permission status
     * @param enabled the boolean to set
     */
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    /**
     * Returns true if the permission is enabled
     * @return true if the permission is enabled
     */
    public boolean isEnabled() { return enabled; }

    /**
     * Returns true if the permission is disabled
     * @return true if the permission is disabled
     */
    public boolean isDisabled() { return !enabled; }

    Permission() { /*Exists only to defeat instantiation outside of package.*/ }
}
