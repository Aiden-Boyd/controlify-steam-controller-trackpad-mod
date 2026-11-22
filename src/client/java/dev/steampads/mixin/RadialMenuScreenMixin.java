package dev.steampads.mixin;

import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.gui.screen.RadialMenuScreen;
import dev.steampads.TrackpadRadialScreen;
import org.joml.Vector2fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RadialMenuScreen.class, remap = false)
abstract class RadialMenuScreenMixin {
    @Shadow private int idleTicks;
    @Shadow @Final private int idleTicksTimeout;

    @Redirect(method = "onControllerInput", at = @At(value = "INVOKE",
            target = "Ldev/isxander/controlify/api/bind/InputBinding;digitalNow()Z"))
    private boolean steamtrackpads$holdWhileTouched(InputBinding binding) {
        if ((Object) this instanceof TrackpadRadialScreen radial) {
            return radial.position() != null;
        }
        return binding.digitalNow();
    }

    @ModifyVariable(method = "onControllerInput", at = @At("STORE"), index = 2)
    private float steamtrackpads$horizontal(float original) {
        if ((Object) this instanceof TrackpadRadialScreen radial) {
            Vector2fc position = radial.position();
            if (position == null || steamtrackpads$insideDeadzone(radial, position)) {
                idleTicks = idleTicksTimeout;
                return 0;
            }
            return (position.x() - 0.5f) / steamtrackpads$largestAxis(position);
        }
        return original;
    }

    @ModifyVariable(method = "onControllerInput", at = @At("STORE"), index = 3)
    private float steamtrackpads$vertical(float original) {
        if ((Object) this instanceof TrackpadRadialScreen radial) {
            Vector2fc position = radial.position();
            if (position == null || steamtrackpads$insideDeadzone(radial, position)) {
                return 0;
            }
            return (position.y() - 0.5f) / steamtrackpads$largestAxis(position);
        }
        return original;
    }

    @ModifyVariable(method = "onControllerInput", at = @At("STORE"), index = 4)
    private float steamtrackpads$normalizedThreshold(float original) {
        return (Object) this instanceof TrackpadRadialScreen ? 0.5f : original;
    }

    @Unique
    private static boolean steamtrackpads$insideDeadzone(TrackpadRadialScreen radial, Vector2fc position) {
        float threshold = radial.owner().input().orElseThrow().settings().buttonActivationThreshold;
        double radius = Math.hypot(position.x() - 0.5f, position.y() - 0.5f);
        return radius <= Math.max(0.1f, threshold * 0.4f);
    }

    @Unique
    private static float steamtrackpads$largestAxis(Vector2fc position) {
        return Math.max(Math.abs(position.x() - 0.5f), Math.abs(position.y() - 0.5f));
    }
}
