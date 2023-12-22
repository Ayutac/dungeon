package org.abos.dungeon.core.entity;

import org.abos.common.Named;

import java.util.Objects;

public enum WeaponSlot implements Named {

    RIGHT_HAND("Right Hand only"),
    LEFT_HAND("Left Hand only"),
    ANY("Any Hand"),
    BOTH("Both Hands");

    /**
     * @see #getName()
     */
    private final String name;

    WeaponSlot(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }
}
