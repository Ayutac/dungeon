package org.abos.dungeon.core.crafting;

import org.abos.dungeon.core.task.Information;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Crafting {

    public static final String LIST_FILE_NAME = "craftingRecipeList.csv";

    public static final Map<CraftingInput, CraftingOutput> RECIPES = new HashMap<>();

    private Crafting() {
        /* No instantiation. */
    }

    public static void init() {
        final String iseErrMsg = "Line with wrong number of arguments detected: ";
        final String preformattedIoErrMsg = "Reading the file %s failed!%n";
        URL url;
        List<String> lines;
        try {
            url = Information.class.getClassLoader().getResource(Crafting.LIST_FILE_NAME);
            lines = Files.readAllLines(new File(url.getFile()).toPath());
            for (String line : lines) {
                final String[] arr = line.split(",");
                if (arr.length != 3) {
                    throw new IllegalStateException(iseErrMsg + line);
                }
                if (RECIPES.putIfAbsent(new CraftingInput(arr[0], arr[1]), new CraftingOutput(arr[2])) != null) {
                    throw new IllegalStateException("Multiple recipes with same input detected!");
                }
            }
        } catch (final IOException ex) {
            System.err.printf(preformattedIoErrMsg, Crafting.LIST_FILE_NAME);
        }
    }

}
