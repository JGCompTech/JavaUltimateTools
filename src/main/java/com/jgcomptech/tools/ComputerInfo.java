package com.jgcomptech.tools;

/** Returns Information about the current OS and Hardware on the current system */
public class ComputerInfo {
    public OSInfo.OSObject OS;
    public HWInfo.HWObject HW;

    public ComputerInfo() {
        HW = ReinitalizeHW();
        OS = ReinitalizeOS();
    }

    /* Reprocesses the OS information and returns a new OSObject */
    public static OSInfo.OSObject ReinitalizeOS() {
        OSInfo.VersionObject vobj = new OSInfo.VersionObject();
        vobj.Build = OSInfo.Windows.Version.Build();
        vobj.Main = OSInfo.Windows.Version.Main();
        vobj.Major = OSInfo.Windows.Version.Major();
        vobj.Minor = OSInfo.Windows.Version.Minor();
        vobj.Number = OSInfo.Windows.Version.Number();
        vobj.Revision = OSInfo.Windows.Version.Revision();

        OSInfo.InstallInfoObject iiobj = new OSInfo.InstallInfoObject();
        iiobj.ActivationStatus = OSInfo.Windows.Activation.getStatusString();
        iiobj.Architecture = OSInfo.Architecture.String();
        iiobj.NameExpanded = OSInfo.Name.StringExpanded();
        iiobj.NameExpandedFromRegistry = OSInfo.Name.StringExpandedFromRegistry();
        iiobj.Name = OSInfo.Name.String();
        iiobj.ServicePack = OSInfo.Windows.ServicePack.String();
        iiobj.ServicePackNumber = OSInfo.Windows.ServicePack.Number();
        iiobj.Version = vobj;

        OSInfo.OSObject osobj = new OSInfo.OSObject();
        osobj.ComputerName = OSInfo.Name.ComputerNameActive();
        osobj.ComputerNamePending = OSInfo.Name.ComputerNamePending();
        osobj.DomainName = OSInfo.Windows.UserInfo.CurrentDomainName();
        osobj.LoggedInUserName = OSInfo.Windows.UserInfo.LoggedInUserName();
        osobj.RegisteredOrganization = OSInfo.Windows.UserInfo.RegisteredOrganization();
        osobj.RegisteredOwner = OSInfo.Windows.UserInfo.RegisteredOwner();
        osobj.InstallInfo = iiobj;

        return osobj;
    }

    /* Reprocesses the Hardware information and returns a new HWObject */
    private HWInfo.HWObject ReinitalizeHW() {
        HWInfo.BIOSObject biosobj = new HWInfo.BIOSObject();
        biosobj.Name = HWInfo.BIOS.getVendor() + " " + HWInfo.BIOS.getVersion();
        biosobj.ReleaseDate = HWInfo.BIOS.getReleaseDate();
        biosobj.Vendor = HWInfo.BIOS.getVendor();
        biosobj.Version = HWInfo.BIOS.getVersion();

        HWInfo.NetworkObject nwobj = new HWInfo.NetworkObject();
        nwobj.ConnectionStatus = HWInfo.Network.isConnectedToInternet();
        nwobj.InternalIPAddress = HWInfo.Network.getInternalIPAddress();
        nwobj.ExternalIPAddress = HWInfo.Network.getExternalIPAddress();

        HWInfo.ProcessorObject pobj = new HWInfo.ProcessorObject();
        pobj.Name = HWInfo.Processor.Name();
        pobj.Cores = HWInfo.Processor.Cores();

        HWInfo.RAMObject robj = new HWInfo.RAMObject();
        robj.TotalInstalled = HWInfo.RAM.GetTotalRam();

        HWInfo.StorageObject sobj = new HWInfo.StorageObject();
        sobj.SystemDrive = new HWInfo.DriveObject();
        sobj.SystemDrive.DriveType = "Fixed";
        sobj.SystemDrive.TotalFree = HWInfo.Storage.getSystemDriveFreeSpace();
        sobj.SystemDrive.TotalSize = HWInfo.Storage.getSystemDriveSize();

        HWInfo.HWObject hwobj = new HWInfo.HWObject();
        hwobj.SystemOEM = HWInfo.OEM.Name();
        hwobj.ProductName = HWInfo.OEM.ProductName();
        hwobj.BIOS = biosobj;
        hwobj.Network = nwobj;
        hwobj.Processor = pobj;
        hwobj.RAM = robj;
        hwobj.Storage = sobj;

        return hwobj;
    }
}