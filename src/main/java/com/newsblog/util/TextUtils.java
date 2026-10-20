package com.newsblog.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

/** Text helpers. Instance methods are available in templates as {@code ${@textUtils...}}. */
@Component("textUtils")
@RequiredArgsConstructor
public class TextUtils {
    private final HtmlUtils htmlUtils;

    public int wordCount(String html) {
        String t = htmlUtils.plainText(html).trim();
        return t.isEmpty() ? 0 : t.split("\\s+").length;
    }

    /** Reading time in minutes at 200 words per minute (minimum 1). */
    public int readingTime(String html) {
        return Math.max(1, (int) Math.ceil(wordCount(html) / 200.0));
    }

    public String excerpt(String html, int max) { return truncate(htmlUtils.plainText(html), max); }

    public String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, Math.max(0, max - 1)).trim() + "…";
    }

    public String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] p = name.trim().split("\\s+");
        String r = p.length == 1 ? p[0].substring(0, 1) : p[0].substring(0, 1) + p[p.length - 1].substring(0, 1);
        return r.toUpperCase();
    }

    /** Builds "/path?k=v&k2=v2&" (skipping blank values) so templates can append "page=N". */
    public static String query(String path, String... keyValues) {
        StringBuilder sb = new StringBuilder(path).append('?');
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            String v = keyValues[i + 1];
            if (v != null && !v.isBlank()) {
                sb.append(keyValues[i]).append('=').append(UriUtils.encodeQueryParam(v, StandardCharsets.UTF_8)).append('&');
            }
        }
        return sb.toString();
    }
}
