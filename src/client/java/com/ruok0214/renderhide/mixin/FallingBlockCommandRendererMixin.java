package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import net.minecraft.class_11661;
import net.minecraft.class_11791;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4597;
import net.minecraft.class_4696;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/class_11681", remap = false)
abstract class FallingBlockCommandRendererMixin {
    @Unique
    private static final ThreadLocal<class_11791> renderhide$currentMovingState = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<Float> renderhide$currentOpacity =
            ThreadLocal.withInitial(() -> 1.0F);

    @Redirect(
            method = "method_72998(Lnet/minecraft/class_11788;Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_776;Lnet/minecraft/class_4618;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/class_11661$class_11790;comp_4653()Lnet/minecraft/class_11791;")
    )
    private class_11791 renderhide$captureMovingState(class_11661.class_11790 command) {
        class_11791 state = command.comp_4653();
        renderhide$currentMovingState.set(state);
        renderhide$currentOpacity.set(1.0F);
        return state;
    }

    @Redirect(
            method = "method_72998(Lnet/minecraft/class_11788;Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_776;Lnet/minecraft/class_4618;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4696;method_29359(Lnet/minecraft/class_2680;)Lnet/minecraft/class_1921;")
    )
    private class_1921 renderhide$chooseMovingLayer(class_2680 state) {
        class_11791 moving = renderhide$currentMovingState.get();
        float opacity = moving == null ? 1.0F : renderhide$opacity(moving, state);
        renderhide$currentOpacity.set(opacity);
        return opacity < 1.0F ? class_12249.method_75977() : class_4696.method_29359(state);
    }

    @Inject(
            method = "method_72998(Lnet/minecraft/class_11788;Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_776;Lnet/minecraft/class_4618;)V",
            at = @At("RETURN")
    )
    private void renderhide$clearMovingState(CallbackInfo ci) {
        renderhide$currentMovingState.remove();
        renderhide$currentOpacity.remove();
    }

    @Unique
    private static float renderhide$opacity(class_11791 moving, class_2680 state) {
        float stored = ((MovingBlockOpacityAccess) moving).renderhide$getOpacity();
        if (!Float.isNaN(stored)) return stored;
        if ((moving.field_62245 != null && RegionManager.isFullyHidden(moving.field_62245, state))
                || (moving.field_62246 != null && RegionManager.isFullyHidden(moving.field_62246, state))) return 0.0F;
        if ((moving.field_62245 != null && RegionManager.isGhostRendered(moving.field_62245, state))
                || (moving.field_62246 != null && RegionManager.isGhostRendered(moving.field_62246, state))) {
            return RegionManager.hiddenBlockOpacity();
        }
        return 1.0F;
    }
}

