package com.gigmarket.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** The fixed list of gig categories shared by the browse filter and the post-gig form. */
public final class Categories {

    public static final List<String> ALL = Collections.unmodifiableList(
            Arrays.asList("Tutoring", "Design", "Development", "Writing", "Event Support"));

    private Categories() {
    }

    public static boolean isValid(String category) {
        return category != null && ALL.contains(category);
    }
}
