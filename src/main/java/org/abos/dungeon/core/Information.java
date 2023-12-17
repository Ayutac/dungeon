package org.abos.dungeon.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Information implements Task {

    public static final String INFORMATION_LINES_FILE_NAME = "informationLines.txt";

    /* package private */ static final List<String> INFORMATION_LINES = new ArrayList<>();

    static {
        final URL linesUrl = Information.class.getClassLoader().getResource(INFORMATION_LINES_FILE_NAME);
        try {
            INFORMATION_LINES.addAll(Files.readAllLines(new File(linesUrl.getFile()).toPath()));        
        } catch (final IOException ex) {
            System.err.printf("Reading the file %s failed!%n", INFORMATION_LINES_FILE_NAME);
        }
    }

    protected String text;

    public Information(final String text) {
        this.text = Objects.requireNonNull(text);
    }

    public String getText() {
        return text;
    }

    @Override
    public void accept(final Player player) {
        player.displayInformation(this);
        player.clearCurrentTask();
    }
    
    public static Information getRandomInformation(final Random random) {
        return new Information(INFORMATION_LINES.get(random.nextInt(INFORMATION_LINES.size())));
    }
}
