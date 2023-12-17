package org.abos.dungeon.gui.fx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.abos.dungeon.core.Information;
import org.abos.dungeon.core.Player;
import org.abos.dungeon.core.Question;
import org.abos.dungeon.core.Room;

import java.util.Optional;

public class FxPlayer extends Player {
    
    public FxPlayer(Room startRoom) {
        super(startRoom);
    }

    @Override
    protected Room selectDoor() {
        final FxDoorSelector doorSelector = new FxDoorSelector(currentRoom.getDoorCount());
        Optional<Integer> selection = Optional.empty();
        while (selection.isEmpty()) {
            selection = doorSelector.showAndWait();
        }
        final Room selectedRoom = currentRoom.getRoomBehindDoor(selection.get());
        if (selectedRoom.isExit()) {
            return null;
        }
        return selectedRoom;
    }

    @Override
    protected void displayInformation(final Information information) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, information.getText(), ButtonType.OK);
        alert.showAndWait();
    }

    @Override
    protected boolean displayQuestion(final Question question) {
        final FxQuestion fxq = new FxQuestion(question.getQuestion());
        Optional<String> result = fxq.showAndWait();
        if (result.isPresent()) {
            return result.get().equals(question.getAnswer());
        }
        return false;
    }

    @Override
    protected void displayHamsterAcquisition() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, Player.HAMSTER_ACQUISITION_MSG, ButtonType.OK);
        alert.showAndWait();
    }
}
