package com.ruok0214.renderhide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.feature.MovingBlockFeatureRenderer;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovingBlockFeatureRenderer.class)
abstract class FallingBlockCommandRendererMixin {
    @Unique
    private final ThreadLocal<MovingBlockRenderState> renderhide$currentMovingState = new ThreadLocal<>();

    @WrapOperation(
            method = "buildGroup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/MovingBlockFeatureRenderer$Submit;movingBlockRenderState()Lnet/minecraft/client/renderer/block/MovingBlockRenderState;"
            )
    )
    private MovingBlockRenderState renderhide$captureMovingState(
            MovingBlockFeatureRenderer.Submit submit, Operation<MovingBlockRenderState> original) {
        MovingBlockRenderState state = original.call(submit);
        this.renderhide$currentMovingState.set(state);
        return state;
    }

    @ModifyExpressionValue(
            method = "buildGroup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;forceOpaque(ZLnet/minecraft/world/level/block/state/BlockState;)Z"
            )
    )
    private boolean renderhide$ghostIsNotForcedOpaque(boolean original) {
        float opacity = renderhide$currentOpacity();
        return opacity > 0.0F && opacity < 1.0F ? false : original;
    }

    @ModifyVariable(method = "putBakedQuad", at = @At("HEAD"), argsOnly = true)
    private ChunkSectionLayer renderhide$useTranslucentLayer(ChunkSectionLayer original) {
        float opacity = renderhide$currentOpacity();
        return opacity > 0.0F && opacity < 1.0F ? ChunkSectionLayer.TRANSLUCENT : original;
    }

    @ModifyVariable(method = "putBakedQuad", at = @At("HEAD"), argsOnly = true)
    private QuadInstance renderhide$applyMovingAlpha(QuadInstance instance) {
        float opacity = renderhide$currentOpacity();
        if (opacity > 0.0F && opacity < 1.0F) {
            instance.multiplyColor(ARGB.white(opacity));
        }
        return instance;
    }

    @Inject(method = "buildGroup", at = @At("RETURN"))
    private void renderhide$clearMovingState(CallbackInfo ci) {
        this.renderhide$currentMovingState.remove();
    }

    @Unique
    private float renderhide$currentOpacity() {
        MovingBlockRenderState moving = this.renderhide$currentMovingState.get();
        if (moving == null) {
            return 1.0F;
        }
        float stored = ((MovingBlockOpacityAccess) moving).renderhide$getOpacity();
        if (!Float.isNaN(stored)) {
            return stored;
        }
        return RegionManager.movingBlockRenderOpacity(
                moving.blockState, moving.randomSeedPos, moving.blockPos);
    }
}
