package com.kingcolton1.reindevdiscordrpc.mixins;

import com.kingcolton1.reindevdiscordrpc.discord.RPCEventHandler;
import com.kingcolton1.reindevdiscordrpc.util.MultiplayerTrackingUtil;
import com.kingcolton1.reindevdiscordrpc.util.ServerInfoUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    private static final Logger logger = LoggerFactory.getLogger(MinecraftMixin.class);
    private static boolean wasInGame = false;
    private static boolean wasConnecting = false;
    
    @Inject(method = "displayGuiScreen", at = @At("HEAD"))
    private void onGuiDisplayed(net.minecraft.client.gui.GuiScreen guiScreen, CallbackInfo ci) {
        Minecraft mc = (Minecraft)(Object)this;
        
        // Track if we were really in game
        if (mc.thePlayer != null && mc.theWorld != null) {
            wasInGame = true;
        }
        
        // Track connection attempts
        if (guiScreen instanceof net.minecraft.client.gui.GuiConnecting) {
            wasConnecting = true;
            logger.info("[ReIndev Discord RPC] Starting connection attempt");
        }
        
        // Check if we're going back to main menu
        if (guiScreen instanceof GuiMainMenu) {
            // Reset multiplayer tracking when returning to main menu
            MultiplayerTrackingUtil.reset();
            // Reset server IP tracking
            ServerInfoUtil.reset();
            
            // If we were connecting but never got in game, connection failed
            if (wasConnecting && !wasInGame) {
                RPCEventHandler.setMainMenuRPC();
            }
            // If we were in game, normal disconnect
            else if (wasInGame) {
                RPCEventHandler.setMainMenuRPC();
            }
            
            // Reset flags
            wasInGame = false;
            wasConnecting = false;
        }
    }
    
    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onGameShutdown(CallbackInfo ci) {
        logger.info("Game shutting down");
        RPCEventHandler.setMainMenuRPC();
        MultiplayerTrackingUtil.reset();
        ServerInfoUtil.reset();
        wasInGame = false;
        wasConnecting = false;
    }
}
