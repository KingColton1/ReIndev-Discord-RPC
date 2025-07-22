package com.kingcolton1.reindevdiscordrpc.util;

public class MultiplayerTrackingUtil {
    public static boolean hasInitialized = false;
    public static int lastKnownDimension = Integer.MAX_VALUE;

    public static void reset() {
        hasInitialized = false;
        lastKnownDimension = Integer.MAX_VALUE;
    }
}
