package com.jgcomptech.tools.enums;

/**
 * A list of Product Editions according to <a href="http://msdn.microsoft.com/en-us/library/ms724358(VS.85).aspx">Microsoft
 * Documentation</a>
 */
public enum ProductEdition implements BaseEnum {
    /** Business */
    Business(6),
    /** BusinessN */
    BusinessN(16),
    /** ClusterServer */
    ClusterServer(18),
    /** DatacenterServer */
    DatacenterServer(8),
    /** DatacenterServerCore */
    DatacenterServerCore(12),
    /** DatacenterServerCoreV */
    DatacenterServerCoreV(39),
    /** DatacenterServerV */
    DatacenterServerV(37),

    //DeveloperPreview(74),
    /** Enterprise */
    Enterprise(4),

    /** EnterpriseE */
    EnterpriseE(70),
    /** EnterpriseN */
    EnterpriseN(27),
    /** EnterpriseServer */
    EnterpriseServer(10),
    /** EnterpriseServerCore */
    EnterpriseServerCore(14),
    /** EnterpriseServerCoreV */
    EnterpriseServerCoreV(41),
    /** EnterpriseServerIA64 */
    EnterpriseServerIA64(15),
    /** EnterpriseServerV */
    EnterpriseServerV(38),
    /** HomeBasic */
    HomeBasic(2),
    /** HomeBasicE */
    HomeBasicE(67),
    /** HomeBasicN */
    HomeBasicN(5),
    /** HomePremium */
    HomePremium(3),
    /** HomePremiumE */
    HomePremiumE(68),
    /** HomePremiumN */
    HomePremiumN(26),

    //HomePremiumServer(34),
    //HyperV(42),
    /** MediumBusinessServerManagement */
    MediumBusinessServerManagement(30),

    /** MediumBusinessServerMessaging */
    MediumBusinessServerMessaging(32),
    /** MediumBusinessServerSecurity */
    MediumBusinessServerSecurity(31),
    /** Professional */
    Professional(48),
    /** ProfessionalE */
    ProfessionalE(69),
    /** ProfessionalN */
    ProfessionalN(49),

    //SBSolutionServer(50),
    /** ServerForSmallBusiness */
    ServerForSmallBusiness(24),

    /** ServerForSmallBusinessV */
    ServerForSmallBusinessV(35),

    //ServerFoundation(33),
    /** SmallBusinessServer */
    SmallBusinessServer(9),

    //SmallBusinessServerPremium(25),
    //SolutionEmbeddedServer(56),
    /** StandardServer */
    StandardServer(7),

    /** StandardServerCore */
    StandardServerCore(13),
    /** StandardServerCoreV */
    StandardServerCoreV(40),
    /** StandardServerV */
    StandardServerV(36),
    /** Starter */
    Starter(11),
    /** StarterE */
    StarterE(66),
    /** StarterN */
    StarterN(47),
    /** StorageEnterpriseServer */
    StorageEnterpriseServer(23),
    /** StorageExpressServer */
    StorageExpressServer(20),
    /** StorageStandardServer */
    StorageStandardServer(21),
    /** StorageWorkgroupServer */
    StorageWorkgroupServer(22),
    /** Undefined */
    Undefined(0),
    /** Ultimate */
    Ultimate(1),
    /** UltimateE */
    UltimateE(71),
    /** UltimateN */
    UltimateN(28),
    /** WebServer */
    WebServer(17),
    /** WebServerCore */
    WebServerCore(29);

    private final int value;

    ProductEdition(int value) {
        this.value = value;
    }

    public static ProductEdition parse(int value) {
        for(ProductEdition type : ProductEdition.values()) {
            if(type.getValue() == value) {
                return type;
            }
        }
        return Undefined;
    }

    public int getValue() {
        return value;
    }
}
