package org.abos.common;

/**
 * Additional utilities where {@link Exception} and {@link Error} aren't enough.
 */
public final class ErrorUtil {

    private ErrorUtil() {
        /* No instantiation. */
    }

    /**
     * Throws a new {@link AssertionError} about unreachable code.
     * @return nothing
     * @param <T> the return type
     */
    public static <T> T unreachableCode() {
        throw new AssertionError("Unreachable code reached!");
    }

    /**
     * Throws a new {@link AssertionError} about an unknown enum, for {@code default} cases.
     * @param entry the bad enum entry
     * @return nothing
     * @param <S> the enum type
     * @param <T> the return type
     */
    public static <S extends Enum<?>, T> T unknownEnumEntry(S entry) {
        throw new AssertionError("Unknown enum entry " + entry.name() + " encountered!");
    }

}
