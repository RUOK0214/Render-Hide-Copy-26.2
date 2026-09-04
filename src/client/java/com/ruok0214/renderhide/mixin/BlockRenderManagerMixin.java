package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.AlphaVertexConsumer;
import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import java.util.List;
import net.minecraft.class_10889;
import net.minecraft.class_11791;
import net.minecraft.class_1920;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3610;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/class_776", remap = false)
abstract class BlockRenderManagerMixin {
    @Unique
    private static boolean renderhide$isFullyHidden(class_1920 world, class_2338 suppliedPos,
            class_2680 state) {
        if (RegionManager.isFullyHidden(suppliedPos, state)) return true;
        if (world instanceof class_11791 moving) {
            if (moving.field_62245 != null && RegionManager.isFullyHidden(moving.field_62245, state)) return true;
            if (moving.field_62246 != null && RegionManager.isFullyHidden(moving.field_62246, state)) return true;
        }
        return false;
    }

    @Unique
    private static float renderhide$opacity(class_1920 world, class_2338 suppliedPos,
            class_2680 state) {
        // Piston models can use several different buffers during extension and
        // retraction. Apply the opacity where every model is actually rendered,
        // instead of wrapping only the first buffer request in the command renderer.
        if (world instanceof class_11791 moving) {
            float stored = ((MovingBlockOpacityAccess) moving).renderhide$getOpacity();
            if (!Float.isNaN(stored)) return stored;
            if ((moving.field_62245 != null && RegionManager.isGhostRendered(moving.field_62245, state))
                    || (moving.field_62246 != null && RegionManager.isGhostRendered(moving.field_62246, state))) {
                return RegionManager.hiddenBlockOpacity();
            }
        }
        return RegionManager.isGhostRendered(suppliedPos, state)
                ? RegionManager.hiddenBlockOpacity() : 1.0F;
    }

    @Inject(method = "method_3355", at = @At("HEAD"), cancellable = true)
    private void renderhide$skipBlock(class_2680 state, class_2338 pos, class_1920 world,
            class_4587 matrices, class_4588 vertices, boolean cull, List<class_10889> parts,
            CallbackInfo ci) {
        if (renderhide$isFullyHidden(world, pos, state)) ci.cancel();
    }

    @ModifyVariable(method = "method_3355", at = @At("HEAD"), argsOnly = true)
    private class_4588 renderhide$ghostBlock(class_4588 vertices, class_2680 state,
            class_2338 pos, class_1920 world) {
        float opacity = renderhide$opacity(world, pos, state);
        return opacity < 1.0F ? new AlphaVertexConsumer(vertices, opacity) : vertices;
    }

    @Inject(method = "method_3352", at = @At("HEAD"), cancellable = true)
    private void renderhide$skipFluid(class_2338 pos, class_1920 world, class_4588 vertices,
            class_2680 state, class_3610 fluid, CallbackInfo ci) {
        if (renderhide$isFullyHidden(world, pos, state)) ci.cancel();
    }

    @ModifyVariable(method = "method_3352", at = @At("HEAD"), argsOnly = true)
    private class_4588 renderhide$ghostFluid(class_4588 vertices, class_2338 pos,
            class_1920 world, class_4588 ignored, class_2680 state) {
        float opacity = renderhide$opacity(world, pos, state);
        return opacity < 1.0F ? new AlphaVertexConsumer(vertices, opacity) : vertices;
    }
}

