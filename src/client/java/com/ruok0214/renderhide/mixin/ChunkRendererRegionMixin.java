/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.Blocks
 *  net.minecraft.BlockPos
 *  net.minecraft.BlockState
 *  net.minecraft.RenderSectionRegion
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={RenderSectionRegion.class})
abstract class ChunkRendererRegionMixin {
    ChunkRendererRegionMixin() {
    }

    @Inject(method={"getBlockState"}, at={@At(value="RETURN")}, cancellable=true)
    private void renderhide$treatHiddenBlocksAsAir(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (RegionManager.isFullyHidden(pos, (BlockState)cir.getReturnValue())) {
            cir.setReturnValue((Object)Blocks.AIR.defaultBlockState());
        }
    }
}

