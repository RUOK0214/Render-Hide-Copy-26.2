package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import net.minecraft.client.renderer.blockentity.state.PistonHeadRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/renderer/blockentity/PistonHeadRenderer", remap = false)
abstract class PistonBlockEntityRendererMixin {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/level/block/piston/PistonMovingBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/PistonHeadRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At("RETURN")
    )
    private void renderhide$filterCarriedBlock(PistonMovingBlockEntity piston, PistonHeadRenderState renderState,
            float tickProgress, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay,
            CallbackInfo ci) {
        BlockPos pos = piston.getBlockPos();
        BlockState carriedState = piston.getMovedState();

        // Keep the accumulated pre-opacity piston rules as the single source of
        // truth. They resolve moving_piston back to its carried block, include
        // global/region filters, and link normal/sticky piston heads correctly.
        boolean visible = RegionManager.shouldRenderMovingPistonBlock(pos, carriedState);
        float opacity = visible ? 1.0F : RegionManager.hiddenBlockOpacity();

        if (renderState.block != null) {
            ((MovingBlockOpacityAccess) renderState.block).renderhide$setOpacity(opacity);
            if (opacity <= 0.0F) renderState.block = null;
        }
        if (renderState.base != null) {
            ((MovingBlockOpacityAccess) renderState.base).renderhide$setOpacity(opacity);
            if (opacity <= 0.0F) renderState.base = null;
        }
    }
}

