package dev.steampads;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

/**
 * Tracks Minecraft screen context. A native Steam Controller HID bridge
 * will supply touch coordinates in a later milestone.
 */
public final class SteamTrackpadsClient implements ClientModInitializer {
    private static boolean menuOpen;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean now = client.screen != null;
            if (now != menuOpen) {
                menuOpen = now;
                System.out.println("[SteamTrackpads] context=" + (menuOpen ? "MENU" : "GAMEPLAY"));
            }
        });
    }

    public static boolean isMenuOpen() { return menuOpen; }
}
