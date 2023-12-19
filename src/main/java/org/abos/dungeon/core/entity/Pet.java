package org.abos.dungeon.core.entity;

public class Pet extends Thing implements Animal {

    public static final String LIST_FILE_NAME = "petList.csv";

    public Pet(String name, String description) {
        super(name, description);
    }
}
