package com.kingcolton1.reindevdiscordrpc.mixins;

import com.kingcolton1.reindevdiscordrpc.discord.SwitchHandler;
import net.minecraft.client.gui.GuiConnecting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiConnecting.class)
public class NetworkManagerMixin {
    
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConnectingScreenInit(CallbackInfo ci) {
        SwitchHandler.switchServer(1);
    }
}