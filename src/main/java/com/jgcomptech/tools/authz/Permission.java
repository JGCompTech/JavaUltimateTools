package com.jgcomptech.tools.authz;

import com.jgcomptech.tools.events.EventHandler;
import com.jgcomptech.tools.events.EventManager;
import com.jgcomptech.tools.events.EventTarget;
import com.jgcomptech.tools.events.PermissionEvent;
import javafx.beans.property.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

/**
 * An object representing a permission.
 * @since 1.4.0
 * @since 1.5.0 Changed implementation
 */
public final class Permission extends EventTarget<PermissionEvent> implements Serializable {
    /** The status of the permission. */
    private final BooleanProperty enabled = new SimpleBooleanProperty();
    /** The name of the permission. */
    private final StringProperty name = new SimpleStringProperty();

    private final HashSet<String> childPermissions;

    private final PermissionManager manager = PermissionManager.getInstance();

    private final ObjectProperty<PermissionEvent> eventPermissionEnabled = new SimpleObjectProperty<>();
    private final ObjectProperty<PermissionEvent> eventPermissionDisabled = new SimpleObjectProperty<>();

    /**
     * Creates a new permission with the specified name.
     * @param name the name of the new permission
     */
    Permission(final String name, final String parentName) {
        if(parentName == null || parentName.trim().isEmpty() || manager.doesPermissionExist(parentName)) {
            childPermissions = new HashSet<>();

            if(parentName == null || parentName.trim().isEmpty()) this.name.set(name);
            else this.name.set(parentName + ":" + name);

            final var enabledEventName = "permissionEnabled_" + getName();
            final var disabledEventName = "permissionDisabled_" + getName();

            try {
                eventPermissionEnabled.set(EventManager.getInstance().registerNewEvent(
                        enabledEventName,
                        PermissionEvent.class,
                        this,
                        PermissionEvent.PERMISSION_ENABLED));
            } catch(final Exception e) {
                throw new IllegalStateException(enabledEventName + " Event Failed To Load!");
            }

            try {
                eventPermissionDisabled.set(EventManager.getInstance().registerNewEvent(
                        disabledEventName,
                        PermissionEvent.class,
                        this,
                        PermissionEvent.PERMISSION_DISABLED));
            } catch(final Exception e) {
                throw new IllegalStateException(disabledEventName + " Event Failed To Load!");
            }

            enabled.addListener((observable, oldValue, newValue) -> {
                if(!newValue.equals(oldValue)) {
                    if(newValue) eventPermissionEnabled.get().fireEvent(this, this);
                    else eventPermissionDisabled.get().fireEvent(this, this);
                }
            });
        } else throw new AuthorizationException("Parent Permission \"" + parentName + "\" Not Found!");
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

    public ObjectProperty<PermissionEvent> onEnabledProperty() {
        return eventPermissionEnabled;
    }

    /**
     * Returns the event handler that will fire when the permission is disabled.
     * @return the event handler that will fire when the permission is disabled
     */
    public EventHandler<PermissionEvent> getOnDisabled() {
        return getEventHandler(PermissionEvent.PERMISSION_DISABLED);
    }

    public ObjectProperty<PermissionEvent> onDisabledProperty() {
        return eventPermissionDisabled;
    }

    /** Enables the permission and all child permissions. */
    public void enable() {
        enabled.set(true);
        childPermissions.stream()
                .map(manager::getPermission)
                .filter(Objects::nonNull)
                .forEach(Permission::enable);
    }

    /** Disables the permission and all child permissions. */
    public void disable() {
        enabled.set(false);
        childPermissions.stream()
                .map(manager::getPermission)
                .filter(Objects::nonNull)
                .forEach(Permission::disable);
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
     * Checks if the permission has the specified child permission.
     * @param name the name of the permission to check
     * @return true if the permission has the specified child permission
     * @since 1.5.0
     */
    public boolean hasChildPermission(final String name) {
        return childPermissions.contains(name);
    }

    /**
     * Checks if the permission has the specified child permissions.
     * @param names the names of the permissions to check
     * @return true if the permission has the specified child permissions
     * @since 1.5.0
     */
    public boolean hasChildPermissions(final HashSet<String> names) {
        return names.stream().allMatch(this::hasChildPermission);
    }

    /**
     * Adds new permission with the specified name, disabled by default.
     * @param name the name of the new permission
     * @return false if permission already exists
     */
    public boolean addNewChildPermission(final String name) {
        return manager.addCustomPermission(name, this.name.get());
    }

    /**
     * Adds new permission with the specified name, enabled by default.
     * @param name the name of the new permission
     * @return false if permission already exists
     */
    public boolean addAndEnableNewChildPermission(final String name) {
        return manager.addAndEnableCustomPermission(name, this.name.get());
    }

    /**
     * Removes the specified permission as a child.
     * @param name the name of the permission to remove
     * @return false if the permission is not a child
     */
    public boolean removeChildPermission(final String name) {
        return childPermissions.contains(name)
                && manager.removePermission(name)
                && childPermissions.remove(name);
    }

    /**
     * Attempts to inclusively copy a permission to another parent.
     * This method copies all child permissions and all event handlers.
     * @param newParentName the name of the new parent to copy to
     * @return true if copy succeeds
     * @since 1.5.0
     */
    public boolean copyToNewParent(final String newParentName) {
        final var baseName = getBaseName(name.get());
        if(!manager.getPermissions().containsKey(newParentName + ":" + baseName)
        && manager.addCustomPermission(baseName, newParentName)) {
            final var newPermission = manager.getPermission(newParentName + ":" + baseName);
            newPermission.setOnEnabled(getOnEnabled());
            newPermission.setOnDisabled(getOnDisabled());
            copyChildPermissions(newParentName + ":" + baseName,
                    childPermissions, newPermission.childPermissions);
            return true;
        }
        return false;
    }

    private void copyChildPermissions(final String newParentName,
                                      final HashSet<String> oldL,
                                      final HashSet<String> newL) {
        if(!oldL.isEmpty()) {
            for (final var permission : oldL) {
                newL.add(newParentName + ":" + getBaseName(permission));
                manager.addCustomPermission(getBaseName(permission), newParentName);
                final var children1 =
                        manager.getPermissionChildren(permission);
                final var children2 =
                        manager.getPermissionChildren(newParentName + ":" + getBaseName(permission));
                if(children1 != null && !children1.isEmpty()) {
                    copyChildPermissions(newParentName + ":" + getBaseName(permission),
                            children1, children2);
                }
            }
        }
    }

    private String getBaseName(final String name) {
        return name.contains(":") ? name.substring(name.lastIndexOf(":") + 1) : name;
    }

    /**
     * Enables basic event handlers with System.Out logging for all events.
     * @since 1.5.0
     */
    public void enableDebugLogging() {
        setOnEnabled(e -> System.out.println("EVENT: Permission " + e.getPermission().getName() + " Enabled!"));
        setOnDisabled(e -> System.out.println("EVENT: Permission " + e.getPermission().getName() + " Disabled!"));
    }

    /**
     * Disables basic event handlers with System.Out logging for all events.
     * @since 1.5.0
     */
    public void disableDebugLogging() {
        setOnEnabled(null);
        setOnDisabled(null);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Permission)) return false;

        final var permission = (Permission) o;

        return new EqualsBuilder()
                .append(isEnabled(), permission.isEnabled())
                .append(getName(), permission.getName())
                .append(childPermissions, permission.childPermissions)
                .append(manager, permission.manager)
                .append(eventPermissionEnabled, permission.eventPermissionEnabled)
                .append(eventPermissionDisabled, permission.eventPermissionDisabled)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(isEnabled())
                .append(getName())
                .append(childPermissions)
                .append(manager)
                .append(eventPermissionEnabled)
                .append(eventPermissionDisabled)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("enabled", enabled)
                .append("name", name)
                .toString();
    }
}
