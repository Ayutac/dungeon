package org.abos.dungeon.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public abstract class Dungeon {

    protected final Room exit = new Room(true, Room.EXIT_ID, this, null);

    protected final Room start;

    protected final List<Room> rooms = new ArrayList<>();

    protected Random random;

    protected Room currentRoom;

    protected TaskFactory taskFactory;

    public Dungeon(final Random random, final TaskFactory taskFactory) {
        this.random = Objects.requireNonNull(random);
        this.taskFactory = Objects.requireNonNull(taskFactory);
        start = generateRoom(exit);
        currentRoom = start;
    }

    public Dungeon(final TaskFactory taskFactory) {
        this(new Random(), taskFactory);
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Random random() {
        return random;
    }

    public TaskFactory getTaskFactory() {
        return taskFactory;
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
        final Room generated = new Room(rooms.size(), this, Objects.requireNonNull(from));
        rooms.add(generated);
        return generated;
    }

    /**
     * Returns the chance a door moves the player back to an existing room
     * @return the chance a door moves the player back to an existing room
     */
    protected double chanceRoomGoesBack() {
        return 1d / Math.E;
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

    /**
     * Moves the player to the next room.
     */
    public void enterNextRoom() {
        currentRoom.fillDoors();
        final Room nextRoom = selectDoor();
        final Room oldRoom = currentRoom;
        currentRoom = nextRoom;
        final Task newTask = currentRoom.getTask();
        if (newTask == null) {
            return;
        }
        if (!newTask.isSolved()) {
            newTask.run();
            if (!newTask.isSolved()) {
                currentRoom = oldRoom;
            }
        }
    }

    /**
     * Let the player select the door of the current room to go through.
     * The rooms behind the doors are guaranteed to be generated
     * when this method is called.
     * @return the next room for the player
     */
    protected abstract Room selectDoor();
}
