package org.abos.dungeon.gui.fx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.abos.dungeon.core.crafting.CraftingOutput;
import org.abos.dungeon.core.entity.Item;
import org.abos.dungeon.core.reward.Reward;
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

    @Override
    protected void displayRewardAcquisition(Reward reward, int lostAmount) {
        final String msg;
        if (lostAmount == 0) {
            msg = String.format(Reward.PREFORMATTED_REWARD_MSG, reward.entity().getName(), reward.amount());
        }
        else {
            msg = String.format(Reward.PREFORMATTED_REWARD_WITH_LOSS_MSG, reward.entity().getName(), reward.amount(), lostAmount);
        }
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    @Override
    public void displayInventory(final Inventory inventory) {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public void displayMenagerie() {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    protected void displayCraftingIngredients() {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    protected void displayCraftingResult(final CraftingOutput output) {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    protected Item selectItem(final String msg) {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented!");
    }
}
