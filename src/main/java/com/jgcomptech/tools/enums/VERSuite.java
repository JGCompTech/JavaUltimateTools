package com.jgcomptech.tools.enums;

import com.sun.jna.platform.win32.WinDef;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of WindowsVersion Suite Masks according to
 * <a href="http://msdn.microsoft.com/en-us/library/ms724833(VS.85).aspx">Microsoft Documentation</a>.
 */
public enum VERSuite implements BaseEnum {
    Unknown(0),
    //SmallBusiness(1),
    /** Enterprise. */
    Enterprise(2),

    //BackOffice(4),
    //Terminal(16),
    //SmallBusinessRestricted(32),
    /** EmbeddedNT. */
    EmbeddedNT(64),

    /** Datacenter. */
    Datacenter(128),

    //SingleUserTS(256),
    /** Personal. */
    Personal(512),

    /** Blade. */
    Blade(1024),
    /** StorageServer. */
    StorageServer(8192),
    /** ComputeServer. */
    ComputeServer(16384);
    //WHServer = (32768);

    private final int value;

    VERSuite(final int value) {
        this.value = value;
    }

    public static List<VERSuite> parse(final WinDef.WORD value) {
        final List<VERSuite> verSuiteList = new ArrayList<>();
        for(final VERSuite vs : values()) {
            if((value.intValue() & vs.getValue()) != 0)
                verSuiteList.add(vs);
        }
        return verSuiteList;
    }

    public static VERSuite parse(final int value) {
        for(final VERSuite type : VERSuite.values()) {
            if(type.getValue() == value) {
                return type;
            }
        }
        return Unknown;
    }

    @Override
    public int getValue() { return value; }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("value", value)
                .toString();
    }
}
