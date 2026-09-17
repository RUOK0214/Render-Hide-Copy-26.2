package com.ruok0214.renderhide.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ruok0214.renderhide.BlockEntityOpacityAccess;
import com.ruok0214.renderhide.EntityAlphaSubmitNodeCollector;
import com.ruok0214.renderhide.RegionManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.PistonHeadRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
abstract class BlockEntityRenderDispatcherMixin {
    @Inject(method = "tryExtractRenderState", at = @At("RETURN"))
    private void renderhide$storeBlockEntityOpacity(BlockEntity blockEntity,
            float tickProgress, ModelFeatureRenderer.CrumblingOverlay overlay,
            boolean offScreen, CallbackInfoReturnable<BlockEntityRenderState> cir) {
        BlockEntityRenderState state = cir.getReturnValue();
        if (state == null) {
            return;
        }
        // Moving pistons carry separate opacity for their base and moved block.
        // Applying the outer block-entity alpha too would multiply it twice.
        float opacity = state instanceof PistonHeadRenderState ? 1.0F
                : RegionManager.blockRenderOpacity(
                        blockEntity.getBlockPos(), blockEntity.getBlockState());
        ((BlockEntityOpacityAccess) state).renderhide$setBlockEntityOpacity(opacity);
    }

    @WrapOperation(method = "submit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;submit(Lnet/minecraft/client/renderer/blockentity/state/BlockEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"))
    private <S extends BlockEntityRenderState> void renderhide$submitBlockEntityWithOpacity(
            BlockEntityRenderer<?, S> renderer, S state, PoseStack poseStack,
            SubmitNodeCollector collector, CameraRenderState cameraState,
            Operation<Void> original) {
        float opacity = ((BlockEntityOpacityAccess) state).renderhide$getBlockEntityOpacity();
        if (opacity <= 0.0F) {
            return;
        }
        SubmitNodeCollector adjusted = opacity < 1.0F
                ? new EntityAlphaSubmitNodeCollector(collector, opacity) : collector;
        original.call(renderer, state, poseStack, adjusted, cameraState);
    }
}
