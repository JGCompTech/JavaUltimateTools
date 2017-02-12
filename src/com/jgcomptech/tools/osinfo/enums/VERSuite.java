package com.jgcomptech.tools.osinfo.enums;

import com.sun.jna.platform.win32.WinDef;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// A list of WindowsVersion Suite Masks according to ( http://msdn.microsoft.com/en-us/library/ms724833(VS.85).aspx )
/// </summary>
public enum VERSuite implements BaseEnum {
    //SmallBusiness(1),
    /// <summary>
    /// Enterprise
    /// </summary>
    Enterprise(2),

    //BackOffice(4),
    //Terminal(16),
    //SmallBusinessRestricted(32),
    /// <summary>
    /// EmbeddedNT
    /// </summary>
    EmbeddedNT(64),

    /// <summary>
    /// Datacenter
    /// </summary>
    Datacenter(128),

    //SingleUserTS(256),
    /// <summary>
    /// Personal
    /// </summary>
    Personal(512),

    /// <summary>
    /// Blade
    /// </summary>
    Blade(1024),
    /// <summary>
    /// StorageServer
    /// </summary>
    StorageServer(8192),
    /// <summary>
    /// ComputeServer
    /// </summary>
    ComputeServer(16384);
    //WHServer = 32768);

    private final int _value;

    VERSuite(int value) {
        _value = value;
    }

    public int getValue() {
        return _value;
    }

    public static List<VERSuite> parse(WinDef.WORD value)
    {
        List<VERSuite> VERSuiteList = new ArrayList<>();
        for (VERSuite VS : values())
        {
            if ((value.intValue() & VS.getValue()) != 0)
                VERSuiteList.add(VS);
        }
        return VERSuiteList;
    }

    public static VERSuite parse(int value) {
        for (VERSuite type : VERSuite.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
