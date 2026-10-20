package com.newsblog.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlugGeneratorTest {
    @Test
    void createsUrlFriendlySlug() {
        assertEquals("news-blog-platform", SlugGenerator.slugify("News & Blog Platform"));
        assertEquals("10-best-java-full-stack-practices-for-2024", SlugGenerator.slugify("10 Best Java Full Stack Practices for 2024"));
    }

    @Test
    void removesAccentsAndPunctuation() {
        assertEquals("cafe-au-lait", SlugGenerator.slugify("  Café au lait!!  "));
    }

    @Test
    void fallsBackWhenNothingUsable() {
        assertEquals("item", SlugGenerator.slugify("???"));
    }

    @Test
    void appendsNumberWhenSlugIsTaken() {
        Set<String> taken = Set.of("hello", "hello-2");
        assertEquals("hello-3", SlugGenerator.unique("Hello", taken::contains));
    }
}
