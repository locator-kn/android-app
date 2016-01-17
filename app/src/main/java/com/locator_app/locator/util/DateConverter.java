package com.locator_app.locator.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    public static Date toDate(String s) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        try {
            return format.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String toddMMyyyy(String s) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date date = toDate(s);
        if (date != null) {
            return format.format(toDate(s));
        }
        return s;
    }
}
