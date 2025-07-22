package com.kingcolton1.reindevdiscordrpc.util;

public class ServerInfoUtil {
    private static String serverIP = null;
    
    public static void setServerIP(String ip) {
        serverIP = ip;
    }
    
    public static String getServerIP() {
        return serverIP;
    }
    
    public static void reset() {
        serverIP = null;
    }
    
    public static boolean hasServerIP() {
        return serverIP != null && !serverIP.isEmpty();
    }
}
