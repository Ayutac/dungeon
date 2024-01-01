package org.abos.dungeon.core;

import org.abos.common.Named;

import java.util.Objects;

public enum TurnEntry implements Named {

    DOOR("Select a door"),
    CRAFT("Craft something"),
    MENU("Go to menu");

    /**
     * @see #getName()
     */
    private final String name;

    TurnEntry(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }
}
