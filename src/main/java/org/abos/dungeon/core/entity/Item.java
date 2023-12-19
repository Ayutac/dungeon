package org.abos.dungeon.core.entity;

import org.abos.dungeon.core.Information;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Item extends Entity {

    Set<Item> itemRegistry = new HashSet<>();

    /**
     * Returns true if and only if the other object is the same item as this one.
     * @param obj the object to compare, {@code null} is allowed but will always return {@code false}.
     * @return {@code true} if the items are the same, else {@code false}
     */
    @Override
    boolean equals(Object obj);

    /**
     * Returns the hash code of this item. Different item instances who {@link #equals(Object) equal} each other return the same hash code.
     * @return the hash code of this item
     */
    @Override
    int hashCode();

    static void init() throws IllegalStateException {
        final String iseErrMsg = "Line with wrong number of arguments detected: ";
        final String preformattedIoErrMsg = "Reading the file %s failed!%n";
        URL url;
        List<String> lines;
        try {
            url = Information.class.getClassLoader().getResource(Thing.LIST_FILE_NAME);
            lines = Files.readAllLines(new File(url.getFile()).toPath());
            for (String line : lines) {
                final String[] arr = line.split(",");
                if (arr.length != 2) {
                    throw new IllegalStateException(iseErrMsg + line);
                }
                new Thing(arr[0], arr[1]);
            }
        } catch (final IOException ex) {
            System.err.printf(preformattedIoErrMsg, Thing.LIST_FILE_NAME);
        }
        try {
            url = Information.class.getClassLoader().getResource(Pet.LIST_FILE_NAME);
            lines = Files.readAllLines(new File(url.getFile()).toPath());
            for (String line : lines) {
                final String[] arr = line.split(",");
                if (arr.length != 2) {
                    throw new IllegalStateException(iseErrMsg + line);
                }
                new Pet(arr[0], arr[1]);
            }
        } catch (final IOException ex) {
            System.err.printf(preformattedIoErrMsg, Pet.LIST_FILE_NAME);
        }
        try {
            url = Information.class.getClassLoader().getResource(Plant.LIST_FILE_NAME);
            lines = Files.readAllLines(new File(url.getFile()).toPath());
            for (String line : lines) {
                final String[] arr = line.split(",");
                if (arr.length != 2) {
                    throw new IllegalStateException(iseErrMsg + line);
                }
                new Plant(arr[0], arr[1]);
            }
        } catch (final IOException ex) {
            System.err.printf(preformattedIoErrMsg, Plant.LIST_FILE_NAME);
        }
    }

}
