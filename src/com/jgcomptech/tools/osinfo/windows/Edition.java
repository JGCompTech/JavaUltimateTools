package com.jgcomptech.tools.osinfo.windows;

import com.jgcomptech.tools.NativeMethods;
import com.jgcomptech.tools.osinfo.enums.OtherConsts;
import com.jgcomptech.tools.osinfo.enums.ProductEdition;
import com.jgcomptech.tools.osinfo.enums.VERSuite;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import org.jetbrains.annotations.NotNull;

/**
 * Gets the product type of the operating system running on this Computer.
 */
public class Edition {
    /**
     * Gets the product type of the OS as a string
     *
     * @return string containing the the operating system product type
     */
    public static String String() {
        switch(Version.Major()) {
            case 5:
                return GetVersion5();
            case 6:
            case 10:
                return GetVersion6AndUp();
        }
        return "";
    }

    /**
     * Returns the product type from Windows 2000 to XP and Server 2000 to 2003
     */
    @NotNull
    static String GetVersion5() {
        WinNT.OSVERSIONINFOEX osVersionInfo = new WinNT.OSVERSIONINFOEX();
        if(NativeMethods.getVersionInfoFailed(osVersionInfo)) return "";
        WinDef.WORD wSuiteMask = osVersionInfo.wSuiteMask;

        if(NativeMethods.getSystemMetrics(OtherConsts.SMMediaCenter.getValue())) return " Media Center";
        if(NativeMethods.getSystemMetrics(OtherConsts.SMTabletPC.getValue())) return " Tablet PC";
        if(CheckIf.isWindowsServer()) {
            if(Version.Minor() == 0) {
                // Windows 2000 Datacenter Server
                if(VERSuite.parse(wSuiteMask).contains(VERSuite.Datacenter)) { return " Datacenter Server"; }
                // Windows 2000 Advanced Server
                if(VERSuite.parse(wSuiteMask).contains(VERSuite.Enterprise)) { return " Advanced Server"; }
                // Windows 2000 Server
                return " Server";
            }
            if(Version.Minor() == 2) {
                // Windows Server 2003 Datacenter Edition
                if(VERSuite.parse(wSuiteMask).contains(VERSuite.Datacenter)) { return " Datacenter Edition"; }
                // Windows Server 2003 Enterprise Edition
                if(VERSuite.parse(wSuiteMask).contains(VERSuite.Enterprise)) { return " Enterprise Edition"; }
                // Windows Server 2003 Storage Edition
                if(VERSuite.parse(wSuiteMask).contains(VERSuite.StorageServer)) { return " Storage Edition"; }
                // Windows Server 2003 Compute Cluster Edition
                if(VERSuite.parse(wSuiteMask).contains(VERSuite.ComputeServer)) { return " Compute Cluster Edition"; }
                // Windows Server 2003 Web Edition
                if(VERSuite.parse(wSuiteMask).contains(VERSuite.Blade)) { return " Web Edition"; }
                // Windows Server 2003 Standard Edition
                return " Standard Edition";
            }
        } else {
            //Windows XP Embedded
            if(VERSuite.parse(wSuiteMask).contains(VERSuite.EmbeddedNT)) { return " Embedded"; }
            // Windows XP / Windows 2000 Professional
            return (VERSuite.parse(wSuiteMask).contains(VERSuite.Personal)) ? " Home" : " Professional";
        }
        return "";
    }

    /**
     * Returns the product type from Windows Vista to 10 and Server 2008 to 2016
     */
    @NotNull
    static String GetVersion6AndUp() {
        switch(ProductEdition.parse(getProductInfo())) {
            case Ultimate:
            case UltimateE:
            case UltimateN:
                return "Ultimate";

            case Professional:
            case ProfessionalE:
            case ProfessionalN:
                return "Professional";

            case HomePremium:
            case HomePremiumE:
            case HomePremiumN:
                return "Home Premium";

            case HomeBasic:
            case HomeBasicE:
            case HomeBasicN:
                return "Home Basic";

            case Enterprise:
            case EnterpriseE:
            case EnterpriseN:
            case EnterpriseServerV:
                return "Enterprise";

            case Business:
            case BusinessN:
                return "Business";

            case Starter:
            case StarterE:
            case StarterN:
                return "Starter";

            case ClusterServer:
                return "Cluster Server";

            case DatacenterServer:
            case DatacenterServerV:
                return "Datacenter";

            case DatacenterServerCore:
            case DatacenterServerCoreV:
                return "Datacenter (Core installation)";

            case EnterpriseServer:
                return "Enterprise";

            case EnterpriseServerCore:
            case EnterpriseServerCoreV:
                return "Enterprise (Core installation)";

            case EnterpriseServerIA64:
                return "Enterprise For Itanium-based Systems";

            case SmallBusinessServer:
                return "Small Business Server";
            //case SmallBusinessServerPremium:
            //  return "Small Business Server Premium Edition";
            case ServerForSmallBusiness:
            case ServerForSmallBusinessV:
                return "Windows Essential Server Solutions";

            case StandardServer:
            case StandardServerV:
                return "Standard";

            case StandardServerCore:
            case StandardServerCoreV:
                return "Standard (Core installation)";

            case WebServer:
            case WebServerCore:
                return "Web Server";

            case MediumBusinessServerManagement:
            case MediumBusinessServerMessaging:
            case MediumBusinessServerSecurity:
                return "Windows Essential Business Server";

            case StorageEnterpriseServer:
            case StorageExpressServer:
            case StorageStandardServer:
            case StorageWorkgroupServer:
                return "Storage Server";
        }
        return "";
    }

    /**
     * Gets the product type of the OS as an integer
     *
     * @return integer equivalent of the operating system product type
     */
    public static int ProductType() {
        WinNT.OSVERSIONINFOEX osVersionInfo = new WinNT.OSVERSIONINFOEX();
        if(NativeMethods.getVersionInfoFailed(osVersionInfo)) return ProductEdition.Undefined.getValue();
        return osVersionInfo.wProductType;
    }

    private static int getProductInfo() {
        return NativeMethods.getProductInfo(Version.Major(), Version.Minor());
    }
}
