package com.newsblog.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Predicate;

/** Turns titles into URL-friendly slugs, e.g. "Hello, World!" -> "hello-world". */
public final class SlugGenerator {
    private SlugGenerator() {}

    public static String slugify(String input) {
        if (input == null) return "item";
        String s = Normalizer.normalize(input.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s = s.replaceAll("[\\s_]+", "-").replaceAll("[^\\w-]", "");
        s = s.toLowerCase(Locale.ROOT).replaceAll("-{2,}", "-").replaceAll("^-|-$", "");
        return s.isEmpty() ? "item" : s;
    }

    /** Appends -2, -3 ... until the slug is not taken. */
    public static String unique(String input, Predicate<String> exists) {
        String base = slugify(input);
        String slug = base;
        int i = 2;
        while (exists.test(slug)) slug = base + "-" + i++;
        return slug;
    }
}
