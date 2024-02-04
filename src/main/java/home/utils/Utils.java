package home.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

import home.gui.IGuiConsts;

public final class Utils {

    public static String getFormattedDate(long dateTimeInMilliseconds) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(dateTimeInMilliseconds),
                TimeZone.getDefault().toZoneId());
        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofPattern(IGuiConsts.DATE_FORMAT, Locale.ROOT);
        return dateTime.format(dateFormatter);
    }

    public static long getLongFromFormattedDate(String formattedDate) {
        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofPattern(IGuiConsts.DATE_FORMAT, Locale.ROOT);
        long millisecondsSinceEpoch = LocalDateTime.parse(formattedDate, dateFormatter)
                .atZone(TimeZone.getDefault().toZoneId())
                .toInstant().toEpochMilli();
        return millisecondsSinceEpoch;
    }

    private Utils() {
    }
}
