package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.light.LightPipeline;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadViewMutable;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer", remap = false)
abstract class SodiumDefaultFluidRendererMixin {
    @Shadow @Final private int[] quadColors;

    // Runs once per face, before front/back copies are written to the mesh.
    // ABGR and ARGB both store alpha in the high byte; preserve the native tint.
    @Inject(method = "updateQuad", at = @At("RETURN"), remap = false)
    private void renderhide$applyDefaultFluidAlpha(ModelQuadViewMutable quad, LevelSlice level,
            BlockPos pos, LightPipeline lighter, Direction dir, ModelQuadFacing facing,
            float brightness, ColorProvider<FluidState> colorProvider, FluidState fluidState,
            CallbackInfo ci) {
        if (!RegionManager.isGhostRendered(pos, level.getBlockState(pos))) return;
        float opacity = RegionManager.hiddenBlockOpacity();
        for (int i = 0; i < 4; i++) {
            int color = this.quadColors[i];
            int alpha = Math.round((color >>> 24) * opacity);
            this.quadColors[i] = (color & 0x00FFFFFF) | (alpha << 24);
        }
    }
}
