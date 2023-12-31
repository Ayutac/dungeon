package org.abos.dungeon.gui.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.abos.dungeon.core.Dungeon;
import org.abos.dungeon.core.Inventory;
import org.abos.dungeon.core.crafting.Crafting;
import org.abos.dungeon.core.entity.LivingEntity;
import org.abos.dungeon.core.reward.DefaultRewardFactory;
import org.abos.dungeon.core.task.DefaultTaskFactory;
import org.abos.dungeon.core.entity.Item;

import java.util.Random;

public class FxMenu extends Application {
    @Override
    public void start(Stage stage) {
        final StackPane sp = new StackPane();
        final Scene scene = new Scene(sp, 640, 480);
        stage.setScene(scene);
        stage.show();
        final Random random = new Random(0);
        Item.init();
        LivingEntity.init();
        Crafting.init();
        final Dungeon dungeon = new Dungeon(random, new DefaultTaskFactory(random), new DefaultRewardFactory(random));
        final FxPlayer player = new FxPlayer(dungeon.getStartRoom(), new Inventory(Inventory.DEFAULT_INVENTORY_CAPACITY, Inventory.DEFAULT_STACK_CAPACITY));
        while (player.getCurrentRoom() != null) {
            player.enterNextRoom();
        }
    }

    public static void main(String[] args) {
        Application.launch();
    }
}
