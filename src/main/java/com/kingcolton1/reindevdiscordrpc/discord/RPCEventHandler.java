package com.kingcolton1.reindevdiscordrpc.discord;

import com.fox2code.foxloader.event.GlobalTickEvent;
import com.fox2code.foxloader.event.player.PlayerLeaveEvent;
import com.fox2code.foxloader.event.player.PlayerJoinEvent;
import com.fox2code.foxloader.event.client.GuiScreenEvent;
import com.fox2code.foxevents.EventHandler;
import com.kingcolton1.reindevdiscordrpc.util.ServerInfoUtil;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class RPCEventHandler {
    private EntityPlayer plr;
    private int lastDimension = Integer.MAX_VALUE; // Track dimension changes
    private boolean inMainMenu = false; // Track main menu state

    @EventHandler
    public void guiScreenDetect(GuiScreenEvent e) {
        if (e.getGuiScreen() instanceof GuiMainMenu) {
            // Clear player data and reset to main menu
            plr = null;
            lastDimension = Integer.MAX_VALUE; // Reset dimension tracking
            inMainMenu = true;
            setMainMenuRPC();
        } else {
            inMainMenu = false;
        }
    }

    @EventHandler
    public void onClientTick(GlobalTickEvent e) {
        try {
            Minecraft mc = Minecraft.getInstance();
            
            // Check if we're in the main menu via current screen (backup detection)
            if (mc != null && mc.currentScreen instanceof GuiMainMenu && !inMainMenu) {
                inMainMenu = true;
                plr = null;
                lastDimension = Integer.MAX_VALUE;
                
                setMainMenuRPC();
                return;
            }
            
            // Handle singleplayer dimension changes
            if (!inMainMenu) {
                if (plr == null && mc != null && mc.thePlayer != null) {
                    plr = mc.thePlayer;
                }
                
                if (plr != null && plr.dimension != lastDimension) {
                    int currentDimension = plr.dimension;
                    SwitchHandler.switchDimension(currentDimension);
                    lastDimension = currentDimension;
                }
            }
        } catch (NoSuchMethodError | NoSuchFieldError | NullPointerException err) {}
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        plr = e.getEntityPlayer();
        lastDimension = Integer.MAX_VALUE; // Reset dimension tracking
        inMainMenu = false;
        ServerInfoUtil.reset();
        SwitchHandler.switchServer(0);
        SwitchHandler.switchDimension(plr.dimension);
        lastDimension = plr.dimension;
    }

    @EventHandler
    public void onPlayerLoggedOut(PlayerLeaveEvent e) {
        plr = null;
        lastDimension = Integer.MAX_VALUE; // Reset dimension tracking
        inMainMenu = true; // Set main menu state
        setMainMenuRPC();
    }

    // Set main menu RPC and reset state
    public static void setMainMenuRPC() {
        DiscordRichPresence mainMenuRpc = new DiscordRichPresence.Builder("On the Main Menu")
                .build();
        DiscordRPC.discordUpdatePresence(mainMenuRpc);
    }
}