package org.abos.dungeon.core.task;

import org.abos.dungeon.core.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A {@link Task} that just consists of recognizing some information in text form.
 */
public class Information implements Task {

    /**
     * File name of the information lines.
     */
    public static final String LINES_FILE_NAME = "informationLines.csv";

    /**
     * The information lines to randomly generate {@link Information} instances from.
     */
    /* package private */ static final List<String> INFORMATION_LINES = new ArrayList<>();

    static {
        final URL linesUrl = Information.class.getClassLoader().getResource(LINES_FILE_NAME);
        try {
            INFORMATION_LINES.addAll(Files.readAllLines(new File(linesUrl.getFile()).toPath()));        
        } catch (final IOException ex) {
            System.err.printf("Reading the file %s failed!%n", LINES_FILE_NAME);
        }
    }

    /**
     * @see #getText() 
     */
    protected String text;

    /**
     * Creates a new {@link Information} instance with the specified text.
     * @param text the text to display, not {@code null}
     */
    public Information(final String text) {
        this.text = Objects.requireNonNull(text);
    }

    /**
     * Returns the text of this information to be recognized.
     */
    public String getText() {
        return text;
    }

    @Override
    public void accept(final Player player) {
        player.displayInformation(this);
        player.clearCurrentTask();
    }

    @Override
    public void writeObject(final DataOutputStream dos) throws IOException {
        dos.writeUTF(text);
    }

    /**
     * Reads an {@link Information} instance from the specified stream.
     * @param dis the {@link DataInputStream} to read from
     * @return a new {@link Information} instance
     * @throws IOException If an I/O exception occurs.
     */
    public static Information readObject(final DataInputStream dis) throws IOException {
        return new Information(dis.readUTF());
    }

    /**
     * Returns a new {@link Information} instance with text randomly selected from {@link #INFORMATION_LINES}.
     * @param random a {@link Random} instance
     * @return a new and randomized {@link Information} instance
     */
    public static Information getRandomInformation(final Random random) {
        return new Information(INFORMATION_LINES.get(random.nextInt(INFORMATION_LINES.size())));
    }
}
