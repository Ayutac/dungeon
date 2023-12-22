package org.abos.common;

/**
 * Additional utilities where {@link String} isn't enough.
 */
public final class StringUtil {

    private StringUtil() {
        /* No instantiation. */
    }

    /**
     * Capitalizes the first character of the given string if possible
     * and puts the rest of the string into lowercase, as far as possible.
     * @param s the string to capitalize
     * @return the capitalized string; {@code null} if and only if {@code s == null}
     */
    public static String toCapitalized(final String s) {
        if (s == null || s.isBlank()) {
            return s;
        }
        final String firstLetter = s.substring(0,1);
        return firstLetter.toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     * Counts the occurence of the specified character in the specified string
     * @param s the string to check
     * @param c the character to count
     * @return the amount of characters; {@code s == null} returns 0
     * @implSpec Use {@link String#toCharArray()}.
     */
    public static int countCharacter(final String s, final char c) {
        if (s == null) {
            return 0;
        }
        int count = 0;
        for (char character : s.toCharArray()) {
            if (character == c) {
                count++;
            }
        }
        return count;
    }

    /**
     * Works like {@link String#split(String)} except that trailing empty strings are included.
     * @param s the string to split, not {@code null}
     * @param delimiter the delimiter character
     * @return an array of the string slices, not {@code null}
     */
    public static String[] exactSplit(final String s, final char delimiter) {
        final String[] oldSplit = s.split(String.valueOf(delimiter));
        final int size = countCharacter(s, delimiter) + 1;
        if (oldSplit.length == size) {
            return oldSplit;
        }
        final String[] newSplit = new String[size];
        System.arraycopy(oldSplit, 0, newSplit, 0, oldSplit.length);
        for (int i = oldSplit.length; i < size; i++) {
            newSplit[i] = "";
        }
        return newSplit;
    }

}
