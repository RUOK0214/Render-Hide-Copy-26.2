/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1920
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2680
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.minecraft.class_1920;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
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
    protected class_1920 level;
    @Shadow
    protected class_2680 state;
    @Shadow
    protected class_2338 pos;

    SodiumBlockOcclusionMixin() {
    }

    @Inject(method={"shouldDrawSide"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void renderhide$showFaceBesideHiddenNeighbour(class_2350 class_23502, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        class_2338 class_23382 = this.pos.method_10093(class_23502);
        class_2680 class_26802 = this.level.method_8320(class_23382);
        if (!RegionManager.isHidden(this.pos, this.state) && RegionManager.isHidden(class_23382, class_26802)) {
            callbackInfoReturnable.setReturnValue((Object)Boolean.TRUE);
        }
    }
}


