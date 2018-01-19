package com.jgcomptech.tools.permissions;

import com.jgcomptech.tools.events.EventHandler;
import com.jgcomptech.tools.events.EventManager;
import com.jgcomptech.tools.events.EventTarget;
import com.jgcomptech.tools.events.PermissionEvent;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.util.HashSet;

/**
 * An object representing a permission.
 * @since 1.4.0
 */
public final class Permission extends EventTarget<PermissionEvent> implements Serializable {
    /** The status of the permission. */
    private final BooleanProperty enabled = new SimpleBooleanProperty();
    /** The name of the permission. */
    private final StringProperty name = new SimpleStringProperty();

    private final HashSet<String> childPermissions;

    private final PermissionManager manager = PermissionManager.getInstance();

    private final PermissionEvent eventPermissionEnabled;
    private final PermissionEvent eventPermissionDisabled;

    /**
     * Creates a new permission with the specified name.
     * @param name the name of the new permission
     */
    Permission(final String name) {
        childPermissions = new HashSet<>();
        this.name.set(name);

        String enabledEventName = "permissionEnabled_" + getName();
        String disabledEventName = "permissionDisabled_" + getName();

        try {
            eventPermissionEnabled = EventManager.getInstance().registerNewEvent(
                    enabledEventName,
                    PermissionEvent.class,
                    this,
                    PermissionEvent.PERMISSION_ENABLED);
        } catch(Exception e) {
            throw new IllegalStateException(enabledEventName + " Event Failed To Load!");
        }

        try {
            eventPermissionDisabled = EventManager.getInstance().registerNewEvent(
                    disabledEventName,
                    PermissionEvent.class,
                    this,
                    PermissionEvent.PERMISSION_DISABLED);
        } catch(Exception e) {
            throw new IllegalStateException(disabledEventName + " Event Failed To Load!");
        }

        enabled.addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)) {
                if(newValue) eventPermissionEnabled.fireEvent(this, this);
                else eventPermissionDisabled.fireEvent(this, this);
            }
        });
    }

    /**
     * Returns the name of the permission.
     * @return the name of the permission
     */
    public String getName() { return name.get(); }

    public StringProperty nameProperty() { return name; }

    public BooleanProperty enabledProperty() { return enabled; }

    /**
     * Sets the event handler that will fire when the permission is enabled.
     * @param e the event handler
     */
    public void setOnEnabled(final EventHandler<PermissionEvent> e) {
        if(e == null) removeEventHandler(PermissionEvent.PERMISSION_ENABLED);
        else addEventHandler(PermissionEvent.PERMISSION_ENABLED, e);
    }

    /**
     * Sets the event handler that will fire when the permission is disabled.
     * @param e the event handler
     */
    public void setOnDisabled(final EventHandler<PermissionEvent> e) {
        if(e == null) removeEventHandler(PermissionEvent.PERMISSION_DISABLED);
        else addEventHandler(PermissionEvent.PERMISSION_DISABLED, e);
    }

    /**
     * Returns the event handler that will fire when the permission is disabled.
     * @return the event handler that will fire when the permission is disabled
     */
    public EventHandler<PermissionEvent> getOnEnabled() {
        return getEventHandler(PermissionEvent.PERMISSION_ENABLED);
    }

    /**
     * Returns the event handler that will fire when the permission is disabled.
     * @return the event handler that will fire when the permission is disabled
     */
    public EventHandler<PermissionEvent> getOnDisabled() {
        return getEventHandler(PermissionEvent.PERMISSION_DISABLED);
    }

    /** Enables the permission and all child permissions. */
    public void enable() {
        enabled.set(true);
        for(String name : childPermissions) {
            Permission child = manager.getPermission(name);
            if(child != null) child.enable();
        }
    }

    /** Disables the permission and all child permissions. */
    public void disable() {
        enabled.set(false);
        for(String name : childPermissions) {
            Permission child = manager.getPermission(name);
            if(child != null) child.disable();
        }
    }

    /**
     * Sets the permission status.
     * @param enabled the boolean to set
     */
    public void setEnabled(final boolean enabled) {
        if(enabled) enable();
        else disable();
    }

    /**
     * Returns true if the permission is enabled.
     * @return true if the permission is enabled
     */
    public boolean isEnabled() { return enabled.get(); }

    /**
     * Returns true if the permission is disabled.
     * @return true if the permission is disabled
     */
    public boolean isDisabled() { return !enabled.get(); }

    /**
     * Returns a list of child permissions.
     * @return a list of child permissions
     */
    public HashSet<String> getChildPermissions() { return childPermissions; }

    /**
     * Adds new permission with the specified name, disabled by default.
     * @param name the name of the new permission
     * @return false if permission already exists
     */
    public boolean addNewChildPermission(final String name) {
        if(manager.getPermissions().containsKey(name)) return false;
        if(childPermissions.contains(name)) return false;
        else {
            manager.addCustomPermission(name);
            childPermissions.add(name);
            return true;
        }
    }

    /**
     * Adds existing permission with the specified name as a child permission.
     * @param name the name of the new permission
     * @return false if permission already exists
     */
    public boolean addExistingChildPermission(final String name) {
        if(manager.getPermissions().containsKey(name)
                && !name.equals(getName())
                && !childPermissions.contains(name)) {
            childPermissions.add(name);
            if(isEnabled()) manager.enablePermission(name);
            else manager.disablePermission(name);
            return true;
        } else return false;
    }

    /**
     * Adds new permission with the specified name, enabled by default.
     * @param name the name of the new permission
     * @return false if permission already exists
     */
    public boolean addAndEnableNewChildPermission(final String name) {
        if(manager.getPermissions().containsKey(name)) return false;
        if(childPermissions.contains(name)) return false;
        else {
            manager.addAndEnableCustomPermission(name);
            childPermissions.add(name);
            return true;
        }
    }

    /**
     * Removes the specified permission as a child.
     * @param name the name of the permission to remove
     * @return false if the permission is not a child
     */
    public boolean removeChildPermission(final String name) {
        if(childPermissions.contains(name)) {
            childPermissions.remove(name);
            return true;
        } else return false;
    }

    /**
     * Removes the specified permission as a child.
     * @param name the name of the permission to remove
     * @param permanentlyDelete if true also removes the permission from the PermissionsManager
     * @return false if the permission is not a child or if permanentlyDelete is true and is a System Permission
     */
    public boolean removeChildPermission(final String name, final boolean permanentlyDelete) {
        if(childPermissions.contains(name)) {
            if(permanentlyDelete) {
                return manager.removePermission(name);
            }
            else childPermissions.remove(name);
            return true;
        } else return false;
    }
}
