package org.abos.dungeon.core;

import org.abos.common.MathUtil;
import org.abos.common.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Room implements Serializable {

    // do not change this number!
    public static final int MIN_DOORS = 2;

    public static final int MAX_DOORS = 8;

    public static final int EXIT_ID = -1;

    public static final int START_ID = 0;
    
    public static final int RETURN_ID = 0;

    protected final int id;

    protected final int doorCount;

    protected final Dungeon dungeon;

    private final Integer fromId;

    protected final List<Integer> doors = new LinkedList<>();

    protected final Task task;
    
    protected boolean hasHamster;

    /**
     * Unchecked constructor for {@link Serializable} read.
     * 
     * @see #readObject(DataInputStream, Dungeon) 
     */
    private Room(final int id, final Dungeon dungeon, final Integer fromId, final int doorCount, final Task task, final boolean hasHamster) {
        this.id = id;
        this.dungeon = dungeon;
        this.fromId = fromId;
        this.doorCount = doorCount;
        this.task = task;
        this.hasHamster = hasHamster;
    }

    /* package private */ Room(final boolean exit, final int id, final Dungeon dungeon, final Integer fromId) {
        if (!exit && id < 0) {
            throw new IllegalArgumentException("ID must be non-negative!");
        }
        if (!exit && fromId == null) {
            throw new NullPointerException("The room we come from can't be null!");
        }
        this.id = id;
        this.dungeon = Objects.requireNonNull(dungeon);
        this.fromId = fromId;
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

    public Room(final int id, final Dungeon dungeon, final Integer fromId) {
        this(false, id, dungeon, fromId);
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
            doors.add(dungeon.getRandomGeneratedRoom(this).getId());
        }
        else {
            doors.add(guaranteedRoom.getId());
        }
        // the others (except for door 0) could lead back
        for (int i = MIN_DOORS; i < doorCount; i++) {
            doors.add(dungeon.getRandomRoom(this).getId());
        }
        Collections.shuffle(doors, dungeon.random());
        doors.add(0, fromId);
    }

    public Room getRoomBehindDoor(final int index) {
        return dungeon.getRoom(doors.get(index));
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

    @Override
    public void writeObject(final DataOutputStream dos) throws IOException {
        dos.writeInt(id);
        dos.writeInt(doorCount);
        dos.writeBoolean(doors.isEmpty());
        if (doors.isEmpty()) {
            dos.writeInt(fromId);
        }
        else {
            for (Integer door : doors) {
                dos.writeInt(door);
            }
        }
        dos.writeBoolean(hasHamster);
        dos.writeBoolean(task != null);
        if (task != null) {
            dos.writeUTF(task.getClass().getSimpleName());
            task.writeObject(dos);
        }
    }
    
    public static Room readObject(final DataInputStream dis, final Dungeon dungeon) throws IOException {
        final int id = dis.readInt();
        final int doorCount = dis.readInt();
        final int fromId;
        final List<Integer> doors = new LinkedList<>();
        if (dis.readBoolean()) {
            fromId = dis.readInt();
        }
        else {
            for (int i = 0; i < doorCount; i++) {
                doors.add(dis.readInt());
            }
            fromId = doors.get(RETURN_ID);
        }
        final boolean hasHamster = dis.readBoolean();
        final boolean hasTask = dis.readBoolean();
        final Task task;
        if (!hasTask) {
            task = null;
        }
        else {
            final String taskClass = dis.readUTF();
            if (taskClass.equals(Information.class.getSimpleName())) {
                task = Information.readObject(dis);
            } else if (taskClass.equals(Question.class.getSimpleName())) {
                task = Question.readObject(dis);
            }
            else {
                throw new AssertionError("Unknown task subclass " + taskClass + " encountered!");
            }
        }
        final Room result = new Room(id, dungeon, fromId, doorCount, task, hasHamster);
        result.doors.addAll(doors);
        return result;
    }
}
