/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl
 *  net.minecraft.class_1087
 *  net.minecraft.class_11515
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2680
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
import net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl;
import net.minecraft.class_1087;
import net.minecraft.class_11515;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
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
    private class_2680 renderhide$currentState;
    @Unique
    private class_2338 renderhide$currentPos;

    SodiumGhostBlockMixin() {
    }

    @Inject(method={"renderModel"}, at={@At(value="HEAD")}, remap=false)
    private void renderhide$captureBlock(class_1087 class_10872, class_2680 class_26802, class_2338 class_23382, class_2338 class_23383, CallbackInfo callbackInfo) {
        this.renderhide$currentState = class_26802;
        this.renderhide$currentPos = new class_2338(class_23382.method_10263(), class_23382.method_10264(), class_23382.method_10260());
    }

    @Inject(method={"processQuad"}, at={@At(value="HEAD")}, remap=false)
    private void renderhide$makeQuadTranslucent(MutableQuadViewImpl mutableQuadViewImpl, CallbackInfo callbackInfo) {
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

    @Redirect(method={"processQuad"}, at=@At(value="INVOKE", target="Lnet/caffeinemc/mods/sodium/client/render/model/MutableQuadViewImpl;getRenderType()Lnet/minecraft/class_11515;"), remap=false)
    private class_11515 renderhide$useTranslucentLayer(MutableQuadViewImpl mutableQuadViewImpl) {
        if (this.renderhide$currentPos != null && this.renderhide$currentState != null && RegionManager.isGhostRendered(this.renderhide$currentPos, this.renderhide$currentState)) {
            return class_11515.field_60926;
        }
        return mutableQuadViewImpl.getRenderType();
    }

    @Redirect(method={"processQuad"}, at=@At(value="INVOKE", target="Lnet/caffeinemc/mods/sodium/client/render/model/MutableQuadViewImpl;getCullFace()Lnet/minecraft/class_2350;"), remap=false, require=0)
    private class_2350 renderhide$exposeFacesBesideGhostBlocks(MutableQuadViewImpl mutableQuadViewImpl) {
        class_2350 class_23502 = mutableQuadViewImpl.getCullFace();
        return class_23502 != null && this.renderhide$currentPos != null && RegionManager.isInsideActiveRegion(this.renderhide$currentPos) ? null : class_23502;
    }
}

