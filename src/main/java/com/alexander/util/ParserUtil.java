package com.alexander.util;

import org.springframework.stereotype.Component;

public class ParserUtil {
    public static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        }  catch (NumberFormatException e) {
            return 0;
        }
    }

    public static boolean parseBoolean(String value) {
        return value.equalsIgnoreCase("true");
    }
}
