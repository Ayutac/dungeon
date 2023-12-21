package org.abos.dungeon.core.crafting;

import org.abos.common.CollectionUtil;
import org.abos.dungeon.core.Inventory;
import org.abos.dungeon.core.entity.Item;

/**
 * Describes the input of a crafting recipe.
 *
 * @see Crafting
 * @see CraftingOutput
 */
public class CraftingInput extends Inventory {

    /**
     * Creates an empty {@link CraftingInput} instance.
     */
    protected CraftingInput() {
        super(2, 1);
    }

    /**
     * Creates a {@link CraftingInput} instance with two items and locks it.
     * @param input1 the first input item
     * @param input2 the second input item
     * @throws NullPointerException If any input refers to {@code null}.
     */
    public CraftingInput(final Item input1, final Item input2) {
        this();
        if (!(addItem(input1) && addItem(input2))) {
            throw new AssertionError("Inputs didn't fit in crafting input inventory!");
        }
        setLocked(true);
    }

    /**
     * Creates a {@link CraftingInput} instance with two items and locks it.
     * @param input1 name of the first input item
     * @param input2 name of the second input item
     * @throws NullPointerException If any input name refers to {@code null}.
     * @see #CraftingInput(Item, Item)
     */
    public CraftingInput(final String input1, final String input2) {
        this(CollectionUtil.getByName(Item.REGISTRY, input1), CollectionUtil.getByName(Item.REGISTRY, input2));
    }
}
