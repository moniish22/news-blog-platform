package com.newsblog.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

/** Sanitises user-supplied HTML (article bodies, newsletters) to prevent XSS. */
@Component("htmlUtils")
public class HtmlUtils {
    private static final Safelist ARTICLE_LIST = Safelist.relaxed()
            .addTags("figure", "figcaption", "hr", "u", "s")
            .addAttributes(":all", "class")
            .addAttributes("a", "target", "rel");

    public String sanitize(String html) {
        return html == null ? "" : Jsoup.clean(html, ARTICLE_LIST);
    }

    /** Removes every tag, leaving plain text (used for comments and excerpts). */
    public String plainText(String html) {
        return html == null ? "" : Jsoup.parse(html).text();
    }
}
