package org.abos.dungeon.core.entity;

public class Plant extends Thing implements LivingEntity {

    public static final String LIST_FILE_NAME = "plantList.csv";

    public Plant(String name, String description) {
        super(name, description);
    }
}
