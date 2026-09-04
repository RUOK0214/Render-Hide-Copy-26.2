/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.LightLayer
 *  net.minecraft.BlockPos
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
import net.minecraft.world.level.LightLayer;
import net.minecraft.core.BlockPos;
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

    @Inject(method={"getBrightness"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private void renderhide$sampleVirtualLight(LightLayer type, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((Object)RegionManager.virtualLightLevel(type, pos, cir.getReturnValueI()));
    }
}


