package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Correct lighting samples before vanilla blends the four vertex light values. */
@Mixin(targets = "net.minecraft.client.renderer.block.BlockModelLighter$Cache")
abstract class VanillaLightCacheMixin {
    @Inject(method = "getLightCoords", at = @At("RETURN"), cancellable = true)
    private void renderhide$sampleVirtualLight(BlockState state, BlockAndTintGetter world,
            BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!RegionManager.isVirtualLightAffected(pos)) return;
        int original = cir.getReturnValueI();
        int block = RegionManager.virtualLightLevel(LightLayer.BLOCK, pos, LightCoordsUtil.block(original));
        int sky = RegionManager.virtualLightLevel(LightLayer.SKY, pos, LightCoordsUtil.sky(original));
        cir.setReturnValue(LightCoordsUtil.pack(block, sky));
    }

    @Inject(method = "getShadeBrightness", at = @At("RETURN"), cancellable = true)
    private void renderhide$hiddenSampleDoesNotShade(BlockState state, BlockAndTintGetter world,
            BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (RegionManager.isVirtualLightAffected(pos) && RegionManager.affectsOcclusion(pos, state)) {
            cir.setReturnValue(1.0F);
        }
    }
}
