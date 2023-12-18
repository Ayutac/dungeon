package org.abos.common;

/**
 * Additional utilities where {@link String} isn't enough.
 */
public final class StringUtil {

    private StringUtil() {
        /* No instantiation. */
    }

    public static String toCapitalized(String s) {
        if (s == null || s.isBlank()) {
            return s;
        }
        final String firstLetter = s.substring(0,1);
        return firstLetter.toUpperCase() + s.substring(1).toLowerCase();
    }

}
