package com.newsblog.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Lifecycle of an article. {@code badge} is the Bootstrap colour used in the UI. */
@Getter
@RequiredArgsConstructor
public enum ArticleStatus {
    DRAFT("Draft", "secondary"),
    PENDING_REVIEW("Pending Review", "warning"),
    PUBLISHED("Published", "success"),
    ARCHIVED("Archived", "dark"),
    SCHEDULED("Scheduled", "info");

    private final String label;
    private final String badge;
}
