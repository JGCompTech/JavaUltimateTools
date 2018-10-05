package com.jgcomptech.tools;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;

/**
 * Returns Information about the current OS and Hardware on the current system.
 * @since 1.2.0
 */
public final class ComputerInfo {
    public OSInfo.OSObject OS;
    public HWInfo.HWObject HW;

    public ComputerInfo() throws IOException, InterruptedException {
        HW = ReInitializeHW();
        OS = ReInitializeOS();
    }

    /* Reprocesses the OS information and returns a new OSObject. */
    public static OSInfo.OSObject ReInitializeOS() throws IOException, InterruptedException {
        final var vobj = new OSInfo.VersionObject();
        vobj.Build = OSInfo.Windows.Version.Build();
        vobj.Main = OSInfo.Windows.Version.Main();
        vobj.Major = OSInfo.Windows.Version.Major();
        vobj.Minor = OSInfo.Windows.Version.Minor();
        vobj.Number = OSInfo.Windows.Version.Number();
        vobj.Revision = OSInfo.Windows.Version.Revision();

        final var iiobj = new OSInfo.InstallInfoObject();
        iiobj.ActivationStatus = OSInfo.Windows.Activation.getStatusString();
        iiobj.Architecture = OSInfo.Architecture.String();
        iiobj.NameExpanded = OSInfo.Name.StringExpanded();
        iiobj.NameExpandedFromRegistry = OSInfo.Name.StringExpandedFromRegistry();
        iiobj.Name = OSInfo.Name.String();
        iiobj.ServicePack = OSInfo.Windows.ServicePack.String();
        iiobj.ServicePackNumber = OSInfo.Windows.ServicePack.Number();
        iiobj.Version = vobj;

        final var osobj = new OSInfo.OSObject();
        osobj.ComputerName = OSInfo.Name.ComputerNameActive();
        osobj.ComputerNamePending = OSInfo.Name.ComputerNamePending();
        osobj.DomainName = OSInfo.Windows.UserInfo.CurrentDomainName();
        osobj.LoggedInUserName = OSInfo.Windows.UserInfo.LoggedInUserName();
        osobj.RegisteredOrganization = OSInfo.Windows.UserInfo.RegisteredOrganization();
        osobj.RegisteredOwner = OSInfo.Windows.UserInfo.RegisteredOwner();
        osobj.InstallInfo = iiobj;

        return osobj;
    }

    /* Reprocesses the Hardware information and returns a new HWObject. */
    public static HWInfo.HWObject ReInitializeHW() throws IOException {
        final var biosobj = new HWInfo.BIOSObject();
        biosobj.Name = HWInfo.BIOS.getVendor() + ' ' + HWInfo.BIOS.getVersion();
        biosobj.ReleaseDate = HWInfo.BIOS.getReleaseDate();
        biosobj.Vendor = HWInfo.BIOS.getVendor();
        biosobj.Version = HWInfo.BIOS.getVersion();

        final var nwobj = new HWInfo.NetworkObject();
        nwobj.ConnectionStatus = HWInfo.Network.isConnectedToInternet();
        nwobj.InternalIPAddress = HWInfo.Network.getInternalIPAddress();
        nwobj.ExternalIPAddress = HWInfo.Network.getExternalIPAddress();

        final var pobj = new HWInfo.ProcessorObject();
        pobj.Name = HWInfo.Processor.Name();
        pobj.Cores = HWInfo.Processor.Cores();

        final var robj = new HWInfo.RAMObject();
        robj.TotalInstalled = HWInfo.RAM.getTotalRam();

        final var sobj = new HWInfo.StorageObject();
        sobj.SystemDrive = new HWInfo.DriveObject();
        sobj.SystemDrive.DriveType = "Fixed";
        sobj.SystemDrive.TotalFree = HWInfo.Storage.getSystemDriveFreeSpace();
        sobj.SystemDrive.TotalSize = HWInfo.Storage.getSystemDriveSize();

        final var hwobj = new HWInfo.HWObject();
        hwobj.SystemOEM = HWInfo.OEM.Name();
        hwobj.ProductName = HWInfo.OEM.ProductName();
        hwobj.BIOS = biosobj;
        hwobj.Network = nwobj;
        hwobj.Processor = pobj;
        hwobj.RAM = robj;
        hwobj.Storage = sobj;

        return hwobj;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof ComputerInfo)) return false;

        final var computerInfo = (ComputerInfo) o;

        return new EqualsBuilder()
                .append(OS, computerInfo.OS)
                .append(HW, computerInfo.HW)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(OS)
                .append(HW)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("OS", OS)
                .append("HW", HW)
                .toString();
    }
}
