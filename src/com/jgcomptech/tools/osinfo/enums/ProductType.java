package com.jgcomptech.tools.osinfo.enums;

/// <summary>
/// A list of Product Types according to ( http://msdn.microsoft.com/en-us/library/ms724833(VS.85).aspx )
/// </summary>
public enum ProductType implements BaseEnum {
    /// <summary>
    /// Workstation
    /// </summary>
    NTWorkstation(1),
    /// <summary>
    /// Domain Controller
    /// </summary>
    NTDomainController(2),
    /// <summary>
    /// Server
    /// </summary>
    NTServer(3);

    private final int value;

    ProductType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ProductType parse(int value) {
        for (ProductType type : ProductType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
