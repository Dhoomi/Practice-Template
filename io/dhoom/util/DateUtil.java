package io.dhoom.util;

import java.util.regex.*;
import java.util.*;
import java.math.*;
import java.text.*;
import java.io.*;

public class DateUtil
{
    private static Pattern timePattern;
    
    public static String removeTimePattern(final String input) {
        return DateUtil.timePattern.matcher(input).replaceFirst("").trim();
    }
    
    public static long parseDateDiff(final String time, final boolean future) throws Exception {
        final Matcher m = DateUtil.timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() != null) {
                if (m.group().isEmpty()) {
                    continue;
                }
                for (int c = 0; c < m.groupCount(); ++c) {
                    if (m.group(c) != null && !m.group(c).isEmpty()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty()) {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty()) {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty()) {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty()) {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) == null) {
                    break;
                }
                if (m.group(7).isEmpty()) {
                    break;
                }
                seconds = Integer.parseInt(m.group(7));
                break;
            }
        }
        if (!found) {
            throw new Exception("Illegal Date");
        }
        final GregorianCalendar var13 = new GregorianCalendar();
        if (years > 0) {
            var13.add(1, years * (future ? 1 : -1));
        }
        if (months > 0) {
            var13.add(2, months * (future ? 1 : -1));
        }
        if (weeks > 0) {
            var13.add(3, weeks * (future ? 1 : -1));
        }
        if (days > 0) {
            var13.add(5, days * (future ? 1 : -1));
        }
        if (hours > 0) {
            var13.add(11, hours * (future ? 1 : -1));
        }
        if (minutes > 0) {
            var13.add(12, minutes * (future ? 1 : -1));
        }
        if (seconds > 0) {
            var13.add(13, seconds * (future ? 1 : -1));
        }
        final GregorianCalendar max = new GregorianCalendar();
        max.add(1, 10);
        return var13.after(max) ? max.getTimeInMillis() : var13.getTimeInMillis();
    }
    
    static int dateDiff(final int type, final Calendar fromDate, final Calendar toDate, final boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            ++diff;
        }
        fromDate.setTimeInMillis(savedDate);
        return --diff;
    }
    
    public static String formatDateDiff(final long date) {
        final GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(date);
        final GregorianCalendar now = new GregorianCalendar();
        return formatDateDiff(now, c);
    }
    
    public static String formatDateDiff(final Calendar fromDate, final Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate)) {
            return "now";
        }
        if (toDate.after(fromDate)) {
            future = true;
        }
        final StringBuilder sb = new StringBuilder();
        final int[] types = { 1, 2, 5, 11, 12, 13 };
        final String[] names = { "year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds" };
        for (int accuracy = 0, i = 0; i < types.length && accuracy <= 2; ++i) {
            final int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0) {
                ++accuracy;
                sb.append(" ").append(diff).append(" ").append(names[i * 2 + ((diff > 1) ? 1 : 0)]);
            }
        }
        return (sb.length() == 0) ? "now" : sb.toString().trim();
    }
    
    public static String formatSimplifiedDateDiff(final long date) {
        final GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(date);
        final GregorianCalendar now = new GregorianCalendar();
        return formatSimplifiedDateDiff(now, c);
    }
    
    public static String formatSimplifiedDateDiff(final Calendar fromDate, final Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate)) {
            return "now";
        }
        if (toDate.after(fromDate)) {
            future = true;
        }
        final StringBuilder sb = new StringBuilder();
        final int[] types = { 1, 2, 5, 11, 12, 13 };
        final String[] names = { "y", "y", "m", "m", "d", "d", "h", "h", "m", "m", "s", "s" };
        for (int accuracy = 0, i = 0; i < types.length && accuracy <= 2; ++i) {
            final int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0) {
                ++accuracy;
                sb.append(" ").append(diff).append("").append(names[i * 2 + ((diff > 1) ? 1 : 0)]);
            }
        }
        return (sb.length() == 0) ? "now" : sb.toString().trim();
    }
    
    public static String readableTime(final long time) {
        final int SECOND = 1000;
        final int MINUTE = 60000;
        final int HOUR = 3600000;
        final int DAY = 86400000;
        long ms = time;
        final StringBuilder text = new StringBuilder("");
        if (time > 86400000L) {
            text.append(time / 86400000L).append(" days ");
            ms = time % 86400000L;
        }
        if (ms > 3600000L) {
            text.append(ms / 3600000L).append(" hours ");
            ms %= 3600000L;
        }
        if (ms > 60000L) {
            text.append(ms / 60000L).append(" minutes ");
            ms %= 60000L;
        }
        if (ms > 1000L) {
            text.append(ms / 1000L).append(" seconds ");
        }
        if (text.toString().trim().isEmpty()) {
            return "now";
        }
        return text.toString();
    }
    
    public static String readableTime(BigDecimal time) {
        final String text = "";
        if (time.doubleValue() <= 60.0) {
            time = time.add(BigDecimal.valueOf(0.1));
            return " " + time + "s";
        }
        if (time.doubleValue() <= 3600.0) {
            final int minutes = time.intValue() / 60;
            final int seconds = time.intValue() % 60;
            final DecimalFormat formatter = new DecimalFormat("00");
            return " " + formatter.format(minutes) + ":" + formatter.format(seconds) + "m";
        }
        return null;
    }
    
    static {
        DateUtil.timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);
    }
}
