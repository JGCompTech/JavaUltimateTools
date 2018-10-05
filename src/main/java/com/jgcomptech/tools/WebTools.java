package com.jgcomptech.tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/** Web tools for completing tasks dealing with websites. */
public final class WebTools {
    /** HTML tools for handling html source code. */
    public static final class HTML {
        /**
         * Gets HTML source code from specified URL.
         * @param url URL to download from
         * @return HTML source code as string
         * @throws IOException if error occurs
         * @throws MalformedURLException if url is invalid
         */
        public static String getHTML(final String url) throws IOException {
            final var newurl = new URL(url);
            final StringBuilder sb;

            try(final var buf = new BufferedInputStream(newurl.openStream())) {
                sb = new StringBuilder();
                int data;
                while((data = buf.read()) != -1) {
                    sb.append((char) data);
                }
            }
            return sb.toString();
        }

        /** Prevents instantiation of this utility class. */
        private HTML() { }
    }

    /** Prevents instantiation of this utility class. */
    private WebTools() { }
}
