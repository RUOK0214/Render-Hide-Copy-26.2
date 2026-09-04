/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1944
 *  net.minecraft.class_2338
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Pseudo
@Mixin(targets={"net/caffeinemc/mods/sodium/client/world/LevelSlice"}, remap=false)
abstract class SodiumVirtualLightMixin {
    SodiumVirtualLightMixin() {
    }

    @Inject(method={"method_8314"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private void renderhide$sampleVirtualLight(class_1944 type, class_2338 pos, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((Object)RegionManager.virtualLightLevel(type, pos, cir.getReturnValueI()));
    }
}


