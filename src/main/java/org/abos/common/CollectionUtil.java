package org.abos.common;

import java.util.*;

public final class CollectionUtil {

    private CollectionUtil() {
        /* No instantiation. */
    }

    /**
     * Returns a random entry from the given map.
     * @param map the map to get a random value from, not {@code null} or empty
     * @param random a {@link Random} instance to use, not {@code null}
     * @return a random map entry
     * @param <K> the type of the keys in the map
     * @param <V> the type of the mapped values
     * @throws NullPointerException If any parameter is {@code null}.
     * @throws IllegalArgumentException If the map is empty.
     * @implNote This method is not very efficient as it converts all entries to a list and randomly selects from that list.
     */
    public static <K, V> Map.Entry<K,V> getRandomEntry(Map<K, V> map, Random random) {
        if (map.isEmpty()) { // throws NPE
            throw new IllegalArgumentException("Map cannot be empty!");
        }
        final List<Map.Entry<K,V>> entryList = new ArrayList<>(map.entrySet());
        return entryList.get(random.nextInt(entryList.size())); // throws NPE
    }

}
