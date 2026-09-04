package com.ruok0214.renderhide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ruok0214.renderhide.AlphaVertexConsumer;
import com.ruok0214.renderhide.RegionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net/caffeinemc/mods/sodium/fabric/render/FluidRendererImpl", remap = false)
abstract class SodiumFluidRendererMixin {
    @Unique
    private static final ThreadLocal<RegionManager.BlockRenderMode> renderhide$currentMode = new ThreadLocal<>();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    private void renderhide$captureFluid(LevelSlice level, BlockState state, FluidState fluidState,
            BlockPos pos, BlockPos modelOffset, TranslucentGeometryCollector collector,
            ChunkBuildBuffers buffers, CallbackInfo ci) {
        RegionManager.BlockRenderMode mode = RegionManager.blockRenderMode(pos, state);
        if (mode == RegionManager.BlockRenderMode.SKIP) {
            renderhide$currentMode.remove();
            ci.cancel();
            return;
        }
        renderhide$currentMode.set(mode);
    }

    @ModifyExpressionValue(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/FluidModel;layer()Lnet/minecraft/client/renderer/chunk/ChunkSectionLayer;"
            ),
            remap = false
    )
    private ChunkSectionLayer renderhide$useTranslucentLayer(ChunkSectionLayer original) {
        return renderhide$currentMode.get() == RegionManager.BlockRenderMode.TRANSLUCENT
                ? ChunkSectionLayer.TRANSLUCENT
                : original;
    }

    @Inject(method = "lambda$render$0", at = @At("RETURN"), cancellable = true, remap = false)
    private static void renderhide$applyFluidAlpha(ChunkModelBuilder builder,
            TranslucentGeometryCollector collector, ChunkSectionLayer layer,
            CallbackInfoReturnable<VertexConsumer> cir) {
        if (renderhide$currentMode.get() == RegionManager.BlockRenderMode.TRANSLUCENT) {
            cir.setReturnValue(new AlphaVertexConsumer(
                    cir.getReturnValue(), RegionManager.hiddenBlockOpacity()));
        }
    }

    @Inject(method = "render", at = @At("RETURN"), remap = false)
    private void renderhide$clearFluid(LevelSlice level, BlockState state, FluidState fluidState,
            BlockPos pos, BlockPos modelOffset, TranslucentGeometryCollector collector,
            ChunkBuildBuffers buffers, CallbackInfo ci) {
        renderhide$currentMode.remove();
    }
}
