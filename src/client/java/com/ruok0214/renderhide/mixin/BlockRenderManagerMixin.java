package com.ruok0214.renderhide.mixin;

import com.mojang.blaze3d.vertex.QuadInstance;
import com.ruok0214.renderhide.RegionManager;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelLighter;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockModelLighter.class)
abstract class BlockRenderManagerMixin {
    @Inject(method = "prepareQuadAmbientOcclusion", at = @At("RETURN"))
    private void renderhide$applyAmbientVirtualLight(BlockAndTintGetter world, BlockState state,
            BlockPos pos, BakedQuad quad, QuadInstance instance, CallbackInfo ci) {
        if (!RegionManager.isVirtualLightAffected(pos, quad.direction())) {
            return;
        }

        float shade = quad.materialInfo().shade()
                ? world.cardinalLighting().byFace(quad.direction())
                : world.cardinalLighting().up();
        instance.setColor(ARGB.gray(shade));
        renderhide$applyLightmap(pos, quad.direction(), instance);
    }

    @Inject(method = "prepareQuadFlat", at = @At("RETURN"))
    private void renderhide$applyFlatVirtualLight(BlockAndTintGetter world, BlockState state,
            BlockPos pos, int suppliedLight, BakedQuad quad, QuadInstance instance, CallbackInfo ci) {
        renderhide$applyLightmap(pos, quad.direction(), instance);
    }

    private static void renderhide$applyLightmap(BlockPos pos, Direction face, QuadInstance instance) {
        if (!RegionManager.isVirtualLightAffected(pos, face)) {
            return;
        }
        for (int vertex = 0; vertex < 4; vertex++) {
            int original = instance.getLightCoords(vertex);
            int originalBlock = LightCoordsUtil.smoothBlock(original) >> 4;
            int originalSky = LightCoordsUtil.smoothSky(original) >> 4;
            int block = RegionManager.virtualLightLevel(LightLayer.BLOCK, pos, face, originalBlock);
            int sky = RegionManager.virtualLightLevel(LightLayer.SKY, pos, face, originalSky);
            instance.setLightCoords(vertex, LightCoordsUtil.smoothPack(
                    Math.max(LightCoordsUtil.smoothBlock(original), block << 4),
                    Math.max(LightCoordsUtil.smoothSky(original), sky << 4)));
        }
    }
}
