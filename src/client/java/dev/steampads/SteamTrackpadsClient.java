package dev.steampads;

import dev.isxander.controlify.Controlify;
import dev.isxander.controlify.InputMode;
import dev.isxander.controlify.api.event.ControlifyEvents;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.controller.touchpad.Touchpads.Touchpad;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public final class SteamTrackpadsClient implements ClientModInitializer {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private static final Identifier STEAM_CONTROLLER = Identifier.fromNamespaceAndPath("controlify", "steam_controller");

    @Override
    public void onInitializeClient() {
        ControlifyEvents.CONTROLLER_STATE_UPDATE.register(event -> {
            Minecraft client = Minecraft.getInstance();
            Controlify controlify = Controlify.instance();
            if (!client.isWindowActive() || controlify.currentInputMode().isController()
                    || controlify.getCurrentController().orElse(null) != event.controller()) {
                return;
            }
            Touchpad[] pads = trackpads(event.controller());
            if (pads != null && (startedTouch(pads[LEFT]) || startedTouch(pads[RIGHT]))) {
                boolean mixed = controlify.config().getSettings().globalSettings().mixedInput;
                controlify.setInputMode(mixed ? InputMode.MIXED : InputMode.CONTROLLER);
            }
        });

        ControlifyEvents.ACTIVE_CONTROLLER_TICKED.register(event -> {
            Minecraft client = Minecraft.getInstance();
            if (!client.isWindowActive() || client.player == null || client.screen != null) {
                return;
            }

            ControllerEntity controller = event.controller();
            Touchpad[] pads = trackpads(controller);
            if (pads == null) {
                return;
            }
            if (startedTouch(pads[LEFT])) {
                openRadial(client, controller, LEFT);
            } else if (startedTouch(pads[RIGHT])) {
                openRadial(client, controller, RIGHT);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.screen instanceof TrackpadRadialScreen radial
                    && (!client.isWindowActive() || client.player == null
                    || !Controlify.instance().currentInputMode().isController()
                    || Controlify.instance().getCurrentController().orElse(null) != radial.owner())) {
                radial.onClose();
            }
        });
    }

    private static void openRadial(Minecraft client, ControllerEntity controller, int padIndex) {
        var radial = new TrackpadRadialScreen(controller, padIndex);
        client.setScreen(radial);
        if (client.screen == radial) {
            radial.onControllerInput(controller);
        }
    }

    public static Touchpad[] trackpads(ControllerEntity controller) {
        if (!STEAM_CONTROLLER.equals(controller.info().type().namespace())) {
            return null;
        }
        Touchpad[] pads = controller.touchpad().map(component -> component.touchpads()).orElse(null);
        return pads != null && pads.length == 2 ? pads : null;
    }

    public static boolean startedTouch(Touchpad pad) {
        return pad.fingersThen().isEmpty() && !pad.fingersNow().isEmpty();
    }
}
