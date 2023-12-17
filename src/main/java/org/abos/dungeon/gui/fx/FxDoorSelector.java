package org.abos.dungeon.gui.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class FxDoorSelector extends Dialog<Integer> {
    
    public FxDoorSelector(final int doorCount) {
        setTitle("Door Selection");
        final Label label = new Label("Go through door number:");
        final ComboBox<Integer> box = new ComboBox<>();
        for (int i = 0; i < doorCount; i++) {
            box.getItems().add(i);
        }
        box.setValue(1);
        getDialogPane().getButtonTypes().add(ButtonType.OK);
        final Button confirmBtn = (Button)getDialogPane().lookupButton(ButtonType.OK);
        confirmBtn.setOnAction(event -> FxDoorSelector.this.resultProperty().setValue(box.getValue()));
        final HBox hbox = new HBox(label, box, confirmBtn);
        hbox.setSpacing(10d);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(5));
        
        getDialogPane().setContent(hbox);
    }
    
}
