package com.kingcolton1.reindevdiscordrpc.mixins;

import com.kingcolton1.reindevdiscordrpc.util.MultiplayerTrackingUtil;
import com.kingcolton1.reindevdiscordrpc.discord.SwitchHandler;
import net.minecraft.client.player.EntityClientPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityClientPlayerMP.class)
public class EntityClientPlayerMPMixin {
    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onPlayerUpdate(CallbackInfo ci) {
        EntityClientPlayerMP player = (EntityClientPlayerMP) (Object) this;
        if (player != null) {
            // Initialize on first update
            if (!MultiplayerTrackingUtil.hasInitialized) {
                SwitchHandler.switchDimension(player.dimension);
                MultiplayerTrackingUtil.hasInitialized = true;
                MultiplayerTrackingUtil.lastKnownDimension = player.dimension;
            }
            // Check for dimension changes
            else if (player.dimension != MultiplayerTrackingUtil.lastKnownDimension) {
                SwitchHandler.switchDimension(player.dimension);
                MultiplayerTrackingUtil.lastKnownDimension = player.dimension;
            }
        }
    }
    
    // Reset when player is created (new connection)
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onPlayerInit(CallbackInfo ci) {
        MultiplayerTrackingUtil.reset();
    }
}
