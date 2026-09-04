/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.model.light.data.QuadLightData
 *  net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.BlockPos
 *  net.minecraft.Direction
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.caffeinemc.mods.sodium.client.model.light.data.QuadLightData;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Pseudo
@Mixin(targets={"net/caffeinemc/mods/sodium/client/model/light/flat/FlatLightPipeline", "net/caffeinemc/mods/sodium/client/model/light/smooth/SmoothLightPipeline"}, remap=false)
abstract class SodiumLightPipelineMixin {
    private static final int MIN_LIGHT_COORDINATE = 192;

    SodiumLightPipelineMixin() {
    }

    @Inject(method={"calculate"}, at={@At(value="RETURN")}, remap=false)
    private void renderhide$improveVisibleBlockLighting(ModelQuadView quad, BlockPos pos, QuadLightData out, Direction cullFace, Direction lightFace, boolean shade, boolean enhanced, CallbackInfo ci) {
        if (!RegionManager.shouldImproveVisibleBlockLighting(pos)) {
            return;
        }
        for (int i = 0; i < out.br.length; ++i) {
            out.br[i] = 1.0f;
            int packed = out.lm[i];
            int block = Math.max(packed & 0xFFFF, 192);
            int sky = Math.max(packed >>> 16 & 0xFFFF, 192);
            out.lm[i] = sky << 16 | block;
        }
    }
}


