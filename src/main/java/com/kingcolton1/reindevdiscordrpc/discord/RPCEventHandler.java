package com.kingcolton1.reindevdiscordrpc.discord;

import com.fox2code.foxloader.event.GlobalTickEvent;
import com.fox2code.foxloader.event.player.PlayerLeaveEvent;
import com.fox2code.foxloader.event.player.PlayerJoinEvent;
import com.fox2code.foxloader.event.client.GuiScreenEvent;
import com.fox2code.foxevents.EventHandler;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class RPCEventHandler {
    private boolean firstInfoSent = false;
    private EntityPlayer plr;
    private int lastDimension = Integer.MAX_VALUE; // Track dimension changes
    private boolean inMainMenu = false; // Track main menu state
    private boolean serverTypeDetected = false; // Track if we've detected server type

    @EventHandler
    public void guiScreenDetect(GuiScreenEvent e) {
        if (e.getGuiScreen() instanceof GuiMainMenu) {
            // Clear player data and reset to main menu
            firstInfoSent = false;
            plr = null;
            lastDimension = Integer.MAX_VALUE;
            inMainMenu = true;
            serverTypeDetected = false; // Reset server type detection
            
            DiscordRichPresence mainMenuRpc = new DiscordRichPresence.Builder("On the Main Menu")
                    .build();
            DiscordRPC.discordUpdatePresence(mainMenuRpc);
        } else {
            inMainMenu = false;
        }
    }

    @EventHandler
    public void onClientTick(GlobalTickEvent e) {
        try {
            Minecraft mc = Minecraft.getInstance();
            
            // Check if we're in main menu via current screen
            if (mc != null && mc.currentScreen instanceof GuiMainMenu && !inMainMenu) {
                inMainMenu = true;
                firstInfoSent = false;
                plr = null;
                lastDimension = Integer.MAX_VALUE;
                serverTypeDetected = false; // Reset server type detection
                
                DiscordRichPresence mainMenuRpc = new DiscordRichPresence.Builder("On the Main Menu")
                        .build();
                DiscordRPC.discordUpdatePresence(mainMenuRpc);
                return;
            }
            
            // If we're not in main menu, handle game state
            if (!inMainMenu) {
                if (plr == null && mc != null && mc.thePlayer != null) {
                    plr = mc.thePlayer;
                }
                
                if (plr != null) {
                    if (plr.dimension != lastDimension) {
                        SwitchHandler.switchDimension(plr.dimension);
                        lastDimension = plr.dimension;
                        if (!serverTypeDetected) {
                            detectAndSetServerType();
                            serverTypeDetected = true;
                        } else {
                            detectAndSetServerType();
                        }
                    }
                    
                    if (!serverTypeDetected) {
                        detectAndSetServerType();
                        serverTypeDetected = true;
                    }
                    
                    if (!firstInfoSent) {
                        SwitchHandler.switchDimension(plr.dimension);
                        detectAndSetServerType();
                        firstInfoSent = true;
                        lastDimension = plr.dimension;
                        serverTypeDetected = true;
                    }
                }
            }
        } catch (NoSuchMethodError | NoSuchFieldError | NullPointerException err) {
            firstInfoSent = false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        plr = e.getEntityPlayer();
        firstInfoSent = false;
        lastDimension = Integer.MAX_VALUE; // Reset dimension tracking
        inMainMenu = false;
        serverTypeDetected = false; // Reset server type detection
        detectAndSetServerType();
    }
    
    // Detect multiplayer vs singleplayer
    private void detectAndSetServerType() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.theWorld != null) {
                if (mc.theWorld.isServer) { // Broken for server detection in multiplayer
                    SwitchHandler.switchServer(1);
                } else {
                    SwitchHandler.switchServer(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLoggedOut(PlayerLeaveEvent e) {
        firstInfoSent = false;
        plr = null;
        lastDimension = Integer.MAX_VALUE; // Reset dimension tracking
        serverTypeDetected = false; // Reset server type detection
    }
}