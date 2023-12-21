package org.abos.dungeon.core.crafting;

import org.abos.common.CollectionUtil;
import org.abos.dungeon.core.Inventory;
import org.abos.dungeon.core.entity.Item;

/**
 * Describes the output of a crafting recipe.
 *
 * @see Crafting
 * @see CraftingInput
 */
public class CraftingOutput extends Inventory {

    /**
     * Creates an empty {@link CraftingOutput} instance.
     */
    protected CraftingOutput() {
        super(1, 1);
    }

    /**
     * Creates a {@link CraftingOutput} instance with one item and locks it.
     * @param output the output item
     * @throws NullPointerException If {@code output} refers to {@code null}.
     */
    public CraftingOutput(final Item output) {
        this();
        if (!addItem(output)) {
            throw new AssertionError("Outputs didn't fit in crafting output inventory!");
        }
        setLocked(true);
    }

    /**
     * Creates a {@link CraftingOutput} instance with one item and locks it.
     * @param output name of the output item
     * @throws NullPointerException If {@code output} refers to {@code null}.
     * @see #CraftingOutput(Item)
     */
    public CraftingOutput(final String output) {
        this(CollectionUtil.getByName(Item.REGISTRY, output));
    }

}
