package org.abos.dungeon.core.entity;

import java.util.Objects;

public class Thing implements Item {

    protected String name;

    protected String description;

    public Thing(final String name, final String description) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        Item.itemRegistry.add(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Thing thing)) return false;
        return name.equals(thing.name) && description.equals(thing.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
