package com.kingcolton1.reindevdiscordrpc.discord;

import com.kingcolton1.reindevdiscordrpc.ReindevDiscordRPC;
import com.kingcolton1.reindevdiscordrpc.util.ServerInfoUtil;
import net.arikia.dev.drpc.DiscordRichPresence;
import java.time.OffsetDateTime;

public class SwitchHandler {
    public static void switchDimension(int dimension) {
        // Only show dimension if config allows it
        if (!ReindevDiscordRPC.CONFIG.showDimension) {
            return;
        }
        
        String state;
        switch (dimension) {
            case -1:
                state = "in the Nether";
                break;
            case 0:
                state = "in the Overworld";
                break;
            default:
                state = "in Unknown Dimension";
                break;
        }

        DiscordRichPresence currentRPC = ReindevDiscordRPC.instance.rpc.getRPC();
        String details = (currentRPC != null && currentRPC.details != null) ? currentRPC.details : "Playing ReIndev";
        updateRPC(details, state);
    }

    public static void switchServer(int server) {
        String details;
        switch (server) {
            case 0:
                details = "Playing Singleplayer";
                break;
            case 1:
                // For multiplayer, check if config allows showing server IP
                if (ReindevDiscordRPC.CONFIG.showServerIP && ServerInfoUtil.hasServerIP()) {
                    details = "Playing on " + ServerInfoUtil.getServerIP();
                } else {
                    details = "Playing Multiplayer";
                }
                break;
            default:
                details = "Playing ReIndev";
                break;
        }

        DiscordRichPresence currentRPC = ReindevDiscordRPC.instance.rpc.getRPC();
        
        // Choose state based on dimension visibility config
        String state;
        if (ReindevDiscordRPC.CONFIG.showDimension && currentRPC != null && currentRPC.state != null) {
            state = currentRPC.state;
        } else {
            state = "";
        }
        
        updateRPC(details, state);
    }

    private static void updateRPC(String details, String state) {
        DiscordRichPresence newRPC;
        if (ReindevDiscordRPC.CONFIG.showDimension && state != null && !state.isEmpty()) {
            newRPC = new DiscordRichPresence.Builder(state)
                    .setDetails(details)
                    .setStartTimestamps(OffsetDateTime.now().toEpochSecond())
                    .build();
        } else {
            newRPC = new DiscordRichPresence.Builder("")
                    .setDetails(details)
                    .setStartTimestamps(OffsetDateTime.now().toEpochSecond())
                    .build();
        }
        ReindevDiscordRPC.instance.rpc.setRPC(newRPC);
    }
    
    // Method to refresh RPC when config changes (buggy)
    public static void refreshRPCForConfigChange() {
        DiscordRichPresence currentRPC = ReindevDiscordRPC.instance.rpc.getRPC();
        if (currentRPC != null && currentRPC.details != null) {
            if (currentRPC.details.contains("Singleplayer")) {
                switchServer(0);
                refreshCurrentDimension();
            } else if (currentRPC.details.contains("Multiplayer") || currentRPC.details.contains("Playing on")) {
                switchServer(1);
                refreshCurrentDimension();
            }
        }
    }
    
    // Helper method to refresh current dimension from game state
    private static void refreshCurrentDimension() {
        try {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc != null && mc.thePlayer != null) {
                switchDimension(mc.thePlayer.dimension);
            }
        } catch (Exception e) {
            // If we can't get player dimension, just continue regardless
        }
    }
}
