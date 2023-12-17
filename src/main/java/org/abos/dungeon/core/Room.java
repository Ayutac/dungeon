package org.abos.dungeon.core;

import org.abos.common.MathUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Room {

    // do not change this number!
    public static final int MIN_DOORS = 2;

    public static final int MAX_DOORS = 8;

    public static final int EXIT_ID = -1;

    public static final int START_ID = 0;

    protected final int id;

    protected final int doorCount;

    protected final Dungeon dungeon;

    private final Room from;

    protected final List<Room> doors = new LinkedList<>();

    protected final Task task;
    
    protected boolean hasHamster;

    /* package private */ Room(final boolean exit, final int id, Dungeon dungeon, final Room from) {
        if (!exit && id < 0) {
            throw new IllegalArgumentException("ID must be non-negative!");
        }
        if (!exit && from == null) {
            throw new NullPointerException("The room we come from can't be null!");
        }
        this.id = id;
        this.dungeon = Objects.requireNonNull(dungeon);
        this.from = from;
        if (exit) {
            doorCount = MIN_DOORS;
            task = null;
        }
        else {
            doorCount = dungeon.random().nextInt(MIN_DOORS, MAX_DOORS + 1);
            if (id == START_ID) {
                task = null;
            }
            else {
                task = dungeon.getTaskFactory().apply(id);
            }
        }
        if (MathUtil.isPrime(id)) {
            hasHamster = dungeon.random().nextDouble() < dungeon.chanceOfHamsterInRoom();
        }
        // else hasHamster defaults to false
    }

    public Room(final int id, final Dungeon dungeon, final Room from) {
        this(false, id, dungeon, from);
    }

    public int getId() {
        return id;
    }

    public boolean isExit() {
        return id == EXIT_ID;
    }

    public int getDoorCount() {
        return doorCount;
    }

    /**
     * Assigns rooms behind the doors. Calling this method after the first
     * call will have no effects.
     */
    protected void fillDoors() {
        if (!doors.isEmpty()) {
            return;
        }
        // the first room is the one guaranteed to open to a new room
        final Room guaranteedRoom = dungeon.generateRoom(this);
        // if null it means the room size limit has been reached
        if (guaranteedRoom == null) {
            doors.add(dungeon.getRandomGeneratedRoom(this));
        }
        else {
            doors.add(guaranteedRoom);
        }
        // the others (except for door 0) could lead back
        for (int i = MIN_DOORS; i < doorCount; i++) {
            doors.add(dungeon.getRandomRoom(this));
        }
        Collections.shuffle(doors, dungeon.random());
        doors.add(0, from);
    }

    public Room getRoomBehindDoor(final int index) {
        return doors.get(index);
    }

    public Task getTask() {
        return task;
    }
    
    public boolean awardHamster() {
        final boolean hadHamster = hasHamster;
        if (hadHamster) {
            hasHamster = false;
        }
        return hadHamster;
    }
}
