package com.backendless.jinx.utilities.strings;

/**
 * Created by ronan.p.higgins on 26/09/2016.
 */
public class StringUtil {

    public static String splitString(String text) {


        String[] parts = text.split("-");
        final String part = parts[4];


        return part;
    }
}
