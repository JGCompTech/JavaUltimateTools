package com.jgcomptech.tools.enums;

import com.sun.jna.platform.win32.WinDef;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of WindowsVersion Suite Masks according to <a href="http://msdn.microsoft.com/en-us/library/ms724833(VS.85).aspx">Microsoft
 * Documentation</a>
 */
public enum VERSuite implements BaseEnum {
    Unknown(0),
    //SmallBusiness(1),
    /** Enterprise */
    Enterprise(2),

    //BackOffice(4),
    //Terminal(16),
    //SmallBusinessRestricted(32),
    /** EmbeddedNT */
    EmbeddedNT(64),

    /** Datacenter */
    Datacenter(128),

    //SingleUserTS(256),
    /** Personal */
    Personal(512),

    /** Blade */
    Blade(1024),
    /** StorageServer */
    StorageServer(8192),
    /** ComputeServer */
    ComputeServer(16384);
    //WHServer = (32768);

    private final int _value;

    VERSuite(int value) {
        _value = value;
    }

    public static List<VERSuite> parse(WinDef.WORD value) {
        List<VERSuite> VERSuiteList = new ArrayList<>();
        for(VERSuite VS : values()) {
            if((value.intValue() & VS.getValue()) != 0)
                VERSuiteList.add(VS);
        }
        return VERSuiteList;
    }

    public static VERSuite parse(int value) {
        for(VERSuite type : VERSuite.values()) {
            if(type.getValue() == value) {
                return type;
            }
        }
        return Unknown;
    }

    public int getValue() {
        return _value;
    }
}
