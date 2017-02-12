package com.jgcomptech.tools.hwinfo;

import static com.jgcomptech.tools.Misc.ConvertBytes;

/**
 * RAM Information
 */
public class RAM {
    /**
     * Returns the total ram installed on the Computer.
     */
    public static String GetTotalRam() {
        long memorySize = ((com.sun.management.OperatingSystemMXBean)
                java.lang.management.ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        return ConvertBytes((double) memorySize);
    }
}
