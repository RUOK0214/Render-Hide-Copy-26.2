package com.ruok0214.renderhide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubmitNodeCollection.class)
abstract class MovingBlockSubmissionMixin {
    @Unique
    private MovingBlockRenderState renderhide$currentMovingState;

    @Inject(method = "submitMovingBlock", at = @At("HEAD"), cancellable = true)
    private void renderhide$captureMovingState(PoseStack poseStack, MovingBlockRenderState state,
            int outlineColor, CallbackInfo ci) {
        this.renderhide$currentMovingState = state;
        if (renderhide$opacity(state) <= 0.0F) {
            this.renderhide$currentMovingState = null;
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "submitMovingBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;hasMaterialFlag(I)Z"
            )
    )
    private boolean renderhide$submitGhostToTranslucentPhase(boolean original) {
        if (this.renderhide$currentMovingState == null) {
            return original;
        }
        float opacity = renderhide$opacity(this.renderhide$currentMovingState);
        return (opacity > 0.0F && opacity < 1.0F) || original;
    }

    @Inject(method = "submitMovingBlock", at = @At("RETURN"))
    private void renderhide$clearMovingState(PoseStack poseStack, MovingBlockRenderState state,
            int outlineColor, CallbackInfo ci) {
        this.renderhide$currentMovingState = null;
    }

    @Unique
    private static float renderhide$opacity(MovingBlockRenderState state) {
        float stored = ((MovingBlockOpacityAccess) state).renderhide$getOpacity();
        return !Float.isNaN(stored)
                ? stored
                : RegionManager.movingBlockRenderOpacity(
                        state.blockState, state.randomSeedPos, state.blockPos);
    }
}
