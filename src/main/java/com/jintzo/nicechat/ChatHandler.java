package com.jintzo.nicechat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.Logger;

public class ChatHandler {
    private final Logger logger;
    private final SettingsUtil settingsHandler;
    private static final String prefix = "?!";
    private EntityPlayer player;

    public ChatHandler(Logger newLogger) {
        logger = newLogger;
        settingsHandler = new SettingsUtil(newLogger);
    }

    @SubscribeEvent
    public void newExternalMessage(ClientChatReceivedEvent event) {
        // clean up string
        String cleanedText = event.getMessage().getUnformattedText().toLowerCase().trim();
        logger.error("handling external message: " + cleanedText);
        boolean shouldBlock = settingsHandler.currentSettings.stream().anyMatch(string -> !string.trim().isEmpty() && cleanedText.contains(string.toLowerCase()));
        if (shouldBlock) {
            event.setCanceled(true);
        }
    }

    /**
     * Handle messages sent by the user to modify the word list
     * Handle external messages by applying the blacklist
     *
     * @param event event to be handled
     */
    @SubscribeEvent
    public void newChatMessage(ClientChatEvent event) {
        player = Minecraft.getMinecraft().player;

        // extract text
        String text = event.getMessage().trim().toLowerCase();
        logger.error("got raw text " + text);

        // only parse if the message starts with prefix, otherwise apply filter
        if (text.startsWith(prefix)) {
            // cancel message and start parsing
            event.setCanceled(true);

            handleCommand(text);
        }
    }

    /**
     * Handle an external chat message.
     * @param text text of the message
     * @return whether or not this message should be blocked
     */
    private boolean handleExternal (String text) {
        // clean up string
        String cleanedText = text.toLowerCase().trim();
        logger.error("handling external message: " + cleanedText);
        return settingsHandler.currentSettings.stream().anyMatch(string -> !string.trim().isEmpty() && string.toLowerCase().contains(cleanedText));
    }

    private void handleCommand (String command) {
        // clean up command by removing the prefix if applicable
        String text = command.replace(prefix, "").trim().toLowerCase();
        logger.error("handling command " + text);

        // if empty, send help text
        if (text.isEmpty()) {
            sendHelp();
        } else if (text.startsWith("list")) {
            sendList();
        } else if (text.startsWith("add")) {
            handleAdd(text);
        } else if (text.startsWith("remove")) {
            handleRemove(text);
        }
    }

    private void sendHelp () {
        player.sendMessage(new TextComponentString("commands: list, add, remove"));
    }

    private void sendList () {
        player.sendMessage(new TextComponentString("blocked strings: " + settingsHandler.currentSettings.toString()));
    }

    private void handleAdd (String command) {
        // clean up
        String text = command.replace("add", "").trim();

        // only continue if string is not empty, otherwise filtering would be broken
        if (text.isEmpty()) return;

        // add word and inform the user
        logger.info("adding word to list: " + text);
        settingsHandler.add(text);
        player.sendMessage(new TextComponentString("added word: " + text));
    }

    private void handleRemove (String command) {
        // clean up
        String text = command.replace("remove", "").trim();

        // remove word and inform the user
        logger.info("removing word from list: " + text);
        settingsHandler.remove(text);
        player.sendMessage(new TextComponentString("removed word: " + text));
    }
}
