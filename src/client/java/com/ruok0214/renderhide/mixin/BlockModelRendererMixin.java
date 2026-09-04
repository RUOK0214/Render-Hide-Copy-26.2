/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.BlockAndTintGetter
 *  net.minecraft.BlockPos
 *  net.minecraft.Direction
 *  net.minecraft.BlockState
 *  net.minecraft.ModelBlockRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={ModelBlockRenderer.class})
abstract class BlockModelRendererMixin {
    BlockModelRendererMixin() {
    }

    @Inject(method={"shouldRenderFace"}, at={@At(value="HEAD")}, cancellable=true)
    private void renderhide$exposeBoundary(BlockAndTintGetter world, BlockState state,
            Direction direction, BlockPos neighborPos, CallbackInfoReturnable<Boolean> cir) {
        BlockPos currentPos = neighborPos.relative(direction.getOpposite());
        boolean currentAffected = RegionManager.affectsOcclusion(currentPos, state);
        if (world instanceof MovingBlockRenderState moving) {
            float opacity = ((MovingBlockOpacityAccess) moving).renderhide$getOpacity();
            if (!Float.isNaN(opacity)) {
                currentAffected = opacity < 1.0F;
            }
        }
        if (!currentAffected
                && RegionManager.isFullyHidden(
                        neighborPos, world.getBlockState(neighborPos))) {
            cir.setReturnValue(true);
        }
    }
}
