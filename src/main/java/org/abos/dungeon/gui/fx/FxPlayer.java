package org.abos.dungeon.gui.fx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.abos.dungeon.core.task.Information;
import org.abos.dungeon.core.Inventory;
import org.abos.dungeon.core.Player;
import org.abos.dungeon.core.task.Question;
import org.abos.dungeon.core.Room;

import java.util.Optional;

public class FxPlayer extends Player {
    
    public FxPlayer(final Room startRoom, final Inventory inventory) {
        super(startRoom, inventory);
    }

    @Override
    protected Room selectDoor() {
        final FxDoorSelector doorSelector = new FxDoorSelector(currentRoom.getDoorCount());
        Optional<Integer> selection = Optional.empty();
        while (selection.isEmpty()) {
            selection = doorSelector.showAndWait();
        }
        if (selection.get() == Room.RETURN_ID && currentRoom.getId() == Room.START_ID) {
            return null;
        }
        return currentRoom.getRoomBehindDoor(selection.get());
    }

    @Override
    public void displayInformation(final Information information) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, information.getText(), ButtonType.OK);
        alert.showAndWait();
    }

    @Override
    public boolean displayQuestion(final Question question) {
        final FxQuestion fxq = new FxQuestion(question.getQuestion());
        Optional<String> result = fxq.showAndWait();
        if (result.isPresent()) {
            return result.get().equals(question.getAnswer());
        }
        return false;
    }
}
