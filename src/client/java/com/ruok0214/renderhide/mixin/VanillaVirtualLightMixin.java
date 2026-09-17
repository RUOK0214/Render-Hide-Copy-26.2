package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Covers Indigo's direct light lookups, without modifying world light or Sodium. */
@Mixin(LightCoordsUtil.class)
abstract class VanillaVirtualLightMixin {
    @Inject(method = "getLightCoords(Lnet/minecraft/util/LightCoordsUtil$BrightnessGetter;Lnet/minecraft/world/level/BlockAndLightGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
            at = @At("RETURN"), cancellable = true)
    private static void renderhide$virtualTerrainLight(LightCoordsUtil.BrightnessGetter getter,
            BlockAndLightGetter world, BlockState state, BlockPos pos,
            CallbackInfoReturnable<Integer> cir) {
        if (!(world instanceof RenderSectionRegion) || !RegionManager.isVirtualLightAffected(pos)) return;
        int original = cir.getReturnValueI();
        cir.setReturnValue(LightCoordsUtil.pack(
                RegionManager.virtualLightLevel(LightLayer.BLOCK, pos, LightCoordsUtil.block(original)),
                RegionManager.virtualLightLevel(LightLayer.SKY, pos, LightCoordsUtil.sky(original))));
    }
}
