package org.abos.dungeon.core.crafting;

import org.abos.common.CollectionUtil;
import org.abos.dungeon.core.Inventory;
import org.abos.dungeon.core.entity.Item;

public class CraftingOutput extends Inventory {

    protected CraftingOutput() {
        super(1, 1);
    }

    public CraftingOutput(final Item output) {
        this();
        if (!addItem(output)) {
            throw new AssertionError("Outputs didn't fit in crafting output inventory!");
        }
        setLocked(true);
    }

    public CraftingOutput(final String output) {
        this(CollectionUtil.getByName(Item.REGISTRY, output));
    }

}
