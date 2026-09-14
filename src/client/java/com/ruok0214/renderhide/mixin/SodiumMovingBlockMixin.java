package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.frapi.render.NonTerrainBlockRenderContext", remap = false)
abstract class SodiumMovingBlockMixin {
    @Unique private float renderhide$movingOpacity = 1.0F;

    @Inject(method = "tesselateBlock", at = @At("HEAD"), remap = false)
    private void renderhide$captureMovingOpacity(QuadEmitter output, float x, float y, float z,
            BlockAndTintGetter level, BlockPos pos, BlockState state, BlockStateModel model,
            long seed, CallbackInfo ci) {
        this.renderhide$movingOpacity = 1.0F;
        if (level instanceof MovingBlockRenderState moving) {
            float stored = ((MovingBlockOpacityAccess) moving).renderhide$getOpacity();
            this.renderhide$movingOpacity = Float.isNaN(stored)
                    ? RegionManager.movingBlockRenderOpacity(
                            moving.blockState, moving.randomSeedPos, moving.blockPos)
                    : stored;
        }
    }

    @Inject(method = "transform", at = @At("HEAD"), cancellable = true, remap = false)
    private void renderhide$skipInvisibleMovingQuad(MutableQuadView quad,
            CallbackInfoReturnable<Boolean> cir) {
        if (this.renderhide$movingOpacity <= 0.0F) {
            cir.setReturnValue(false);
        }
    }

    // Run after Sodium tint and shade operations, before the emitter encodes vertices.
    @Inject(method = "transform", at = @At("RETURN"), remap = false)
    private void renderhide$applyMovingAlpha(MutableQuadView quad,
            CallbackInfoReturnable<Boolean> cir) {
        float opacity = this.renderhide$movingOpacity;
        if (!cir.getReturnValueZ() || opacity <= 0.0F || opacity >= 1.0F) {
            return;
        }
        for (int vertex = 0; vertex < 4; vertex++) {
            int color = quad.color(vertex);
            int alpha = Math.round((color >>> 24) * opacity);
            quad.color(vertex, (color & 0x00FFFFFF) | (alpha << 24));
        }
        quad.chunkLayer(ChunkSectionLayer.TRANSLUCENT);
    }

    @Inject(method = "tesselateBlock", at = @At("RETURN"), remap = false)
    private void renderhide$clearMovingOpacity(QuadEmitter output, float x, float y, float z,
            BlockAndTintGetter level, BlockPos pos, BlockState state, BlockStateModel model,
            long seed, CallbackInfo ci) {
        this.renderhide$movingOpacity = 1.0F;
    }
}
