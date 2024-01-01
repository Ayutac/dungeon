package org.abos.dungeon.core;

import org.abos.common.Named;

import java.util.Objects;

public enum MenuEntry implements Named {

    NEW_GAME("New Game"),
    SAVE_GAME("Save Game"),
    LOAD_GAME("Load Game"),
    OPTIONS("Options"),
    CREDITS("Credits"),
    BACK("Back"),
    EXIT("Exit");

    /**
     * @see #getName()
     */
    private final String name;

    /**
     * Creates a new {@link MenuEntry} instance
     * @param name name of this entry, not {@code null}
     * @throws NullPointerException If {@code name} refers to {@code null}.
     */
    MenuEntry(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }
}
