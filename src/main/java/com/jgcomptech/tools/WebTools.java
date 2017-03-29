package com.jgcomptech.tools;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

/** Web tools for completing tasks dealing with websites */
public class WebTools {
    /** HTML tools for handling html source code */
    public static class HTML {
        /**
         * Gets HTML source code from specified URL
         *
         * @param url URL to download from
         * @return HTML source code as string
         */
        @NotNull
        public static String getHTML(String url) {
            try {
                URL newurl = new URL(url);
                BufferedInputStream buf = new BufferedInputStream(newurl.openStream());

                StringBuilder sb = new StringBuilder();

                int data;
                while((data = buf.read()) != -1) {
                    sb.append((char) data);
                }

                return sb.toString();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        // This class should only be called statically
        private HTML() { super(); }
    }

    // This class should only be called statically
    private WebTools() { super(); }
}