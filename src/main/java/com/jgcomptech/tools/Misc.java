package com.jgcomptech.tools;

import java.math.RoundingMode;
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
        df.setRoundingMode(RoundingMode.DOWN);
        Double factor = 1024d;
        if(input >= factor) {
            input = input / factor;
            if(input >= factor) {
                input = input / factor;
                if(input >= factor) {
                    input = input / factor;
                    if(input >= factor) {
                        input = input / factor;
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
