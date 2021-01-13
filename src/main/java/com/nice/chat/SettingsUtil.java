package com.nice.chat;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

public class SettingsUtil {

    private static final Path SETTINGS_DIR = Minecraft.getMinecraft().mcDataDir.toPath().toAbsolutePath().resolve("chat");
    private static final Path SETTINGS_FILE = SETTINGS_DIR.resolve("words.txt");
    private final Logger logger;

    public HashSet<String> currentSettings = new HashSet<>();

    public SettingsUtil (Logger newLogger) {
        logger = newLogger;
        initWordFile();
        loadLines();
    }

    private void initWordFile() {
        // create folder if it does not exist
        logger.info("creating folder " + SETTINGS_DIR);
        logger.info("creating file " + SETTINGS_FILE);
        if (!Files.exists(SETTINGS_DIR)) {
            SETTINGS_DIR.toFile().mkdir();
        }

        // create file if it does not exist
        if (!Files.exists(SETTINGS_FILE)) {
            BufferedWriter bw = null;
            try {
                bw = Files.newBufferedWriter(SETTINGS_FILE);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try {
                bw.write("");
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isComment(String line) {
        return line.startsWith("#") || line.startsWith("//");
    }

    private void loadLines() {
        logger.info("loading lines from file: " + SETTINGS_FILE.toString());
        try (BufferedReader scan = Files.newBufferedReader(SETTINGS_FILE)) {
            String line;
            while ((line = scan.readLine()) != null) {
                if (line.isEmpty() || isComment(line)) {
                    continue;
                }
                currentSettings.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error getting lines");
            e.printStackTrace();
        }
    }

    private synchronized void save() {
        try {
            BufferedWriter out = Files.newBufferedWriter(SETTINGS_FILE);
            currentSettings.forEach(word -> {
                try {
                    out.write(word + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("saved words to file");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(String newString) {

        // add string if not empty
        if (!newString.isEmpty()) {
            currentSettings.add(newString);
        }

        // save the modified strings list
        save();
    }

    public void remove(String oldString) {

        // remove string
        currentSettings.remove(oldString);

        // save the modified strings list
        save();

    }
}
