package org.abos.dungeon.gui.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class FxQuestion extends Dialog<String> {
    
    public FxQuestion(final String question) {
        setTitle("Question");
        final Label label = new Label(question);
        final TextField textField = new TextField();
        textField.setAlignment(Pos.CENTER);
        getDialogPane().getButtonTypes().add(ButtonType.OK);
        final Button confirmBtn = (Button)getDialogPane().lookupButton(ButtonType.OK);
        confirmBtn.setOnAction(event -> FxQuestion.this.resultProperty().setValue(textField.getText()));
        final VBox vbox = new VBox(label, textField, confirmBtn);
        vbox.setSpacing(10d);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(5));
        getDialogPane().setContent(vbox);
        textField.requestFocus();
    }
    
}
