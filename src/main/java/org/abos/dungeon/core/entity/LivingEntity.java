package org.abos.dungeon.core.entity;

import org.abos.dungeon.core.task.Information;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface LivingEntity extends Entity {

    Set<Item> livingEntityRegistry = new HashSet<>();

    int getMaxHealthPoints();

    int getCurrentHealthPoints();

    static void init()  {
        final String iseErrMsg = "Line with wrong number of arguments detected: ";
        final String preformattedIoErrMsg = "Reading the file %s failed!%n";
        URL url;
        List<String> lines;
        try {
            url = Information.class.getClassLoader().getResource(Creature.LIST_FILE_NAME);
            lines = Files.readAllLines(new File(url.getFile()).toPath());
            for (String line : lines) {
                final String[] arr = line.split(",");
                if (arr.length != 3) {
                    throw new IllegalStateException(iseErrMsg + line);
                }
                new Creature(arr[0], arr[1], Integer.parseInt(arr[2]));
            }
        } catch (final IOException ex) {
            System.err.printf(preformattedIoErrMsg, Creature.LIST_FILE_NAME);
        }
    }

}
