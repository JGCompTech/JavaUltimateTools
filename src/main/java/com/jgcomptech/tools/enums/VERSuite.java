package com.jgcomptech.tools.enums;

import com.sun.jna.platform.win32.WinDef;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        return Arrays.stream(values())
                .filter(vs -> (value.intValue() & vs.value) != 0)
                .collect(Collectors.toList());
    }

    public static VERSuite parse(final int value) {
        return Arrays.stream(VERSuite.values())
                .filter(type -> type.value == value)
                .findFirst()
                .orElse(Unknown);
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
