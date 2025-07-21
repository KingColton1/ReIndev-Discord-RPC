package com.kingcolton1.reindevdiscordrpc;

import com.fox2code.foxloader.config.ConfigEntry;
import com.fox2code.foxloader.loader.Mod;
import com.fox2code.foxloader.event.FoxLoaderEvents;
import com.kingcolton1.reindevdiscordrpc.discord.RPC;
import com.kingcolton1.reindevdiscordrpc.discord.ConnectionHandler;
import com.kingcolton1.reindevdiscordrpc.discord.RPCEventHandler;
import net.arikia.dev.drpc.DiscordRichPresence;

public class ReindevDiscordRPC extends Mod {
    public static ReindevDiscordRPC instance;
    public static final DiscordRPCConfig CONFIG = new DiscordRPCConfig();
    public RPC rpc;
    private ConnectionHandler connectionHandler;
    private RPCEventHandler eventHandler;

    @Override
    public void onPreInit() {
        instance = this;
        this.setConfigObject(CONFIG);
        rpc = new RPC(new DiscordRichPresence());
        connectionHandler = new ConnectionHandler();
        eventHandler = new RPCEventHandler();
        FoxLoaderEvents.INSTANCE.registerEvents(eventHandler);
    }

    public static class DiscordRPCConfig {
        @ConfigEntry(configName = "Show Server IP")
        public boolean showServerIP = true;
    }
}
