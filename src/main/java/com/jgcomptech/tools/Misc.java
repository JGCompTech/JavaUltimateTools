package com.jgcomptech.tools;

import java.text.DecimalFormat;

/** Contains methods to do misc tasks */
public class Misc {
    /**
     * Returns the conversion from bytes to the correct version (1024 bytes = 1 KB)
     *
     * @param input Number to convert to a readable string
     * @return Specified number converted to a readable string
     */
    public static String ConvertBytes(Double input) {
        DecimalFormat df = new DecimalFormat("#.##");
        if(input >= 1024) {
            input = input / 1024;
            if(input >= 1024) {
                input = input / 1024;
                if(input >= 1024) {
                    input = input / 1024;
                    if(input >= 1024) {
                        input = input / 1024;
                        return df.format(input) + " TB";
                    }
                    return df.format(input) + " GB";
                }
                return df.format(input) + " MB";
            }
            return df.format(input) + " KB";
        }
        return df.format(input) + " Bytes";
    }
}
