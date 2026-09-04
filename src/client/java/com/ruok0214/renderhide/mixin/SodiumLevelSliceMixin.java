/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Desc
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import com.ruok0214.renderhide.RenderHideClient;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Pseudo
@Mixin(targets={"net/caffeinemc/mods/sodium/client/world/LevelSlice"}, remap=false)
abstract class SodiumLevelSliceMixin {
    private static final AtomicBoolean RENDERHIDE$LOGGED = new AtomicBoolean();

    SodiumLevelSliceMixin() {
    }

    @Inject(target={@Desc(ret=class_2680.class, args={int.class, int.class, int.class}, value="getBlockState")}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private void renderhide$treatHiddenBlocksAsAir(int x, int y, int z, CallbackInfoReturnable<class_2680> cir) {
        class_2338 pos = new class_2338(x, y, z);
        if (RegionManager.isFullyHidden(pos, (class_2680)cir.getReturnValue())) {
            if (RENDERHIDE$LOGGED.compareAndSet(false, true)) {
                RenderHideClient.LOGGER.info("Sodium mesh filter active at {}, {}, {}", new Object[]{x, y, z});
            }
            cir.setReturnValue((Object)class_2246.field_10124.method_9564());
        }
    }
}


