package com.jgcomptech.tools;

import java.text.DecimalFormat;

public class Misc {
    /** Returns the conversion from bytes to the correct version. Ex. 1024 bytes = 1 KB */
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
