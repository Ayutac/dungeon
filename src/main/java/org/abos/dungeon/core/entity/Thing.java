package org.abos.dungeon.core.entity;

/**
 * A simple implementation of {@link Item} using {@link AbstractEntity}.
 */
public class Thing extends AbstractEntity implements Item {

    /**
     * The file name of the list of things.
     */
    public static final String LIST_FILE_NAME = "thingList.csv";

    /**
     * Creates a new {@link Thing} instance.
     * @param name the name of this thing, not {@code null}
     * @param description the description of this thing, not {@code null}
     * @throws NullPointerException If any parameter refers to {@code null}.
     */
    public Thing(final String name, final String description) {
        super(name, description);
        Item.REGISTRY.add(this);
    }

}
