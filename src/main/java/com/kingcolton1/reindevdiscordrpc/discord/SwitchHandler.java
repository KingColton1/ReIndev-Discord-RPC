package com.kingcolton1.reindevdiscordrpc.discord;

import com.kingcolton1.reindevdiscordrpc.ReindevDiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import java.time.OffsetDateTime;

public class SwitchHandler {
    public static void switchDimension(int dimension) {
        String state;
        if (dimension == -1) {
            state = "in the Nether";
        } else if (dimension == 0) {
            state = "in the Overworld";
        } else {
            state = "in Unknown Dimension";
        }

        DiscordRichPresence currentRPC = ReindevDiscordRPC.instance.rpc.getRPC();
        String details = (currentRPC != null && currentRPC.details != null) ? currentRPC.details : "Playing ReIndev";
        
        DiscordRichPresence newRPC = new DiscordRichPresence.Builder(details)
                .setStartTimestamps(OffsetDateTime.now().toEpochSecond())
                .build();

        newRPC.state = state;
        ReindevDiscordRPC.instance.rpc.setRPC(newRPC);
    }

    public static void switchServer(int server) {
        String details = switch (server) {
            case 0 -> "Playing ReIndev Singleplayer";
            case 1 -> "Playing ReIndev Multiplayer";
            default -> "Playing ReIndev";
        };

        DiscordRichPresence currentRPC = ReindevDiscordRPC.instance.rpc.getRPC();
        
        DiscordRichPresence newRPC = new DiscordRichPresence.Builder(details)
                .setStartTimestamps(OffsetDateTime.now().toEpochSecond())
                .build();

        if (currentRPC != null && currentRPC.state != null) {
            newRPC.state = currentRPC.state;
        }
        ReindevDiscordRPC.instance.rpc.setRPC(newRPC);
    }
}
