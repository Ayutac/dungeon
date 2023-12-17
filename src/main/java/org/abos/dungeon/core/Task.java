package org.abos.dungeon.core;

public abstract class Task implements Runnable {

    protected boolean solved;

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
}
