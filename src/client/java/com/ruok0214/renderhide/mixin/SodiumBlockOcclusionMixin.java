/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.BlockAndTintGetter
 *  net.minecraft.BlockPos
 *  net.minecraft.Direction
 *  net.minecraft.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"net/caffeinemc/mods/sodium/client/render/model/AbstractBlockRenderContext"}, remap=false)
abstract class SodiumBlockOcclusionMixin {
    @Shadow
    protected BlockAndTintGetter level;
    @Shadow
    protected BlockState state;
    @Shadow
    protected BlockPos pos;

    SodiumBlockOcclusionMixin() {
    }

    @Inject(method={"shouldDrawSide"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void renderhide$showFaceBesideHiddenNeighbour(Direction class_23502, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        BlockPos class_23382 = this.pos.relative(class_23502);
        BlockState class_26802 = this.level.getBlockState(class_23382);
        if (!RegionManager.affectsOcclusion(this.pos, this.state)
                && RegionManager.isFullyHidden(class_23382, class_26802)) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }
}
