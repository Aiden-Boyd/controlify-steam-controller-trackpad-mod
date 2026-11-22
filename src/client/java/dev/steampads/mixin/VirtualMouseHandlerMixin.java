package dev.steampads.mixin;

import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.controller.touchpad.Touchpads.Touchpad;
import dev.isxander.controlify.utils.MinecraftUtil;
import dev.isxander.controlify.screenop.ScreenProcessorProvider;
import dev.isxander.controlify.virtualmouse.VirtualMouseHandler;
import dev.steampads.SteamTrackpadsClient;
import net.minecraft.client.Minecraft;
import org.joml.Vector2fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VirtualMouseHandler.class, remap = false)
abstract class VirtualMouseHandlerMixin {
    @Shadow private double targetX;
    @Shadow private double targetY;
    @Shadow private double scrollY;
    @Shadow private boolean virtualMouseEnabled;
    @Shadow public abstract void enableVirtualMouse();

    @Inject(method = "handleControllerInput", at = @At("HEAD"))
    private void steamtrackpads$enableCursorOnTouch(ControllerEntity controller, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        var screen = MinecraftUtil.getScreen();
        if (virtualMouseEnabled || screen == null || !client.isWindowActive()) {
            return;
        }
        Touchpad[] pads = SteamTrackpadsClient.trackpads(controller);
        if (pads != null && (SteamTrackpadsClient.startedTouch(pads[SteamTrackpadsClient.LEFT])
                || SteamTrackpadsClient.startedTouch(pads[SteamTrackpadsClient.RIGHT]))
                && ScreenProcessorProvider.provide(screen).virtualMouseBehaviour().hasCursor()) {
            enableVirtualMouse();
        }
    }

    @Inject(method = "handleControllerInput", at = @At(value = "INVOKE",
            target = "Ldev/isxander/controlify/screenop/ScreenProcessorProvider;provide(Lnet/minecraft/client/gui/screens/Screen;)Ldev/isxander/controlify/screenop/ScreenProcessor;"))
    private void steamtrackpads$moveCursor(ControllerEntity controller, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        Touchpad[] pads = SteamTrackpadsClient.trackpads(controller);
        if (!client.isWindowActive() || pads == null) {
            return;
        }
        Touchpad right = pads[SteamTrackpadsClient.RIGHT];
        if (steamtrackpads$hasDelta(right)) {
            Vector2fc previous = right.fingersThen().getFirst().position();
            Vector2fc current = right.fingersNow().getFirst().position();
            var window = client.getWindow();
            float sensitivity = controller.input().orElseThrow().settings().sensitivity.virtualMouseSensitivity;
            double scale = Math.max(window.getWidth(), window.getHeight()) * sensitivity;
            targetX = Math.clamp(targetX + (current.x() - previous.x()) * scale, 0, window.getWidth());
            targetY = Math.clamp(targetY + (current.y() - previous.y()) * scale, 0, window.getHeight());
        }
    }

    @Inject(method = "handleScroll", at = @At("TAIL"))
    private void steamtrackpads$scroll(ControllerEntity controller, CallbackInfo ci) {
        Touchpad[] pads = SteamTrackpadsClient.trackpads(controller);
        if (!Minecraft.getInstance().isWindowActive() || pads == null) {
            return;
        }
        Touchpad left = pads[SteamTrackpadsClient.LEFT];
        if (steamtrackpads$hasDelta(left)) {
            scrollY += (left.fingersThen().getFirst().position().y()
                    - left.fingersNow().getFirst().position().y()) * 12;
        }
    }

    @Unique
    private static boolean steamtrackpads$hasDelta(Touchpad pad) {
        return !pad.fingersNow().isEmpty() && !pad.fingersThen().isEmpty()
                && pad.fingersNow().getFirst().id() == pad.fingersThen().getFirst().id();
    }
}
