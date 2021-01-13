package com.nice.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.Logger;

public class ChatHandler {
    private final Logger logger;
    private final SettingsUtil settingsHandler;

    public ChatHandler(Logger newLogger) {
        logger = newLogger;
        settingsHandler = new SettingsUtil(newLogger);
    }

    @SubscribeEvent
    public void newExternalMessage(ServerChatEvent event) {
        String message = event.getMessage();
        logger.info("new external message: " + message);

        // if it contains a blocked string, cancel it
        if(settingsHandler.currentSettings.stream().anyMatch(string -> message.toLowerCase().contains(string.toLowerCase()))) {
            logger.info("cancelled message: " + message);
            event.setCanceled(true);
        }
    }

    /**
     * Handle messages sent by the user to modify the word list
     *
     * @param event event to be handled
     */
    @SubscribeEvent
    public void newChatMessage(ClientChatReceivedEvent event) {

        EntityPlayer player = Minecraft.getMinecraft().player;

        final String prefix = "?!";

        // extract text
        String text = event.getMessage().getUnformattedText().replace("<" + Minecraft.getMinecraft().player.getName() + ">", "").trim();

        // only parse if the message starts with .chat, otherwise ignore
        if (!text.startsWith(prefix)) {
            logger.info("message not related");
            return;
        }

        // cancel message and start parsing
        event.setCanceled(true);

        // start by removing the prefix
        text = text.replace(prefix, "").trim();

        // if empty, send help text
        if (text.isEmpty()) {
            player.sendMessage(new TextComponentString("commands: list, add, remove"));
        } else if (text.startsWith("list")) {
            player.sendMessage(new TextComponentString("blocked strings: " + settingsHandler.currentSettings.toString()));
        } else if (text.startsWith("add")) {
            text = text.replace("add", "").trim();
            logger.info("adding word to list: " + text);
            player.sendMessage(new TextComponentString("added word: " + text));
            settingsHandler.add(text);
        } else if (text.startsWith("remove")) {
            text = text.replace("remove", "").trim();
            logger.info("removing word from list: " + text);
            player.sendMessage(new TextComponentString("removed word: " + text));
            settingsHandler.remove(text);
        }
    }
}
