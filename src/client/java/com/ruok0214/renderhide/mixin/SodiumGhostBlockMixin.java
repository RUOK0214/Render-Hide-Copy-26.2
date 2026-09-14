/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl
 *  net.minecraft.BlockStateModel
 *  net.minecraft.ChunkSectionLayer
 *  net.minecraft.BlockPos
 *  net.minecraft.Direction
 *  net.minecraft.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"net/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer"}, remap=false)
abstract class SodiumGhostBlockMixin {
    @Unique
    private BlockState renderhide$currentState;
    @Unique
    private BlockPos renderhide$currentPos;
    SodiumGhostBlockMixin() {
    }

    @Inject(method={"renderModel"}, at={@At(value="HEAD")}, remap=false)
    private void renderhide$captureBlock(BlockStateModel class_10872, BlockState class_26802, BlockPos class_23382, BlockPos class_23383, CallbackInfo callbackInfo) {
        this.renderhide$currentState = class_26802;
        this.renderhide$currentPos = new BlockPos(class_23382.getX(), class_23382.getY(), class_23382.getZ());
    }

    @Inject(method={"processQuad"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void renderhide$allowTranslucentLayer(MutableQuadViewImpl mutableQuadViewImpl, CallbackInfo callbackInfo) {
        if (this.renderhide$currentPos == null || this.renderhide$currentState == null) {
            return;
        }
        RegionManager.BlockRenderMode mode = RegionManager.blockRenderMode(
                this.renderhide$currentPos, this.renderhide$currentState);
        if (mode == RegionManager.BlockRenderMode.SKIP) {
            callbackInfo.cancel();
        }
    }

    // Apply alpha after tint/shading, immediately before vertex encoding.
    @Inject(method = "bufferQuad", at = @At("HEAD"), remap = false)
    private void renderhide$makeQuadTranslucent(MutableQuadViewImpl mutableQuadViewImpl,
            float[] brightnesses, Material material, CallbackInfo callbackInfo) {
        if (this.renderhide$currentPos == null || this.renderhide$currentState == null || !RegionManager.isGhostRendered(this.renderhide$currentPos, this.renderhide$currentState)) {
            return;
        }
        for (int i = 0; i < 4; ++i) {
            int n2 = mutableQuadViewImpl.baseColor(i);
            int originalAlpha = n2 >>> 24 & 0xFF;
            int adjustedAlpha = Math.max(0, Math.min(255,
                    Math.round(originalAlpha * RegionManager.hiddenBlockOpacity())));
            mutableQuadViewImpl.setColor(i, n2 & 0xFFFFFF | adjustedAlpha << 24);
        }
    }

    // Replace the final material, after Sodium's inherited forceOpaque decision.
    @Redirect(method = "processQuad", at = @At(value = "INVOKE",
            target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/DefaultMaterials;forChunkLayer(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayer;)Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;"), remap = false)
    private Material renderhide$useTranslucentLayer(ChunkSectionLayer layer) {
        if (this.renderhide$currentPos != null && this.renderhide$currentState != null
                && RegionManager.isGhostRendered(this.renderhide$currentPos, this.renderhide$currentState)) {
            return DefaultMaterials.forChunkLayer(ChunkSectionLayer.TRANSLUCENT);
        }
        return DefaultMaterials.forChunkLayer(layer);
    }

    @Inject(method={"renderModel"}, at={@At(value="RETURN")}, remap=false)
    private void renderhide$clearBlock(BlockStateModel model, BlockState state, BlockPos pos,
            BlockPos modelOffset, CallbackInfo ci) {
        this.renderhide$currentState = null;
        this.renderhide$currentPos = null;
    }
}
