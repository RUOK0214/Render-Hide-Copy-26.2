package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import net.minecraft.class_11968;
import net.minecraft.class_11683;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2669;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/class_835", remap = false)
abstract class PistonBlockEntityRendererMixin {
    @Inject(
            method = "method_74380(Lnet/minecraft/class_2669;Lnet/minecraft/class_11968;FLnet/minecraft/class_243;Lnet/minecraft/class_11683$class_11792;)V",
            at = @At("RETURN")
    )
    private void renderhide$filterCarriedBlock(class_2669 piston, class_11968 renderState,
            float tickProgress, class_243 cameraPos, class_11683.class_11792 crumblingOverlay,
            CallbackInfo ci) {
        class_2338 pos = piston.method_11016();
        class_2680 carriedState = piston.method_11495();

        // Keep the accumulated pre-opacity piston rules as the single source of
        // truth. They resolve moving_piston back to its carried block, include
        // global/region filters, and link normal/sticky piston heads correctly.
        boolean visible = RegionManager.shouldRenderMovingPistonBlock(pos, carriedState);
        float opacity = visible ? 1.0F : RegionManager.hiddenBlockOpacity();

        if (renderState.field_62729 != null) {
            ((MovingBlockOpacityAccess) renderState.field_62729).renderhide$setOpacity(opacity);
            if (opacity <= 0.0F) renderState.field_62729 = null;
        }
        if (renderState.field_62730 != null) {
            ((MovingBlockOpacityAccess) renderState.field_62730).renderhide$setOpacity(opacity);
            if (opacity <= 0.0F) renderState.field_62730 = null;
        }
    }
}

