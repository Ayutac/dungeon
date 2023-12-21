package org.abos.dungeon.core.crafting;

import org.abos.common.CollectionUtil;
import org.abos.dungeon.core.Inventory;
import org.abos.dungeon.core.entity.Item;

public class CraftingInput extends Inventory {

    protected CraftingInput() {
        super(2, 1);
    }

    public CraftingInput(final Item input1, final Item input2) {
        this();
        if (!(addItem(input1) && addItem(input2))) {
            throw new AssertionError("Inputs didn't fit in crafting input inventory!");
        }
        setLocked(true);
    }

    public CraftingInput(final String input1, final String input2) {
        this(CollectionUtil.getByName(Item.REGISTRY, input1), CollectionUtil.getByName(Item.REGISTRY, input2));
    }
}
