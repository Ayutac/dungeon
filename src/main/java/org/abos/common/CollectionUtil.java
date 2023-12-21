package org.abos.common;

import java.util.*;

/**
 * Additional utilities where {@link Collections} isn't enough.
 */
public final class CollectionUtil {

    private CollectionUtil() {
        /* No instantiation. */
    }

    /**
     * Returns a random entry of the given collection.
     *
     * @param collection the collection to get an entry from
     * @param random a {@link Random} instance
     * @param <T> the type of elements in the collection
     * @return a random entry of the collection, might be {@code null} if the collection contains {@code null}
     * @throws NullPointerException If {@code collection} or {@code random} refers to {@code null}.
     * @throws IllegalArgumentException If the collection is empty.
     */
    public static <T> T getRandomEntry(final Collection<T> collection, final Random random) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("Collection cannot be empty!");
        }
        int selection = random.nextInt(collection.size());
        final Iterator<T> it = collection.iterator();
        while (selection > 0) {
            it.next();
            selection--;
        }
        return it.next();
    }

    /**
     * Returns a random entry from the given map.
     *
     * @param map    the map to get a random value from, not {@code null} or empty
     * @param random a {@link Random} instance to use, not {@code null}
     * @param <K>    the type of the keys in the map
     * @param <V>    the type of the mapped values
     * @return a random map entry, not {@code null}
     * @throws NullPointerException     If any parameter is {@code null}.
     * @throws IllegalArgumentException If the map is empty.
     * @implNote This method is not very efficient as it converts all entries to a list and randomly selects from that list.
     */
    public static <K, V> Map.Entry<K, V> getRandomEntry(final Map<K, V> map, final Random random) {
        if (map.isEmpty()) { // throws NPE
            throw new IllegalArgumentException("Map cannot be empty!");
        }
        final List<Map.Entry<K, V>> entryList = new ArrayList<>(map.entrySet());
        return entryList.get(random.nextInt(entryList.size())); // throws NPE
    }

    /**
     * Sorts the given map alphabetically and returns the entries in a list. The entries might still
     * be bound to the original map; changing the entries of the returned list might result in unpredictable behavior.
     *
     * @param map the map to sort
     * @param <K> the type of the keys in the map
     * @param <V> the type of the mapped values
     * @return a list of the map entries sorted alphabetically by their key
     * @throws NullPointerException If {@code map} refers to {@code null}.
     */
    public static <K extends Named, V> List<Map.Entry<K, V>> getAlphabeticalOrder(final Map<K, V> map) {
        final List<Map.Entry<K, V>> result = new ArrayList<>(map.entrySet());
        result.sort(Comparator.comparing(entry -> entry.getKey().getName()));
        return result;
    }

    /**
     * Returns the first {@link Named} instance in the given {@link Iterable} with the specified name.
     *
     * @param iterable the iterable to search through
     * @param name the name to search for
     * @param <T> the type of the iterable, implementing {@link Named}
     * @return The first encountered {@link Named} with the given name, or {@code null} if there is none.
     * Since names must not be {@code null} by the contract of {@link Named}, {@code name == null} will return {@code null}.
     * @throws NullPointerException If {@code iterable} refers to {@code null}.
     */
    public static <T extends Named> T getByName(final Iterable<T> iterable, final String name) {
        Objects.requireNonNull(iterable);
        if (name == null) {
            return null;
        }
        for (T entry : iterable) {
            if (name.equals(entry.getName())) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Sums the integer collection under the specified key together and returns it.
     *
     * @param multiMap the map to
     * @param key the key of which to sum the integer collection
     * @param <K> the key type of the map
     * @return the sum of the integers under the given key or {@code 0} if the key is not in the key set of the map.
     * @throws NullPointerException If {@code multiMap} refers to {@code null}.
     */
    public static <K> int countAll(final Map<K, ? extends Collection<Integer>> multiMap, K key) {
        final Collection<Integer> stacks = multiMap.get(key);
        if (stacks == null) {
            return 0;
        }
        return stacks.stream().mapToInt(Integer::intValue).sum();
    }
}
