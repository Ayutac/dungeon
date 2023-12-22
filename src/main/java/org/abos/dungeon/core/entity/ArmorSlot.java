package org.abos.dungeon.core.entity;

import org.abos.common.Named;
import org.abos.common.StringUtil;

public enum ArmorSlot implements Named {

    HEAD,
    TORSO,
    LEGS,
    HANDS,
    FEET;

    private final String name;

    ArmorSlot() {
        name = StringUtil.toCapitalized(name());
    }

    @Override
    public String getName() {
        return name;
    }
}
