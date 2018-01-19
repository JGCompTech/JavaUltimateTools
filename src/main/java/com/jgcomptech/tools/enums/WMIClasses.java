package com.jgcomptech.tools.enums;

public enum WMIClasses {
    Win32_WMISetting("Win32_WMISetting"),
    Win32_WMIElementSetting("Win32_WMIElementSetting");

    private final String value;

    WMIClasses(final String text) { value = text; }

    @Override
    public String toString() { return value; }

    public enum COM {
        ClassicCOMApplicationClasses("Win32_ClassicCOMApplicationClasses"),
        ClassicCOMClass("Win32_ClassicCOMClass"),
        ClassicCOMClassSettings("Win32_ClassicCOMClassSettings"),
        ClientApplicationSetting("Win32_ClientApplicationSetting"),
        COMApplication("Win32_COMApplication"),
        COMApplicationClasses("Win32_COMApplicationClasses"),
        COMApplicationSettings("Win32_COMApplicationSettings"),
        COMClass("Win32_COMClass"),
        ComClassAutoEmulator("Win32_COMClassAutoEmulator"),
        ComponentCategory("Win32_ComponentCategory"),
        COMSetting("Win32_COMSetting"),
        DCOMApplication("Win32_DCOMApplication"),
        DCOMApplicationAccessAllowedSetting("Win32_DCOMApplicationAccessAllowedSetting"),
        DCOMApplicationLaunchAllowedSetting("Win32_DCOMApplicationLaunchAllowedSetting"),
        DCOMApplicationSetting("Win32_DCOMApplicationSetting"),
        ImplementedCategory("Win32_ImplementedCategory");

        private final String value;

        COM(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum File_System {
        CIMLogicalDeviceCIMDataFile("Win32_CIMLogicalDeviceCIMDataFile"),
        Directory("Win32_Directory"),
        DirectorySpecification("Win32_DirectorySpecification"),
        DiskDriveToDiskPartition("Win32_DiskDriveToDiskPartition"),
        DiskPartition("Win32_DiskPartition"),
        DiskQuota("Win32_DiskQuota"),
        LogicalDisk("Win32_LogicalDisk"),
        LogicalDiskRootDirectory("Win32_LogicalDiskRootDirectory"),
        LogicalDiskToPartition("Win32_LogicalDiskToPartition"),
        MappedLogicalDisk("Win32_MappedLogicalDisk"),
        OperatingSystemAutochkSetting("Win32_OperatingSystemAutochkSetting"),
        QuotaSetting("Win32_QuotaSetting"),
        ShortcutFile("Win32_ShortcutFile"),
        SubDirectory("Win32_SubDirectory"),
        SystemPartitions("Win32_SystemPartitions"),
        Volume("Win32_Volume"),
        VolumeQuota("Win32_VolumeQuota"),
        VolumeQuotaSetting("Win32_VolumeQuotaSetting"),
        VolumeUserQuota("Win32_VolumeUserQuota");

        private final String value;

        File_System(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum Hardware {
        _1394Controller("Win32_1394Controller"),
        _1394ControllerDevice("Win32_1394ControllerDevice"),
        AllocatedResource("Win32_AllocatedResource"),
        AssociatedProcessorMemory("Win32_AssociatedProcessorMemory"),
        AutochkSetting("Win32_AutochkSetting"),
        BaseBoard("Win32_BaseBoard"),
        Battery("Win32_Battery"),
        BIOS("Win32_BIOS"),
        Bus("Win32_Bus"),
        CacheMemory("Win32_CacheMemory"),
        CDROMDrive("Win32_CDROMDrive"),
        ControllerHastHub("Win32_ControllerHastHub"),
        CurrentProbe("Win32_CurrentProbe"),
        DesktopMonitor("Win32_DesktopMonitor"),
        DeviceBus("Win32_DeviceBus"),
        DeviceMemoryAddress("Win32_DeviceMemoryAddress"),
        DeviceSettings("Win32_DeviceSettings"),
        DiskDrive("Win32_DiskDrive"),
        DisplayControllerConfiguration("Win32_DisplayControllerConfiguration"),
        DMAChannel("Win32_DMAChannel"),
        DriverForDevice("Win32_DriverForDevice"),
        Fan("Win32_Fan"),
        FloppyDrive("Win32_FloppyDrive"),
        FloppyController("Win32_FloppyController"),
        HeatPipe("Win32_HeatPipe"),
        IDEController("Win32_IDEController"),
        IDEControllerDevice("Win32_IDEControllerDevice"),
        InfraredDevice("Win32_InfraredDevice"),
        IRQResource("Win32_IRQResource"),
        Keyboard("Win32_Keyboard"),
        MemoryArray("Win32_MemoryArray"),
        MemoryArrayLocation("Win32_MemoryArrayLocation"),
        MemoryDevice("Win32_MemoryDevice"),
        MemoryDeviceArray("Win32_MemoryDeviceArray"),
        MemoryDeviceLocation("Win32_MemoryDeviceLocation"),
        MotherboardDevice("Win32_MotherboardDevice"),
        NetworkAdapter("Win32_NetworkAdapter"),
        NetworkAdapterConfiguration("Win32_NetworkAdapterConfiguration"),
        NetworkAdapterSetting("Win32_NetworkAdapterSetting"),
        OnBoardDevice("Win32_OnBoardDevice"),
        ParallelPort("Win32_ParallelPort"),
        PCMCIAController("Win32_PCMCIAController"),
        PhysicalMedia("Win32_PhysicalMedia"),
        PhysicalMemory("Win32_PhysicalMemory"),
        PhysicalMemoryArray("Win32_PhysicalMemoryArray"),
        PhysicalMemoryLocation("Win32_PhysicalMemoryLocation"),
        PNPAllocatedResource("Win32_PNPAllocatedResource"),
        PnPDevice("Win32_PnPDevice"),
        PnPEntity("Win32_PnPEntity"),
        PointingDevice("Win32_PointingDevice"),
        PortConnector("Win32_PortConnector"),
        PortableBattery("Win32_PortableBattery"),
        PortResource("Win32_PortResource"),
        POTSModem("Win32_POTSModem"),
        POTSModemToSerialPort("Win32_POTSModemToSerialPort"),
        PowerManagementEvent("Win32_PowerManagementEvent"),
        Printer("Win32_Printer"),
        PrinterConfiguration("Win32_PrinterConfiguration"),
        PrinterController("Win32_PrinterController"),
        PrinterDriver("Win32_PrinterDriver"),
        PrinterDriverDll("Win32_PrinterDriverDll"),
        PrinterSetting("Win32_PrinterSetting"),
        PrintJob("Win32_PrintJob"),
        Processor("Win32_Processor"),
        Refrigeration("Win32_Refrigeration"),
        SCSIController("Win32_SCSIController"),
        SCSIControllerDevice("Win32_SCSIControllerDevice"),
        SerialPort("Win32_SerialPort"),
        SerialPortConfiguration("Win32_SerialPortConfiguration"),
        SerialPortSetting("Win32_SerialPortSetting"),
        SMBIOSMemory("Win32_SMBIOSMemory"),
        SoundDevice("Win32_SoundDevice"),
        SystemBIOS("Win32_SystemBIOS"),
        SystemDriverPNPEntity("Win32_SystemDriverPNPEntity"),
        SystemEnclosure("Win32_SystemEnclosure"),
        SystemMemoryResource("Win32_SystemMemoryResource"),
        SystemSlot("Win32_SystemSlot"),
        TapeDrive("Win32_TapeDrive"),
        TCPIPPrinterPort("Win32_TCPIPPrinterPort"),
        TemperatureProbe("Win32_TemperatureProbe"),
        USBController("Win32_USBController"),
        USBControllerDevice("Win32_USBControllerDevice"),
        USBHub("Win32_USBHub"),
        VideoController("Win32_VideoController"),
        VideoSettings("Win32_VideoSettings"),
        VoltageProbe("Win32_VoltageProbe");

        private final String value;

        Hardware(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum Networking {
        ActiveRoute("Win32_ActiveRoute"),
        IP4PersistedRouteTable("Win32_IP4PersistedRouteTable"),
        IP4RouteTable("Win32_IP4RouteTable"),
        IP4RouteTableEvent("Win32_IP4RouteTableEvent"),
        NetworkClient("Win32_NetworkClient"),
        NetworkConnection("Win32_NetworkConnection"),
        NetworkProtocol("Win32_NetworkProtocol"),
        NTDomain("Win32_NTDomain"),
        PingStatus("Win32_PingStatus"),
        ProtocolBinding("Win32_ProtocolBinding");

        private final String value;

        Networking(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum OS {
        BootConfiguration("Win32_BootConfiguration"),
        ComputerSystem("Win32_ComputerSystem"),
        ComputerSystemProcessor("Win32_ComputerSystemProcessor"),
        ComputerSystemProduct("Win32_ComputerSystemProduct"),
        DependentService("Win32_DependentService"),
        LoadOrderGroup("Win32_LoadOrderGroup"),
        LoadOrderGroupServiceDependencies("Win32_LoadOrderGroupServiceDependencies"),
        LoadOrderGroupServiceMembers("Win32_LoadOrderGroupServiceMembers"),
        OperatingSystem("Win32_OperatingSystem"),
        OperatingSystemQFE("Win32_OperatingSystemQFE"),
        OSRecoveryConfiguration("Win32_OSRecoveryConfiguration"),
        QuickFixEngineering("Win32_QuickFixEngineering"),
        StartupCommand("Win32_StartupCommand"),
        SystemBootConfiguration("Win32_SystemBootConfiguration"),
        SystemDesktop("Win32_SystemDesktop"),
        SystemDevices("Win32_SystemDevices"),
        SystemLoadOrderGroups("Win32_SystemLoadOrderGroups"),
        SystemNetworkConnections("Win32_SystemNetworkConnections"),
        SystemOperatingSystem("Win32_SystemOperatingSystem"),
        SystemProcesses("Win32_SystemProcesses"),
        SystemProgramGroups("Win32_SystemProgramGroups"),
        SystemResources("Win32_SystemResources"),
        SystemServices("Win32_SystemServices"),
        SystemSetting("Win32_SystemSetting"),
        SystemSystemDriver("Win32_SystemSystemDriver"),
        SystemTimeZone("Win32_SystemTimeZone"),
        SystemUsers("Win32_SystemUsers");

        private final String value;

        OS(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum Other {
        BaseService("Win32_BaseService"),
        CIMLogicalDeviceCIMDataFile("Win32_CIMLogicalDeviceCIMDataFile"),
        CodecFile("Win32_CodecFile"),
        CollectionStatistics("Win32_CollectionStatistics"),
        ComputerSystemWindowsProductActivationSetting("Win32_ComputerSystemWindowsProductActivationSetting"),
        CurrentTime("Win32_CurrentTime"),
        Desktop("Win32_Desktop"),
        Directory("Win32_Directory"),
        DirectorySpecification("Win32_DirectorySpecification"),
        DiskDriveToDiskPartition("Win32_DiskDriveToDiskPartition"),
        DiskPartition("Win32_DiskPartition"),
        DiskQuota("Win32_DiskQuota"),
        Environment("Win32_Environment"),
        LocalTime("Win32_LocalTime"),
        LogicalDisk("Win32_LogicalDisk"),
        LogicalDiskRootDirectory("Win32_LogicalDiskRootDirectory"),
        LogicalDiskToPartition("Win32_LogicalDiskToPartition"),
        LogicalProgramGroup("Win32_LogicalProgramGroup"),
        LogicalProgramGroupDirectory("Win32_LogicalProgramGroupDirectory"),
        LogicalProgramGroupItem("Win32_LogicalProgramGroupItem"),
        LogicalProgramGroupItemDataFile("Win32_LogicalProgramGroupItemDataFile"),
        LUID("Win32_LUID"),
        LUIDandAttributes("Win32_LUIDandAttributes"),
        MappedLogicalDisk("Win32_MappedLogicalDisk"),
        NamedJobObject("Win32_NamedJobObject"),
        NamedJobObjectActgInfo("Win32_NamedJobObjectActgInfo"),
        NamedJobObjectLimit("Win32_NamedJobObjectLimit"),
        NamedJobObjectLimitSetting("Win32_NamedJobObjectLimitSetting"),
        NamedJobObjectProcess("Win32_NamedJobObjectProcess"),
        NamedJobObjectSecLimit("Win32_NamedJobObjectSecLimit"),
        NamedJobObjectSecLimitSetting("Win32_NamedJobObjectSecLimitSetting"),
        NamedJobObjectStatistics("Win32_NamedJobObjectStatistics"),
        NTEventlogFile("Win32_NTEventlogFile"),
        NTLogEvent("Win32_NTLogEvent"),
        NTLogEventComputer("Win32_NTLogEventComputer"),
        NTLogEventLog("Win32_NTLogEventLog"),
        NTLogEventUser("Win32_NTLogEventUser"),
        PageFile("Win32_PageFile"),
        PageFileElementSetting("Win32_PageFileElementSetting"),
        PageFileSetting("Win32_PageFileSetting"),
        PageFileUsage("Win32_PageFileUsage"),
        Process("Win32_Process"),
        ProcessStartup("Win32_ProcessStartup"),
        ProgramGroupContents("Win32_ProgramGroupContents"),
        ProgramGroupOrItem("Win32_ProgramGroupOrItem"),
        Proxy("Win32_Proxy"),
        Service("Win32_Service"),
        Thread("Win32_Thread"),
        QuotaSetting("Win32_QuotaSetting"),
        Registry("Win32_Registry"),
        ScheduledJob("Win32_ScheduledJob"),
        ShortcutFile("Win32_ShortcutFile"),
        SIDandAttributes("Win32_SIDandAttributes"),
        SubDirectory("Win32_SubDirectory"),
        SystemDriver("Win32_SystemDriver"),
        SystemPartitions("Win32_SystemPartitions"),
        TimeZone("Win32_TimeZone"),
        TokenGroups("Win32_TokenGroups"),
        TokenPrivileges("Win32_TokenPrivileges"),
        UserDesktop("Win32_UserDesktop"),
        UTCTime("Win32_UTCTime"),
        VolumeQuota("Win32_VolumeQuota"),
        VolumeQuotaSetting("Win32_VolumeQuotaSetting"),
        VolumeUserQuota("Win32_VolumeUserQuota"),
        WindowsProductActivation("Win32_WindowsProductActivation");

        private final String value;

        Other(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum Performance_FormattedData {
        ASP_ActiveServerPages("Win32_PerfFormattedData_ASP_ActiveServerPages"),
        ASPNET_114322_ASPNETAppsv114322("Win32_PerfFormattedData_ASPNET_114322_ASPNETAppsv114322"),
        ASPNET_114322_ASPNETv114322("Win32_PerfFormattedData_ASPNET_114322_ASPNETv114322"),
        ASPNET_2040607_ASPNETAppsv2040607("Win32_PerfFormattedData_ASPNET_2040607_ASPNETAppsv2040607"),
        ASPNET_2040607_ASPNETv2040607("Win32_PerfFormattedData_ASPNET_2040607_ASPNETv2040607"),
        ASPNET_ASPNET("Win32_PerfFormattedData_ASPNET_ASPNET"),
        ASPNET_ASPNETApplications("Win32_PerfFormattedData_ASPNET_ASPNETApplications"),
        aspnet_state_ASPNETStateService("Win32_PerfFormattedData_aspnet_state_ASPNETStateService"),
        ContentFilter_IndexingServiceFilter("Win32_PerfFormattedData_ContentFilter_IndexingServiceFilter"),
        ContentIndex_IndexingService("Win32_PerfFormattedData_ContentIndex_IndexingService"),
        DTSPipeline_SQLServerDTSPipeline("Win32_PerfFormattedData_DTSPipeline_SQLServerDTSPipeline"),
        Fax_FaxServices("Win32_PerfFormattedData_Fax_FaxServices"),
        InetInfo_InternetInformationServicesGlobal(
                "Win32_PerfFormattedData_InetInfo_InternetInformationServicesGlobal"),
        ISAPISearch_HttpIndexingService("Win32_PerfFormattedData_ISAPISearch_HttpIndexingService"),
        MSDTC_DistributedTransactionCoordinator("Win32_PerfFormattedData_MSDTC_DistributedTransactionCoordinator"),
        NETCLRData_NETCLRData("Win32_PerfFormattedData_NETCLRData_NETCLRData"),
        NETCLRNetworking_NETCLRNetworking("Win32_PerfFormattedData_NETCLRNetworking_NETCLRNetworking"),
        NETDataProviderforOracle_NETCLRData("Win32_PerfFormattedData_NETDataProviderforOracle_NETCLRData"),
        NETDataProviderforSqlServer_NETDataProviderforSqlServer(
                "Win32_PerfFormattedData_NETDataProviderforSqlServer_NETDataProviderforSqlServer"),
        NETFramework_NETCLRExceptions("Win32_PerfFormattedData_NETFramework_NETCLRExceptions"),
        NETFramework_NETCLRInterop("Win32_PerfFormattedData_NETFramework_NETCLRInterop"),
        NETFramework_NETCLRJit("Win32_PerfFormattedData_NETFramework_NETCLRJit"),
        NETFramework_NETCLRLoading("Win32_PerfFormattedData_NETFramework_NETCLRLoading"),
        NETFramework_NETCLRLocksAndThreads("Win32_PerfFormattedData_NETFramework_NETCLRLocksAndThreads"),
        NETFramework_NETCLRMemory("Win32_PerfFormattedData_NETFramework_NETCLRMemory"),
        NETFramework_NETCLRRemoting("Win32_PerfFormattedData_NETFramework_NETCLRRemoting"),
        NETFramework_NETCLRSecurity("Win32_PerfFormattedData_NETFramework_NETCLRSecurity"),
        NTFSDRV_ControladordealmacenamientoNTFSdeSMTP(
                "Win32_PerfFormattedData_NTFSDRV_ControladordealmacenamientoNTFSdeSMTP"),
        Outlook_Outlook("Win32_PerfFormattedData_Outlook_Outlook"),
        PerfDisk_LogicalDisk("Win32_PerfFormattedData_PerfDisk_LogicalDisk"),
        PerfDisk_PhysicalDisk("Win32_PerfFormattedData_PerfDisk_PhysicalDisk"),
        PerfNet_Browser("Win32_PerfFormattedData_PerfNet_Browser"),
        PerfNet_Redirector("Win32_PerfFormattedData_PerfNet_Redirector"),
        PerfNet_Server("Win32_PerfFormattedData_PerfNet_Server"),
        PerfNet_ServerWorkQueues("Win32_PerfFormattedData_PerfNet_ServerWorkQueues"),
        PerfOS_Cache("Win32_PerfFormattedData_PerfOS_Cache"),
        PerfOS_Memory("Win32_PerfFormattedData_PerfOS_Memory"),
        PerfOS_Objects("Win32_PerfFormattedData_PerfOS_Objects"),
        PerfOS_PagingFile("Win32_PerfFormattedData_PerfOS_PagingFile"),
        PerfOS_Processor("Win32_PerfFormattedData_PerfOS_Processor"),
        PerfOS_System("Win32_PerfFormattedData_PerfOS_System"),
        PerfProc_FullImage_Costly("Win32_PerfFormattedData_PerfProc_FullImage_Costly"),
        PerfProc_Image_Costly("Win32_PerfFormattedData_PerfProc_Image_Costly"),
        PerfProc_JobObject("Win32_PerfFormattedData_PerfProc_JobObject"),
        PerfProc_JobObjectDetails("Win32_PerfFormattedData_PerfProc_JobObjectDetails"),
        PerfProc_Process("Win32_PerfFormattedData_PerfProc_Process"),
        PerfProc_ProcessAddressSpace_Costly("Win32_PerfFormattedData_PerfProc_ProcessAddressSpace_Costly"),
        PerfProc_Thread("Win32_PerfFormattedData_PerfProc_Thread"),
        PerfProc_ThreadDetails_Costly("Win32_PerfFormattedData_PerfProc_ThreadDetails_Costly"),
        RemoteAccess_RASPort("Win32_PerfFormattedData_RemoteAccess_RASPort"),
        RemoteAccess_RASTotal("Win32_PerfFormattedData_RemoteAccess_RASTotal"),
        RSVP_RSVPInterfaces("Win32_PerfFormattedData_RSVP_RSVPInterfaces"),
        RSVP_RSVPService("Win32_PerfFormattedData_RSVP_RSVPService"),
        Spooler_PrintQueue("Win32_PerfFormattedData_Spooler_PrintQueue"),
        TapiSrv_Telephony("Win32_PerfFormattedData_TapiSrv_Telephony"),
        Tcpip_ICMP("Win32_PerfFormattedData_Tcpip_ICMP"),
        Tcpip_IP("Win32_PerfFormattedData_Tcpip_IP"),
        Tcpip_NBTConnection("Win32_PerfFormattedData_Tcpip_NBTConnection"),
        Tcpip_NetworkInterface("Win32_PerfFormattedData_Tcpip_NetworkInterface"),
        Tcpip_TCP("Win32_PerfFormattedData_Tcpip_TCP"),
        Tcpip_UDP("Win32_PerfFormattedData_Tcpip_UDP"),
        TermService_TerminalServices("Win32_PerfFormattedData_TermService_TerminalServices"),
        TermService_TerminalServicesSession("Win32_PerfFormattedData_TermService_TerminalServicesSession"),
        W3SVC_WebService("Win32_PerfFormattedData_W3SVC_WebService");

        private final String value;

        Performance_FormattedData(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum Performance_RawData {
        PerfRawData_ASP_ActiveServerPages("Win32_PerfRawData_ASP_ActiveServerPages"),
        PerfRawData_ASPNET_114322_ASPNETAppsv114322("Win32_PerfRawData_ASPNET_114322_ASPNETAppsv114322"),
        PerfRawData_ASPNET_114322_ASPNETv114322("Win32_PerfRawData_ASPNET_114322_ASPNETv114322"),
        PerfRawData_ASPNET_2040607_ASPNETAppsv2040607("Win32_PerfRawData_ASPNET_2040607_ASPNETAppsv2040607"),
        PerfRawData_ASPNET_2040607_ASPNETv2040607("Win32_PerfRawData_ASPNET_2040607_ASPNETv2040607"),
        PerfRawData_ASPNET_ASPNET("Win32_PerfRawData_ASPNET_ASPNET"),
        PerfRawData_ASPNET_ASPNETApplications("Win32_PerfRawData_ASPNET_ASPNETApplications"),
        PerfRawData_aspnet_state_ASPNETStateService("Win32_PerfRawData_aspnet_state_ASPNETStateService"),
        PerfRawData_ContentFilter_IndexingServiceFilter("Win32_PerfRawData_ContentFilter_IndexingServiceFilter"),
        PerfRawData_ContentIndex_IndexingService("Win32_PerfRawData_ContentIndex_IndexingService"),
        PerfRawData_DTSPipeline_SQLServerDTSPipeline("Win32_PerfRawData_DTSPipeline_SQLServerDTSPipeline"),
        PerfRawData_Fax_FaxServices("Win32_PerfRawData_Fax_FaxServices"),
        PerfRawData_InetInfo_InternetInformationServicesGlobal(
                "Win32_PerfRawData_InetInfo_InternetInformationServicesGlobal"),
        PerfRawData_ISAPISearch_HttpIndexingService("Win32_PerfRawData_ISAPISearch_HttpIndexingService"),
        PerfRawData_MSDTC_DistributedTransactionCoordinator(
                "Win32_PerfRawData_MSDTC_DistributedTransactionCoordinator"),
        PerfRawData_NETCLRData_NETCLRData("Win32_PerfRawData_NETCLRData_NETCLRData"),
        PerfRawData_NETCLRNetworking_NETCLRNetworking("Win32_PerfRawData_NETCLRNetworking_NETCLRNetworking"),
        PerfRawData_NETDataProviderforOracle_NETCLRData("Win32_PerfRawData_NETDataProviderforOracle_NETCLRData"),
        PerfRawData_NETDataProviderforSqlServer_NETDataProviderforSqlServer(
                "Win32_PerfRawData_NETDataProviderforSqlServer_NETDataProviderforSqlServer"),
        PerfRawData_NETFramework_NETCLRExceptions("Win32_PerfRawData_NETFramework_NETCLRExceptions"),
        PerfRawData_NETFramework_NETCLRInterop("Win32_PerfRawData_NETFramework_NETCLRInterop"),
        PerfRawData_NETFramework_NETCLRJit("Win32_PerfRawData_NETFramework_NETCLRJit"),
        PerfRawData_NETFramework_NETCLRLoading("Win32_PerfRawData_NETFramework_NETCLRLoading"),
        PerfRawData_NETFramework_NETCLRLocksAndThreads("Win32_PerfRawData_NETFramework_NETCLRLocksAndThreads"),
        PerfRawData_NETFramework_NETCLRMemory("Win32_PerfRawData_NETFramework_NETCLRMemory"),
        PerfRawData_NETFramework_NETCLRRemoting("Win32_PerfRawData_NETFramework_NETCLRRemoting"),
        PerfRawData_NETFramework_NETCLRSecurity("Win32_PerfRawData_NETFramework_NETCLRSecurity"),
        PerfRawData_NTFSDRV_ControladordealmacenamientoNTFSdeSMTP(
                "Win32_PerfRawData_NTFSDRV_ControladordealmacenamientoNTFSdeSMTP"),
        PerfRawData_Outlook_Outlook("Win32_PerfRawData_Outlook_Outlook"),
        PerfRawData_PerfDisk_LogicalDisk("Win32_PerfRawData_PerfDisk_LogicalDisk"),
        PerfRawData_PerfDisk_PhysicalDisk("Win32_PerfRawData_PerfDisk_PhysicalDisk"),
        PerfRawData_PerfNet_Browser("Win32_PerfRawData_PerfNet_Browser"),
        PerfRawData_PerfNet_Redirector("Win32_PerfRawData_PerfNet_Redirector"),
        PerfRawData_PerfNet_Server("Win32_PerfRawData_PerfNet_Server"),
        PerfRawData_PerfNet_ServerWorkQueues("Win32_PerfRawData_PerfNet_ServerWorkQueues"),
        PerfRawData_PerfOS_Cache("Win32_PerfRawData_PerfOS_Cache"),
        PerfRawData_PerfOS_Memory("Win32_PerfRawData_PerfOS_Memory"),
        PerfRawData_PerfOS_Objects("Win32_PerfRawData_PerfOS_Objects"),
        PerfRawData_PerfOS_PagingFile("Win32_PerfRawData_PerfOS_PagingFile"),
        PerfRawData_PerfOS_Processor("Win32_PerfRawData_PerfOS_Processor"),
        PerfRawData_PerfOS_System("Win32_PerfRawData_PerfOS_System"),
        PerfRawData_PerfProc_FullImage_Costly("Win32_PerfRawData_PerfProc_FullImage_Costly"),
        PerfRawData_PerfProc_Image_Costly("Win32_PerfRawData_PerfProc_Image_Costly"),
        PerfRawData_PerfProc_JobObject("Win32_PerfRawData_PerfProc_JobObject"),
        PerfRawData_PerfProc_JobObjectDetails("Win32_PerfRawData_PerfProc_JobObjectDetails"),
        PerfRawData_PerfProc_Process("Win32_PerfRawData_PerfProc_Process"),
        PerfRawData_PerfProc_ProcessAddressSpace_Costly("Win32_PerfRawData_PerfProc_ProcessAddressSpace_Costly"),
        PerfRawData_PerfProc_Thread("Win32_PerfRawData_PerfProc_Thread"),
        PerfRawData_PerfProc_ThreadDetails_Costly("Win32_PerfRawData_PerfProc_ThreadDetails_Costly"),
        PerfRawData_RemoteAccess_RASPort("Win32_PerfRawData_RemoteAccess_RASPort"),
        PerfRawData_RemoteAccess_RASTotal("Win32_PerfRawData_RemoteAccess_RASTotal"),
        PerfRawData_RSVP_RSVPInterfaces("Win32_PerfRawData_RSVP_RSVPInterfaces"),
        PerfRawData_RSVP_RSVPService("Win32_PerfRawData_RSVP_RSVPService"),
        PerfRawData_Spooler_PrintQueue("Win32_PerfRawData_Spooler_PrintQueue"),
        PerfRawData_TapiSrv_Telephony("Win32_PerfRawData_TapiSrv_Telephony"),
        PerfRawData_Tcpip_ICMP("Win32_PerfRawData_Tcpip_ICMP"),
        PerfRawData_Tcpip_IP("Win32_PerfRawData_Tcpip_IP"),
        PerfRawData_Tcpip_NBTConnection("Win32_PerfRawData_Tcpip_NBTConnection"),
        PerfRawData_Tcpip_NetworkInterface("Win32_PerfRawData_Tcpip_NetworkInterface"),
        PerfRawData_Tcpip_TCP("Win32_PerfRawData_Tcpip_TCP"),
        PerfRawData_Tcpip_UDP("Win32_PerfRawData_Tcpip_UDP"),
        PerfRawData_TermService_TerminalServices("Win32_PerfRawData_TermService_TerminalServices"),
        PerfRawData_TermService_TerminalServicesSession("Win32_PerfRawData_TermService_TerminalServicesSession"),
        PerfRawData_W3SVC_WebService("Win32_PerfRawData_W3SVC_WebService");

        private final String value;

        Performance_RawData(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum Security {
        AccountSID("Win32_AccountSID"),
        ACE("Win32_ACE"),
        LogicalFileAccess("Win32_LogicalFileAccess"),
        LogicalFileAuditing("Win32_LogicalFileAuditing"),
        LogicalFileGroup("Win32_LogicalFileGroup"),
        LogicalFileOwner("Win32_LogicalFileOwner"),
        LogicalFileSecuritySetting("Win32_LogicalFileSecuritySetting"),
        LogicalShareAccess("Win32_LogicalShareAccess"),
        LogicalShareAuditing("Win32_LogicalShareAuditing"),
        LogicalShareSecuritySetting("Win32_LogicalShareSecuritySetting"),
        PrivilegesStatus("Win32_PrivilegesStatus"),
        SecurityDescriptor("Win32_SecurityDescriptor"),
        SecuritySetting("Win32_SecuritySetting"),
        SecuritySettingAccess("Win32_SecuritySettingAccess"),
        SecuritySettingAuditing("Win32_SecuritySettingAuditing"),
        SecuritySettingGroup("Win32_SecuritySettingGroup"),
        SecuritySettingOfLogicalFile("Win32_SecuritySettingOfLogicalFile"),
        SecuritySettingOfLogicalShare("Win32_SecuritySettingOfLogicalShare"),
        SecuritySettingOfObject("Win32_SecuritySettingOfObject"),
        SecuritySettingOwner("Win32_SecuritySettingOwner"),
        SID("Win32_SID"),
        Trustee("Win32_Trustee");

        private final String value;

        Security(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum Shares {
        ConnectionShare("Win32_ConnectionShare"),
        DFSNode("Win32_DFSNode"),
        DFSNodeTarget("Win32_DFSNodeTarget"),
        DFSTarget("Win32_DFSTarget"),
        PrinterShare("Win32_PrinterShare"),
        ServerConnection("Win32_ServerConnection"),
        ServerSession("Win32_ServerSession"),
        SessionConnection("Win32_SessionConnection"),
        SessionProcess("Win32_SessionProcess"),
        Share("Win32_Share"),
        ShareToDirectory("Win32_ShareToDirectory");

        private final String value;

        Shares(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum Storage {
        ShadowBy("Win32_ShadowBy"),
        ShadowContext("Win32_ShadowContext"),
        ShadowCopy("Win32_ShadowCopy"),
        ShadowDiffVolumeSupport("Win32_ShadowDiffVolumeSupport"),
        ShadowFor("Win32_ShadowFor"),
        ShadowOn("Win32_ShadowOn"),
        ShadowProvider("Win32_ShadowProvider"),
        ShadowStorage("Win32_ShadowStorage"),
        ShadowVolumeSupport("Win32_ShadowVolumeSupport"),
        Volume("Win32_Volume"),
        VolumeUserQuota("Win32_VolumeUserQuota");

        private final String value;

        Storage(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum Users {
        Account("Win32_Account"),
        Group("Win32_Group"),
        GroupInDomain("Win32_GroupInDomain"),
        GroupUser("Win32_GroupUser"),
        LogonSession("Win32_LogonSession"),
        LogonSessionMappedDisk("Win32_LogonSessionMappedDisk"),
        NetworkLoginProfile("Win32_NetworkLoginProfile"),
        SystemAccount("Win32_SystemAccount"),
        UserAccount("Win32_UserAccount"),
        UserInDomain("Win32_UserInDomain");

        private final String value;

        Users(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }

    public enum Unknown {
        ActionCheck("Win32_ActionCheck"),
        ApplicationCommandLine("Win32_ApplicationCommandLine"),
        ApplicationService("Win32_ApplicationService"),
        AssociatedBattery("Win32_AssociatedBattery"),
        Binary("Win32_Binary"),
        BindImageAction("Win32_BindImageAction"),
        CheckCheck("Win32_CheckCheck"),
        ClassInforAction("Win32_ClassInforAction"),
        CommandLineAccess("Win32_CommandLineAccess"),
        Condition("Win32_Condition"),
        CreateFolderAction("Win32_CreateFolderAction"),
        DiskDrivePhysicalMedia("Win32_DiskDrivePhysicalMedia"),
        DisplayConfiguration("Win32_DisplayConfiguration"),
        DriverVXD("Win32_DriverVXD"),
        DuplicateFileAction("Win32_DuplicateFileAction"),
        EnvironmentSpecification("Win32_EnvironmentSpecification"),
        ExtensionInfoAction("Win32_ExtensionInfoAction"),
        FileSpecification("Win32_FileSpecification"),
        FontInfoAction("Win32_FontInfoAction"),
        IniFileSpecification("Win32_IniFileSpecification"),
        InstalledSoftwareElement("Win32_InstalledSoftwareElement"),
        LaunchCondition("Win32_LaunchCondition"),
        LoggedOnUser("Win32_LoggedOnUser"),
        LogicalMemoryConfiguration("Win32_LogicalMemoryConfiguration"),
        MIMEInfoAction("Win32_MIMEInfoAction"),
        MoveFileAction("Win32_MoveFileAction"),
        ODBCAttribute("Win32_ODBCAttribute"),
        ODBCDataSourceAttribute("Win32_ODBCDataSourceAttribute"),
        ODBCDataSourceSpecification("Win32_ODBCDataSourceSpecification"),
        ODBCDriverAttribute("Win32_ODBCDriverAttribute"),
        ODBCDriverSoftwareElement("Win32_ODBCDriverSoftwareElement"),
        ODBCDriverSpecification("Win32_ODBCDriverSpecification"),
        ODBCSourceAttribute("Win32_ODBCSourceAttribute"),
        ODBCTranslatorSpecification("Win32_ODBCTranslatorSpecification"),
        Patch("Win32_Patch"),
        PatchFile("Win32_PatchFile"),
        PatchPackage("Win32_PatchPackage"),
        PnPSignedDriver("Win32_PnPSignedDriver"),
        PnPSignedDriverCIMDataFile("Win32_PnPSignedDriverCIMDataFile"),
        Product("Win32_Product"),
        ProductCheck("Win32_ProductCheck"),
        ProductResource("Win32_ProductResource"),
        ProductSoftwareFeatures("Win32_ProductSoftwareFeatures"),
        ProgIDSpecification("Win32_ProgIDSpecification"),
        Property("Win32_Property"),
        PublishComponentAction("Win32_PublishComponentAction"),
        RegistryAction("Win32_RegistryAction"),
        RemoveFileAction("Win32_RemoveFileAction"),
        RemoveIniAction("Win32_RemoveIniAction"),
        ReserveCost("Win32_ReserveCost"),
        SelfRegModuleAction("Win32_SelfRegModuleAction"),
        ServiceControl("Win32_ServiceControl"),
        ServiceSpecification("Win32_ServiceSpecification"),
        ServiceSpecificationService("Win32_ServiceSpecificationService"),
        ShortcutAction("Win32_ShortcutAction"),
        ShortcutSAP("Win32_ShortcutSAP"),
        SoftwareElement("Win32_SoftwareElement"),
        SoftwareElementAction("Win32_SoftwareElementAction"),
        SoftwareElementCheck("Win32_SoftwareElementCheck"),
        SoftwareElementCondition("Win32_SoftwareElementCondition"),
        SoftwareElementResource("Win32_SoftwareElementResource"),
        SoftwareFeature("Win32_SoftwareFeature"),
        SoftwareFeatureAction("Win32_SoftwareFeatureAction"),
        SoftwareFeatureCheck("Win32_SoftwareFeatureCheck"),
        SoftwareFeatureParent("Win32_SoftwareFeatureParent"),
        SoftwareFeatureSoftwareElements("Win32_SoftwareFeatureSoftwareElements"),
        SystemLogicalMemoryConfiguration("Win32_SystemLogicalMemoryConfiguration"),
        Terminal("Win32_Terminal"),
        TerminalService("Win32_TerminalService"),
        TerminalServiceSetting("Win32_TerminalServiceSetting"),
        TerminalServiceToSetting("Win32_TerminalServiceToSetting"),
        TerminalTerminalSetting("Win32_TerminalTerminalSetting"),
        TSAccount("Win32_TSAccount"),
        TSClientSetting("Win32_TSClientSetting"),
        TSEnvironmentSetting("Win32_TSEnvironmentSetting"),
        TSGeneralSetting("Win32_TSGeneralSetting"),
        TSLogonSetting("Win32_TSLogonSetting"),
        TSNetworkAdapterListSetting("Win32_TSNetworkAdapterListSetting"),
        TSNetworkAdapterSetting("Win32_TSNetworkAdapterSetting"),
        TSPermissionsSetting("Win32_TSPermissionsSetting"),
        TSRemoteControlSetting("Win32_TSRemoteControlSetting"),
        TSSessionDirectory("Win32_TSSessionDirectory"),
        TSSessionDirectorySetting("Win32_TSSessionDirectorySetting"),
        TSSessionSetting("Win32_TSSessionSetting"),
        TypeLibraryAction("Win32_TypeLibraryAction"),
        UninterruptiblePowerSupply("Win32_UninterruptiblePowerSupply"),
        WMIElementSetting("Win32_WMIElementSetting"),
        WMISetting("Win32_WMISetting");

        private final String value;

        Unknown(final String text) { value = text; }

        @Override
        public String toString() { return value; }
    }
}

