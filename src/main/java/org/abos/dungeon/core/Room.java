package org.abos.dungeon.core;

import org.abos.common.Serializable;
import org.abos.dungeon.core.reward.Reward;
import org.abos.dungeon.core.task.Information;
import org.abos.dungeon.core.task.Question;
import org.abos.dungeon.core.task.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the rooms of a {@link Dungeon} a {@link Player} can be in.
 */
public class Room implements Serializable {

    /**
     * The minimal amount of doors each room can have.
     * @see #MAX_DOORS
     */
    // Do NOT change this number!
    public static final int MIN_DOORS = 2;

    /**
     * The maximal amount of doors each room can have.
     * @see #MIN_DOORS
     */
    public static final int MAX_DOORS = 8;

    /**
     * The {@link Room#getId()} of the exit.
     */
    // Do NOT make this number non-negative!
    public static final int EXIT_ID = -1;

    /**
     * The {@link Room#getId()} of the start room.
     */
    public static final int START_ID = 0;

    /**
     * The door number of the return door.
     */
    public static final int RETURN_ID = 0;

    /**
     * @see #getId() 
     */
    protected final int id;

    /**
     * @see #getDoorCount() 
     */
    protected final int doorCount;

    /**
     * The dungeon this room is in.
     */
    protected final Dungeon dungeon;

    /**
     * ID of the room normally leading into this one.
     */
    private final Integer fromId;

    /**
     * The rooms reachable from this room.
     */
    protected final List<Integer> doors = new LinkedList<>();

    /**
     * @see #getTask() 
     */
    protected final Task task;

    protected boolean rewardCollected;

    /**
     * Unchecked constructor for {@link Serializable} read.
     * 
     * @see #readObject(DataInputStream, Dungeon) 
     */
    private Room(final int id, final Dungeon dungeon, final Integer fromId, final int doorCount, final Task task) {
        this.id = id;
        this.dungeon = dungeon;
        this.fromId = fromId;
        this.doorCount = doorCount;
        this.task = task;
    }

    /**
     * Creates a new {@link Room}.
     * @param exit if this is the exit room
     * @param id the ID of this room
     * @param dungeon the dungeon this room belongs to, not {@code null}
     * @param fromId the room this room was reached from
     */
    /* package private */ Room(final boolean exit, final int id, final Dungeon dungeon, final Integer fromId) {
        if (exit && id != EXIT_ID) {
            throw new IllegalArgumentException("Exit ID must be " + EXIT_ID + "!");
        }
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
    }

    /**
     * Creates a new {@link Room} that is not an exit.
     * @param id the ID of this room
     * @param dungeon the dungeon this room belongs to, not {@code null}
     * @param fromId the room this room was reached from, not {@code null}
     */
    public Room(final int id, final Dungeon dungeon, final Integer fromId) {
        this(false, id, dungeon, fromId);
    }

    /**
     * The ID of this room. Unique identifier across all rooms.
     */
    public int getId() {
        return id;
    }

    /**
     * How many other rooms are accessible from this room, including the room this room was originally accessed from.
     * @see #fromId
     */
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

    /**
     * Returns the room behind the specified door number.
     * @param index the door number
     * @throws IndexOutOfBoundsException If {@code index} is invalid.
     */
    public Room getRoomBehindDoor(final int index) throws IndexOutOfBoundsException {
        return dungeon.getRoom(doors.get(index));
    }

    /**
     * Returns the {@link Task} the {@link Player} has to solve to fully enter this room and progress.
     * @return the {@link Task} to be solved, can be {@code null}, meaning no task has to be completed.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Gives out the reward in this room, if one is available.
     * This method does NOT assign it to any player, just removes it from this room.
     * @return the reward this room had
     */
    public Reward awardReward(final Player player) {
        if (rewardCollected) {
            return null;
        }
        rewardCollected = true;
        return dungeon.getRewardFactory().apply(id, player);
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
        dos.writeBoolean(rewardCollected);
        dos.writeBoolean(task != null);
        if (task != null) {
            dos.writeUTF(task.getClass().getSimpleName());
            task.writeObject(dos);
        }
    }

    /**
     * Reads an {@link Room} instance from the specified stream.
     * @param dis the {@link DataInputStream} to read from
     * @param dungeon the {@link Dungeon} this room shall belong to
     * @return a new {@link Room} instance
     * @throws IOException If an I/O exception occurs.
     */
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
        final boolean rewardCollected = dis.readBoolean();
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
        final Room result = new Room(id, dungeon, fromId, doorCount, task);
        result.rewardCollected = rewardCollected;
        result.doors.addAll(doors);
        return result;
    }
}
