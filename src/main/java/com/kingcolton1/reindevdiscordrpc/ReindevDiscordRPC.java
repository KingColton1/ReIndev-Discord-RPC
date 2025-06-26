package com.kingcolton1.reindevdiscordrpc;

import com.fox2code.foxloader.config.ConfigEntry;
import com.fox2code.foxloader.loader.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.common.networking.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReindevDiscordRPC extends Mod {
    private static final Logger log = LoggerFactory.getLogger(ReindevDiscordRPC.class);
    public static final DiscordRPCConfig CONFIG = new DiscordRPCConfig();
    private final RPC rpc = new RPC();
    private ServerAddress serverAddress;
    private Minecraft mc;
    private String currentServer = "";
    private String currentWorld = "";

    private Thread updateThread;
    private volatile boolean running = false;

    @Override
    public void onPreInit() {
        this.setConfigObject(CONFIG);
        rpc.start();
        startUpdateThread();
    }

    public static class DiscordRPCConfig {
        @ConfigEntry(configName = "Show Server IP")
        public boolean showServerIP = true;
    }

    private void startUpdateThread() {
        running = true;
        updateThread = new Thread(() -> {
            while (running) {
                try {
                    if (mc == null) continue;

                    if (mc.theWorld == null) {
                        log.info("No world detected");
                        if (!currentWorld.equals("menu")) {
                            currentWorld = "menu";
                            currentServer = "";
                            updateRPCMenu();
                        }
                    }
                    else {
                        if (mc.isMultiplayerWorld()) {
                            log.info("Multiplayer world detected");
                            String serverIP = serverAddress.getIP();
                            if (!currentServer.equals(serverIP)) {
                                currentServer = serverIP;
                                updateRPC();
                            }
                        }
                        else {
                            log.info("Singleplayer world detected");
                            String worldName = mc.theWorld.getWorldInfo().getWorldName();
                            if (!currentWorld.equals(worldName)) {
                                currentWorld = worldName;
                                updateRPC();
                            }
                        }
                    }

                    Thread.sleep(5000);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "Discord-RPC-Update-Thread");

        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void updateRPC() {
        rpc.update(builder -> {
            builder.setDetails(mc.isMultiplayerWorld() ? "Playing Multiplayer" : "Playing Singleplayer")
                    .setState(mc.isMultiplayerWorld() ? "Server: " + currentServer : "World: " + currentWorld)
                    .setLargeImage("reindev", "Reindev")
                    .setStartTimestamp(System.currentTimeMillis());
        });
    }

    private void updateRPCMenu() {
        rpc.update(builder -> {
            builder.setDetails("ReIndev 2.9 - In Menu")
                    .setState("Browsing")
                    .setLargeImage("reindev", "Reindev")
                    .setStartTimestamp(System.currentTimeMillis());
        });
    }

    public void onStop() {
        running = false;
        if (updateThread != null) {
            updateThread.interrupt();
            try {
                updateThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        rpc.stop();
    }
}
