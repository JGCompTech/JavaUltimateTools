package com.jgcomptech.tools.enums;

/**
 * A list of Product Types according to <a href="http://msdn.microsoft.com/en-us/library/ms724833(VS.85).aspx">Microsoft
 * Documentation</a>
 */
public enum ProductType implements BaseEnum {
    /** Unknown OS */
    Unknown(0),
    /** Workstation */
    NTWorkstation(1),
    /** Domain Controller */
    NTDomainController(2),
    /** Server */
    NTServer(3);

    private final int value;

    ProductType(int value) {
        this.value = value;
    }

    public static ProductType parse(int value) {
        for(final ProductType type : ProductType.values()) {
            if(type.getValue() == value) {
                return type;
            }
        }
        return null;
    }

    @Override
    public int getValue() { return value; }
}
