package com.kingcolton1.reindevdiscordrpc.mixins;

import com.kingcolton1.reindevdiscordrpc.util.ServerInfoUtil;
import net.minecraft.common.networking.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(ServerAddress.class)
public class ServerAddressMixin {
    private final Logger logger = LoggerFactory.getLogger(ServerAddressMixin.class);
    
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onServerAddressCreated(CallbackInfo ci) {
        try {
            ServerAddress serverAddress = (ServerAddress)(Object)this;
            if (serverAddress.getIP() != null && !serverAddress.getIP().isEmpty()) {
                String serverIP = serverAddress.getIP();
                int port = serverAddress.getPort();
                
                // Check if it's an IP address using regex
                boolean isIPAddress = isValidIPAddress(serverIP);
                
                String fullAddress;
                if (isIPAddress) {
                    fullAddress = serverIP + ":" + port;
                } else {
                    // If it's a domain name, display it without port
                    fullAddress = serverIP;
                }
                ServerInfoUtil.setServerIP(fullAddress);
            }
        } catch (Exception e) {
            logger.error("[ReIndev Discord RPC] Error capturing server info: " + e.getMessage());
        }
    }
    
    private boolean isValidIPAddress(String ip) {
        // Regex pattern for IPv4 address (0-255.0-255.0-255.0-255)
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        if ("localhost".equals(ip)) {
            return true;
        }
        return ip.matches(ipv4Pattern);
    }
}
