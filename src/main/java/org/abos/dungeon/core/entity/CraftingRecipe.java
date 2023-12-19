package org.abos.dungeon.core.entity;

import org.abos.common.CollectionUtil;
import org.abos.dungeon.core.task.Information;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record CraftingRecipe(Item input1, Item input2, Item output) {

    public static final String LIST_FILE_NAME = "craftingRecipeList.csv";

    public static final Set<CraftingRecipe> REGISTRY = new HashSet<>();

    public CraftingRecipe(final Item input1, final Item input2, final Item output) {
        this.input1 = Objects.requireNonNull(input1);
        this.input2 = Objects.requireNonNull(input2);
        this.output = Objects.requireNonNull(output);
    }

    public CraftingRecipe(final String input1, final String input2, final String output) {
        this(CollectionUtil.getByName(Item.REGISTRY, input1),
                CollectionUtil.getByName(Item.REGISTRY, input2),
                CollectionUtil.getByName(Item.REGISTRY, output));
    }

    public static boolean isValid(final CraftingRecipe recipe) {
        if (REGISTRY.contains(recipe)) {
            return true;
        }
        if (recipe.input1.equals(recipe.input2)) {
            return false;
        }
        return REGISTRY.contains(new CraftingRecipe(recipe.input2, recipe.input1, recipe.output));
    }

    public static void init() {
        final String iseErrMsg = "Line with wrong number of arguments detected: ";
        final String preformattedIoErrMsg = "Reading the file %s failed!%n";
        URL url;
        List<String> lines;
        try {
            url = Information.class.getClassLoader().getResource(CraftingRecipe.LIST_FILE_NAME);
            lines = Files.readAllLines(new File(url.getFile()).toPath());
            for (String line : lines) {
                final String[] arr = line.split(",");
                if (arr.length != 3) {
                    throw new IllegalStateException(iseErrMsg + line);
                }
                REGISTRY.add(new CraftingRecipe(arr[0], arr[1], arr[2]));
            }
        } catch (final IOException ex) {
            System.err.printf(preformattedIoErrMsg, CraftingRecipe.LIST_FILE_NAME);
        }
    }

}
