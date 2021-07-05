package com.sauron.eye.util;

import org.slf4j.helpers.MessageFormatter;

public class TextUtils {

    public static String format(String msg, Object... args) {
        return MessageFormatter.arrayFormat(msg, args).getMessage();
    }
}
