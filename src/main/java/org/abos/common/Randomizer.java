package org.abos.common;

import java.util.Random;

/**
 * Marks a class as having access to a {@link Random} instance.
 */
public interface Randomizer {

    /**
     * Returns the {@link Random} instance of this instance.
     * @return the randomizer, not {@code null}
     */
    Random random();

}
