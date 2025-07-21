package com.kingcolton1.reindevdiscordrpc.discord;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionHandler {
    private final String appID = "1396734001590566952";
    private final Logger logger;
    private Thread callbackThread;

    public ConnectionHandler() {
        logger = LoggerFactory.getLogger(ConnectionHandler.class);
        init();
    }

    private void init() {
        logger.info("Initializing ReIndev RPC");

        DiscordEventHandlers eventHandlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler((discordUser -> {
                    String avatar_url = "https://discordapp.com/api/avatars/";
                    if (discordUser.avatar == null) {
                        avatar_url += (Integer.parseInt(discordUser.discriminator) % 5) + ".png";
                    } else if (discordUser.avatar.startsWith("a_")) {
                        avatar_url += discordUser.userId + "/" + discordUser.avatar + ".gif";
                    } else {
                        avatar_url += discordUser.userId + "/" + discordUser.avatar + ".png";
                    }
                    logger.info(String.format("RPC Ready. Registered user %s#%s (ID: %s, avatar: %s)", discordUser.username, discordUser.discriminator, discordUser.userId, avatar_url));
                })).build();

        DiscordRPC.discordInitialize(appID, eventHandlers, true);

        DiscordRichPresence richPresence = new DiscordRichPresence.Builder("Loading Minecraft...")
                .setStartTimestamps(OffsetDateTime.now().toEpochSecond())
                .build();
        DiscordRPC.discordUpdatePresence(richPresence);

        startThread();
    }

    private void startThread() {
        logger.info("Starting callback thread.");
        callbackThread = new Thread(() -> {
            while (true) {
                try {
                    DiscordRPC.discordRunCallbacks();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }, "Discord-Callback");
        callbackThread.start();
    }
}
