package org.abos.dungeon.core.item;

import org.abos.common.Named;

import java.util.Objects;

public enum WeaponSlots implements Named {

    RIGHT_HAND("Right Hand"),
    LEFT_HAND("Left Hand");

    private final String name;

    WeaponSlots(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }
}
