package com.kingcolton1.reindevdiscordrpc.discord;

import com.kingcolton1.reindevdiscordrpc.ReindevDiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.networking.ServerData;
import java.time.OffsetDateTime;

public class SwitchHandler {
    private static ServerData serverData;

    public static void switchDimension(int dimension) {
        String state = switch (dimension) {
            case -1 -> "in the Nether";
            case 0 -> "in the Overworld";
            default -> "in Unknown Dimension";
        };

        DiscordRichPresence currentRPC = ReindevDiscordRPC.instance.rpc.getRPC();
        String details = (currentRPC != null && currentRPC.details != null) ? currentRPC.details : "in Unknown Dimension";
        RPCManager(details, state);
    }

    // TODO: Fix this method to properly handle server data
    public static void switchServer(int server) {
        String details = switch (server) {
            case 0 -> "Playing Singleplayer";
            case 1 -> "Playing Multiplayer";
            default -> "Playing ReIndev";
        };

        if (server == 1) {
            // Check if server info should be shown via config
            if (ReindevDiscordRPC.CONFIG.showServerIP) {
                try {
                    String serverIP = "Unknown Server";

                    if (serverData != null) {
                        serverIP = serverData.serverIP;
                    }

                    details = String.format("Playing Multiplayer\n%s", serverIP);
                } catch (Exception e) {
                    details = "Playing Multiplayer";
                    System.out.println("[ReIndev Discord RPC] Error getting server data: " + e.getMessage());
                }
            } else {
                details = "Playing Multiplayer";
            }
        }

        DiscordRichPresence currentRPC = ReindevDiscordRPC.instance.rpc.getRPC();
        String state = (currentRPC != null && currentRPC.state != null) ? currentRPC.state : "Playing ReIndev";
        RPCManager(details, state);
    }

    public static void RPCManager(String details, String state) {
        DiscordRichPresence newRPC = new DiscordRichPresence.Builder(state)
                .setDetails(details)
                .setStartTimestamps(OffsetDateTime.now().toEpochSecond())
                .build();
        ReindevDiscordRPC.instance.rpc.setRPC(newRPC);
    }
}
