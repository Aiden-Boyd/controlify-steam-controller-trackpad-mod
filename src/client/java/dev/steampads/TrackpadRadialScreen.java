package dev.steampads;

import dev.isxander.controlify.bindings.ControlifyBindings;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.controller.touchpad.Touchpads.Touchpad;
import dev.isxander.controlify.gui.screen.RadialItems;
import dev.isxander.controlify.gui.screen.RadialMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.joml.Vector2fc;

import java.util.Collections;

public final class TrackpadRadialScreen extends RadialMenuScreen {
    private final ControllerEntity controller;
    private final int padIndex;

    public TrackpadRadialScreen(ControllerEntity controller, int padIndex) {
        super(controller, ControlifyBindings.RADIAL_MENU.on(controller),
                padIndex == SteamTrackpadsClient.LEFT
                        ? Collections.singletonList(RadialItems.createHotbarItemSelect())
                        : RadialItems.createBindingPages(controller),
                padIndex == SteamTrackpadsClient.LEFT
                        ? Component.empty()
                        : Component.translatable("controlify.radial_menu.configure_hint"),
                null);
        this.controller = controller;
        this.padIndex = padIndex;
    }

    public ControllerEntity owner() {
        return controller;
    }

    @Override
    public void onControllerInput(ControllerEntity controller) {
        if (!Minecraft.getInstance().isWindowActive()) {
            onClose();
            return;
        }
        super.onControllerInput(controller);
    }

    public Vector2fc position() {
        Touchpad[] pads = SteamTrackpadsClient.trackpads(controller);
        if (pads == null || pads[padIndex].fingersNow().isEmpty()) {
            return null;
        }
        return pads[padIndex].fingersNow().getFirst().position();
    }
}
