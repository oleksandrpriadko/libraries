package com.android.oleksandrpriadko.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;

public class TimeUtils {

    public static String custom(String dateString, F formatIN, F formatOUT) {
        try {
            Date date = getFormatter(formatIN.pattern).parse(dateString);
            return getFormatter(formatOUT.pattern).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date custom(String dateString, F formatIN) {
        try {
            return getFormatter(formatIN.pattern).parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String custom(Date date, F formatOUT) {
        try {
            return getFormatter(formatOUT.pattern).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date date(int dayOfMonth,
                            int month,
                            int year) {
        try {
            return getFormatter(F.dMyyyy.pattern)
                    .parse(intArrToDashString(dayOfMonth, month, year));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static Date date(String ddMyyyy) {
        try {
            return getFormatter(F.ddMyyyy.pattern).parse(ddMyyyy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static Date date(F formatIn, String ddMyyyy) {
        try {
            return getFormatter(formatIn.pattern).parse(ddMyyyy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static String EddMyyyy(Date date) {
        try {
            return getFormatter(F.EddMMMyyyy.pattern).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("SpellCheckingInspection")
    protected static String dMyyyy(Date date) {
        try {
            return getFormatter(F.dMyyyy.pattern).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static String MMMyyyy(Date date) {
        try {
            return getFormatter(F.MMMyyyy.pattern).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String yyyy(Date date) {
        try {
            return getFormatter(F.yyyy.pattern).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    protected static String intArrToDashString(int... ints) {
        StringBuilder result = new StringBuilder();
        for (int anInt : ints) {
            result.append(anInt).append("-");
        }
        return result.toString();
    }

    protected static SimpleDateFormat getFormatter(String pattern) {
        if (TextUtils.isEmpty(pattern)) {
            pattern = F.dMyyyy.pattern;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        format.setCalendar(calendar);
        return format;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public enum F {
        EddMMMyyyy("E dd MMM yyyy"),
        EddMMM("E dd.MM"),
        dMyyyy("d-M-yyyy"),
        ddMyyyy("dd-M-yyyy"),
        Edd("E dd"),
        MMMyyyy("MMM yyyy"),
        MMM("MMM"),
        yyyy("yyyy");

        public String pattern;

        F(String pattern) {
            this.pattern = pattern;
        }
    }
}
