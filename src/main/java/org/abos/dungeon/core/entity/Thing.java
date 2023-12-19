package org.abos.dungeon.core.entity;

public class Thing extends AbstractEntity implements Item {

    public static final String LIST_FILE_NAME = "thingList.csv";

    public Thing(final String name, final String description) {
        super(name, description);
        Item.itemRegistry.add(this);
    }

}
