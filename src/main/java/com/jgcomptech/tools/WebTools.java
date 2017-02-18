package com.jgcomptech.tools;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

public class WebTools {
    public static class HTML {
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
    }
}