package org.abos.dungeon.cmd;

import org.abos.dungeon.core.Question;
import org.abos.dungeon.core.Room;

import java.util.Scanner;

public class CmdQuestion extends Question {

    public CmdQuestion(String question, String answer) {
        super(question, answer);
    }

    @Override
    protected boolean displayQuestion() {
        final Scanner scanner = new Scanner(System.in);
        System.out.print(question);
        String playerAnswer = scanner.nextLine();
        return playerAnswer.equals(answer);
    }

}
