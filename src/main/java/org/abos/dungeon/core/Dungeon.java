package org.abos.dungeon.core;

import org.abos.common.Randomizer;
import org.abos.common.Serializable;
import org.abos.dungeon.core.reward.RewardFactory;
import org.abos.dungeon.core.task.TaskFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * The dungeon the {@link Player} can explore {@link Room Rooms} in.
 */
public class Dungeon implements Randomizer, Serializable {

    /**
     * The end of the dungeon.
     */
    protected final Room exitRoom = new Room(true, Room.EXIT_ID, this, null);

    /**
     * @see #getStartRoom()
     */
    protected Room startRoom;

    /**
     * A collection of all rooms currently existing in the dungeon.
     */
    protected final List<Room> rooms = new ArrayList<>();

    /**
     * @see #random()
     */
    protected final Random random;

    /**
     * @see #getTaskFactory()
     */
    protected final TaskFactory taskFactory;

    /**
     * @see #getRewardFactory()
     */
    protected final RewardFactory rewardFactory;

    /**
     * Creates a new dungeon with the specified parameters.
     * @param random a {@link Random} instance
     * @param taskFactory a {@link TaskFactory} instance
     * @param rewardFactory a {@link RewardFactory} instance
     * @param generateStartRoom if the start room should be generated
     * @throws NullPointerException If {@code random}, {@code taskFactory} or {@code rewardFactory} refers to {@code null}.
     */
    private Dungeon(final Random random, final TaskFactory taskFactory, final RewardFactory rewardFactory, final boolean generateStartRoom) {
        this.random = Objects.requireNonNull(random);
        this.taskFactory = Objects.requireNonNull(taskFactory);
        this.rewardFactory = Objects.requireNonNull(rewardFactory);
        if (generateStartRoom) {
            startRoom = generateRoom(exitRoom);
        }
    }

    /**
     * Creates a new dungeon with the specified parameters.
     * @param random a {@link Random} instance
     * @param taskFactory a {@link TaskFactory} instance
     * @param rewardFactory a {@link RewardFactory} instance
     * @throws NullPointerException If {@code random} or {@code taskFactory} refers to {@code null}.
     */
    public Dungeon(final Random random, final TaskFactory taskFactory, final RewardFactory rewardFactory) {
        this(random, taskFactory, rewardFactory, true);
    }

    @Override
    public Random random() {
        return random;
    }

    /**
     * Returns the {@link TaskFactory} instance of this dungeon.
     * @return the task factory, not {@code null}
     */
    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    /**
     * Returns the {@link RewardFactory} instance of this dungeon.
     * @return the reward factory, not {@code null}
     */
    public RewardFactory getRewardFactory() {
        return rewardFactory;
    }

    /**
     * Returns the start room of this dungeon, which should be right in front of the exit.
     * @return the start room, not {@code null}
     */
    public Room getStartRoom() {
        return startRoom;
    }

    /**
     * Returns the specified room of this dungeon.
     * @param index the room number
     * @return the specified room
     * @throws IndexOutOfBoundsException If {@code index} is invalid.
     * @throws IllegalStateException If the ID of the specified room doesn't match its index in the list.
     */
    public Room getRoom(int index) {
        final Room result = rooms.get(index);
        if (result.getId() != index) {
            throw new IllegalStateException("Index and room ID differ!");
        }
        return result;
    }

    /**
     * Returns an existing room different from the specified one.
     * @param from a room that shall not be returned
     * @return a randomly selected existing room
     * @throws IllegalStateException If there are no rooms to draw from.
     */
    public Room getRandomGeneratedRoom(final Room from) {
        if (rooms.isEmpty() || (rooms.size() == 1 && rooms.contains(from))) {
            throw new IllegalStateException("No rooms to draw from!");
        }
        Room selected = from;
        while (selected.equals(from)) {
            selected = rooms.get(random().nextInt(rooms.size()));
        }
        return selected;
    }

    /**
     * Generates a new room.
     * @param from the room the new room is accessed from
     * @return a new room or {@code null} if the size limit for rooms has been reached
     */
    public Room generateRoom(final Room from) {
        if (rooms.size() == Integer.MAX_VALUE) {
            return null;
        }
        final Room generated = new Room(rooms.size(), this, from.getId());
        rooms.add(generated);
        return generated;
    }

    /**
     * Returns the chance a door moves the player back to an existing room.
     */
    protected double chanceRoomGoesBack() {
        return 1d / Math.E; // about 0.368d
    }

    /**
     * Returns a random room that might be freshly generated or already exists.
     * @param from the room the new room is accessed from
     * @return a random room, not {@code null}
     */
    public Room getRandomRoom(final Room from) {
        if (random().nextDouble() < chanceRoomGoesBack() || rooms.size() == Integer.MAX_VALUE) {
            return getRandomGeneratedRoom(from);
        }
        return generateRoom(from);
    }

    @Override
    public void writeObject(final DataOutputStream dos) throws IOException {
        dos.writeInt(rooms.size());
        for (Room room : rooms) {
            room.writeObject(dos);
        }
    }

    /**
     * Reads an {@link Dungeon} instance from the specified stream.
     * @param dis the {@link DataInputStream} to read from
     * @param random the {@link Random} instance for the new dungeon
     * @param taskFactory the {@link TaskFactory} instance for the new dungeon
     * @param rewardFactory the {@link RewardFactory} instance for the new dungeon
     * @return a new {@link Dungeon} instance
     * @throws IOException If an I/O exception occurs.
     */
    public static Dungeon readObject(final DataInputStream dis, final Random random, final TaskFactory taskFactory, final RewardFactory rewardFactory) throws IOException {
        final int roomCount = dis.readInt();
        final List<Room> rooms = new LinkedList<>();
        final Dungeon result = new Dungeon(random, taskFactory, rewardFactory, false);
        for (int i = 0; i < roomCount; i++) {
            rooms.add(Room.readObject(dis, result));
        }
        result.startRoom = rooms.get(0);
        result.rooms.addAll(rooms);
        return result;
    }
}
