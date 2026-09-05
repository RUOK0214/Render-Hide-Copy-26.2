package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.fabricmc.fabric.impl.client.indigo.renderer.render.AltModelBlockRendererImpl", remap = false)
abstract class IndigoTerrainRendererMixin {
    @Shadow
    private BlockAndTintGetter level;

    @Shadow
    private BlockPos pos;

    @Shadow
    private BlockState blockState;

    @Inject(method = "transform", at = @At("HEAD"), cancellable = true, remap = false)
    private void renderhide$skipHiddenQuad(MutableQuadView quad,
            CallbackInfoReturnable<Boolean> cir) {
        if (this.pos != null && this.blockState != null
                && RegionManager.blockRenderMode(this.pos, this.blockState)
                        == RegionManager.BlockRenderMode.SKIP) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "transform", at = @At("RETURN"), cancellable = true, remap = false)
    private void renderhide$makeQuadTranslucent(MutableQuadView quad,
            CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || this.pos == null || this.blockState == null
                || RegionManager.blockRenderMode(this.pos, this.blockState)
                        != RegionManager.BlockRenderMode.TRANSLUCENT) {
            return;
        }

        int alpha = ARGB.white(RegionManager.hiddenBlockOpacity());
        for (int vertex = 0; vertex < 4; vertex++) {
            quad.color(vertex, ARGB.multiply(quad.color(vertex), alpha));
        }
        quad.chunkLayer(ChunkSectionLayer.TRANSLUCENT);
    }

    @Inject(method = "shouldCullFace", at = @At("HEAD"), cancellable = true, remap = false)
    private void renderhide$exposeFaceBesideHiddenNeighbour(
            net.minecraft.core.Direction direction,
            CallbackInfoReturnable<Boolean> cir) {
        if (direction == null || this.level == null || this.pos == null
                || this.blockState == null) {
            return;
        }

        BlockPos neighbourPos = this.pos.relative(direction);
        RegionManager.BlockRenderMode currentMode = RegionManager.blockRenderMode(
                this.pos, this.blockState);
        RegionManager.BlockRenderMode neighbourMode = RegionManager.blockRenderMode(
                neighbourPos, this.level.getBlockState(neighbourPos));
        if (RegionManager.shouldExposeFace(currentMode, neighbourMode)) {
            cir.setReturnValue(false);
        }
    }
}
