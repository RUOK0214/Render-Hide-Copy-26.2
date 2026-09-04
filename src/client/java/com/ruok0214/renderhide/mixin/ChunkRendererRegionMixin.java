/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_853
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_853;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_853.class})
abstract class ChunkRendererRegionMixin {
    ChunkRendererRegionMixin() {
    }

    @Inject(method={"method_8320"}, at={@At(value="RETURN")}, cancellable=true)
    private void renderhide$treatHiddenBlocksAsAir(class_2338 pos, CallbackInfoReturnable<class_2680> cir) {
        if (RegionManager.isFullyHidden(pos, (class_2680)cir.getReturnValue())) {
            cir.setReturnValue((Object)class_2246.field_10124.method_9564());
        }
    }
}


