package com.newsblog.util;

import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Date helpers, exposed to templates as {@code ${@dateUtils.format(x)}}. */
@Component("dateUtils")
public class DateUtils {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    public String format(LocalDateTime d) { return d == null ? "" : d.format(DATE); }

    public String formatDateTime(LocalDateTime d) { return d == null ? "" : d.format(DATE_TIME); }

    public String monthLabel(YearMonth ym) { return ym.format(MONTH); }

    /** The most recent {@code n} months, newest first (used by the archive page). */
    public List<YearMonth> lastMonths(int n) {
        List<YearMonth> list = new ArrayList<>();
        YearMonth now = YearMonth.now();
        for (int i = 0; i < n; i++) list.add(now.minusMonths(i));
        return list;
    }

    public String timeAgo(LocalDateTime d) {
        if (d == null) return "";
        long m = Duration.between(d, LocalDateTime.now()).toMinutes();
        if (m < 1) return "just now";
        if (m < 60) return m + " min ago";
        if (m < 1440) return (m / 60) + " h ago";
        if (m < 43200) return (m / 1440) + " d ago";
        return format(d);
    }
}
