package org.abos.dungeon.core.entity;

import org.abos.common.CollectionUtil;
import org.abos.common.StringUtil;
import org.abos.dungeon.core.task.Information;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface Item extends Entity {

    Set<Item> REGISTRY = new HashSet<>();

    private static void loadType(final String fileName, final int argCount, final Consumer<String[]> constructor) throws IllegalStateException {
        try {
            final URL url = Information.class.getClassLoader().getResource(fileName);
            final List<String> lines = Files.readAllLines(new File(url.getFile()).toPath());
            for (String line : lines) {
                final String[] arr = StringUtil.exactSplit(line, ',');
                if (arr.length != argCount) {
                    throw new IllegalStateException("Line with wrong number of arguments detected: " + line);
                }
                constructor.accept(arr);
            }
        }
        catch (final IOException ex) {
            System.err.printf("Reading the file %s failed!%n", fileName);
        }
        catch (final NumberFormatException ex) {
            System.err.printf("A number in the file %s couldn't be parsed!%n", fileName);
        }
        catch (final IllegalArgumentException ex) {
            System.err.printf("An argument in the file %s was incorrect!%n", fileName);
        }
    }

    static void init() throws IllegalStateException {
        loadType(Thing.LIST_FILE_NAME, 2, args ->
                new Thing(args[0], args[1]));
        loadType(Plant.LIST_FILE_NAME, 2, args ->
                new Plant(args[0], args[1]));
        loadType(Armor.LIST_FILE_NAME, 4, args ->
                new Armor(args[0], args[1], ArmorSlot.valueOf(args[2]), Integer.parseInt(args[3])));
        loadType(Weapon.LIST_FILE_NAME, 5, args ->
                new Weapon(args[0], args[1], WeaponSlot.valueOf(args[2]), Integer.parseInt(args[3]),
                        args[4].isEmpty() ? null : CollectionUtil.getByName(Item.REGISTRY, args[4])));
    }

}
