package org.abos.dungeon.core;

import org.abos.common.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public abstract class Player implements Serializable {

    protected Room currentRoom;
    
    protected Room oldRoom;

    protected int highestRoomNumber;

    protected final Set<Integer> clearedTasks = new HashSet<>();
    
    protected final Set<Integer> collectedHamsters = new HashSet<>();

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
        oldRoom = currentRoom;
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
                collectHamster();
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

    protected abstract void displayInformation(final Information information);

    protected abstract boolean displayQuestion(final Question question);
    
    public void collectHamster() {
        if (currentRoom.awardHamster()) {
            collectedHamsters.add(currentRoom.getId());
            displayHamsterAcquisition();
        }
    }
    
    protected abstract void displayHamsterAcquisition();
    
    public int getHamsterCount() {
        return collectedHamsters.size();
    }

    @Override
    public void writeObject(final DataOutputStream dos) throws IOException {
        dos.writeInt(oldRoom.getId());
        dos.writeInt(highestRoomNumber);
        dos.writeInt(clearedTasks.size());
        for (Integer clearedTask : clearedTasks) {
            dos.writeInt(clearedTask);
        }
        dos.writeInt(collectedHamsters.size());
        for (Integer hamster : collectedHamsters) {
            dos.writeInt(hamster);
        }
    }
    
    public static Player readObject(final DataInputStream dis, final Dungeon dungeon, final Function<Room, Player> constructor) throws IOException {
        final int currentRoom = dis.readInt();
        final int highestRoomNumber = dis.readInt();
        final int taskCount = dis.readInt();
        final Set<Integer> clearedTasks = new HashSet<>();
        for (int i = 0; i < taskCount; i++) {
            clearedTasks.add(dis.readInt());
        }
        final int hamsterCount = dis.readInt();
        final Set<Integer> hamsters = new HashSet<>();
        for (int i = 0; i < hamsterCount; i++) {
            hamsters.add(dis.readInt());
        }
        final Player result = constructor.apply(dungeon.getRoom(currentRoom));
        result.highestRoomNumber = highestRoomNumber;
        result.clearedTasks.addAll(clearedTasks);
        result.collectedHamsters.addAll(hamsters);
        return result;
    }
    
}
