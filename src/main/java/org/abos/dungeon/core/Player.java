package org.abos.dungeon.core;

import java.util.*;

public abstract class Player {

    protected Room currentRoom;

    protected final Set<Integer> clearedTasks = new HashSet<>();

    protected int highestRoomNumber;

    public Player(final Room startRoom) {
        currentRoom = Objects.requireNonNull(startRoom);
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Moves the player to the next room.
     */
    public void enterNextRoom() {
        currentRoom.fillDoors();
        final Room nextRoom = selectDoor();
        final Room oldRoom = currentRoom;
        currentRoom = nextRoom;
        if (currentRoom == null) {
            return;
        }
        final Task newTask = currentRoom.getTask();
        if (newTask == null) {
            return;
        }
        if (!hasClearedTask(currentRoom.getId())) {
            newTask.accept(this);
            if (hasClearedTask(currentRoom.getId())) {
                highestRoomNumber = Math.max(highestRoomNumber, currentRoom.getId());
            }
            else {
                currentRoom = oldRoom;
            }
        }
    }

    /**
     * Let the player select the door of the current room to go through.
     * The rooms behind the doors are guaranteed to be generated
     * when this method is called.
     * @return the next room for the player; {@code null} means dungeon is to be exited
     */
    protected abstract Room selectDoor();

    public int getHighestRoomNumber() {
        return highestRoomNumber;
    }

    public boolean hasClearedTask(final int roomNumber) {
        return clearedTasks.contains(roomNumber);
    }

    public void clearCurrentTask() {
        clearedTasks.add(currentRoom.getId());
    }

    public int getClearedTaskCount() {
        return clearedTasks.size();
    }

    protected abstract boolean displayQuestion(final Question question);
}
