package org.abos.dungeon.core;

import org.abos.common.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Dungeon implements Serializable {

    protected final Room exitRoom = new Room(true, Room.EXIT_ID, this, null);

    protected Room startRoom;

    protected final List<Room> rooms = new ArrayList<>();

    protected Random random;

    protected TaskFactory taskFactory;

    private Dungeon(final Random random, final TaskFactory taskFactory, final boolean generateStartRoom) {
        this.random = Objects.requireNonNull(random);
        this.taskFactory = Objects.requireNonNull(taskFactory);
        if (generateStartRoom) {
            startRoom = generateRoom(exitRoom);
        }
    }
    
    public Dungeon(final Random random, final TaskFactory taskFactory) {
        this(random, taskFactory, true);
    }

    public Random random() {
        return random;
    }

    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    public Room getStartRoom() {
        return startRoom;
    }
    
    public Room getRoom(int index) {
        return rooms.get(index);
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
     * Returns the chance a door moves the player back to an existing room
     * @return the chance a door moves the player back to an existing room
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
    
    public double chanceOfHamsterInRoom() {
        return 0.1d;
    }

    @Override
    public void writeObject(final DataOutputStream dos) throws IOException {
        dos.writeInt(rooms.size());
        for (Room room : rooms) {
            room.writeObject(dos);
        }
    }
    
    public static Dungeon readObject(final DataInputStream dis, final Random random, final TaskFactory taskFactory) throws IOException {
        final int roomCount = dis.readInt();
        final List<Room> rooms = new LinkedList<>();
        final Dungeon result = new Dungeon(random, taskFactory, false);
        for (int i = 0; i < roomCount; i++) {
            rooms.add(Room.readObject(dis, result));
        }
        result.startRoom = rooms.get(0);
        result.rooms.addAll(rooms);
        return result;
    }
}
