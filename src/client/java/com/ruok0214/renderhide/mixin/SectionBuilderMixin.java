package com.ruok0214.renderhide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ruok0214.renderhide.AlphaVertexConsumer;
import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(SectionCompiler.class)
abstract class SectionBuilderMixin {
    private static final ThreadLocal<RegionManager.BlockRenderMode> renderhide$currentMode = new ThreadLocal<>();

    @WrapOperation(
            method = "compile",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private BlockState renderhide$captureCurrentBlock(RenderSectionRegion world, BlockPos pos,
            Operation<BlockState> original) {
        BlockState state = original.call(world, pos);
        renderhide$currentMode.set(RegionManager.blockRenderMode(pos, state));
        return state;
    }

    @WrapOperation(
            method = "compile",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isSolidRender()Z"
            )
    )
    private boolean renderhide$ghostDoesNotOcclude(BlockState state, Operation<Boolean> original) {
        return renderhide$currentMode.get() != RegionManager.BlockRenderMode.NORMAL
                ? false
                : original.call(state);
    }

    @ModifyExpressionValue(
            method = "compile",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;forceOpaque(ZLnet/minecraft/world/level/block/state/BlockState;)Z"
            )
    )
    private boolean renderhide$ghostIsNotForcedOpaque(boolean original) {
        return renderhide$currentMode.get() != RegionManager.BlockRenderMode.NORMAL ? false : original;
    }

    @ModifyVariable(method = "getOrBeginLayer", at = @At("HEAD"), argsOnly = true)
    private ChunkSectionLayer renderhide$useTranslucentLayer(ChunkSectionLayer original) {
        return renderhide$currentMode.get() == RegionManager.BlockRenderMode.TRANSLUCENT
                ? ChunkSectionLayer.TRANSLUCENT : original;
    }

    @WrapOperation(
            method = {"lambda$compile$0", "lambda$compile$1"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;putBlockBakedQuad(FFFLnet/minecraft/client/resources/model/geometry/BakedQuad;Lcom/mojang/blaze3d/vertex/QuadInstance;)V"
            )
    )
    private void renderhide$emitBlockQuad(BufferBuilder builder, float x, float y, float z,
            BakedQuad quad, QuadInstance instance, Operation<Void> original) {
        RegionManager.BlockRenderMode mode = renderhide$currentMode.get();
        if (mode == RegionManager.BlockRenderMode.SKIP) {
            return;
        }
        if (mode == RegionManager.BlockRenderMode.TRANSLUCENT) {
            instance.multiplyColor(ARGB.white(RegionManager.hiddenBlockOpacity()));
        }
        original.call(builder, x, y, z, quad, instance);
    }

    @Inject(method = "lambda$compile$2", at = @At("RETURN"), cancellable = true)
    private void renderhide$applyFluidAlpha(CallbackInfoReturnable<VertexConsumer> cir) {
        RegionManager.BlockRenderMode mode = renderhide$currentMode.get();
        if (mode == RegionManager.BlockRenderMode.TRANSLUCENT
                || mode == RegionManager.BlockRenderMode.SKIP) {
            cir.setReturnValue(new AlphaVertexConsumer(cir.getReturnValue(), RegionManager.hiddenBlockOpacity()));
        }
    }

    @Inject(method = "handleBlockEntity", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void renderhide$skipBlockEntity(SectionCompiler.Results data,
            E blockEntity, CallbackInfo ci) {
        if (RegionManager.isFullyHidden(blockEntity.getBlockPos(), blockEntity.getBlockState())) {
            ci.cancel();
        }
    }

    @Inject(method = "compile", at = @At("RETURN"))
    private void renderhide$clearCapturedBlock(CallbackInfoReturnable<SectionCompiler.Results> cir) {
        renderhide$currentMode.remove();
    }
}
