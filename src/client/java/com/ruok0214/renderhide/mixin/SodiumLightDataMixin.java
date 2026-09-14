package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess", remap = false)
abstract class SodiumLightDataMixin {
    @Shadow protected BlockAndTintGetter level;

    @Inject(method = "compute(III)I", at = @At("RETURN"), cancellable = true, remap = false)
    private void renderhide$sampleHiddenBlockLighting(int x, int y, int z,
            CallbackInfoReturnable<Integer> cir) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!RegionManager.affectsOcclusion(pos, this.level.getBlockState(pos))) {
            return;
        }

        int original = cir.getReturnValue();
        // Opaque samples normally store zero light. Recover actual light, including
        // the optional virtual-light correction provided by LevelSlice.
        int block = Math.max(LightDataAccess.unpackBL(original),
                this.level.getBrightness(LightLayer.BLOCK, pos));
        int sky = Math.max(LightDataAccess.unpackSL(original),
                this.level.getBrightness(LightLayer.SKY, pos));

        // Only hidden samples stop occluding light. Keep geometry and emission,
        // and let Sodium calculate visible-neighbor AO and directional shading.
        cir.setReturnValue(LightDataAccess.packBL(block)
                | LightDataAccess.packSL(sky)
                | LightDataAccess.packLU(LightDataAccess.unpackLU(original))
                | LightDataAccess.packAO(1.0f)
                | LightDataAccess.packEM(LightDataAccess.unpackEM(original))
                | LightDataAccess.packOP(false)
                | LightDataAccess.packFO(false)
                | LightDataAccess.packFC(LightDataAccess.unpackFC(original)));
    }
}
