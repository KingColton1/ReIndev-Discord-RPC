package com.kingcolton1.reindevdiscordrpc.discord;

import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class RPC {
    private DiscordRichPresence presence;

    public RPC (DiscordRichPresence presence) {
        this.presence = presence;
    }

    public void setRPC(DiscordRichPresence presence) {
        this.presence = presence;
        DiscordRPC.discordUpdatePresence(presence);
    }

    public DiscordRichPresence getRPC() {
        return presence;
    }
}
