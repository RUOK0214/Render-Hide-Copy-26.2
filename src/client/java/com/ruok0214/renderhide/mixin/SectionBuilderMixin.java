package com.ruok0214.renderhide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
    private static final ThreadLocal<BlockPos> renderhide$currentPos = new ThreadLocal<>();
    private static final ThreadLocal<BlockState> renderhide$currentState = new ThreadLocal<>();

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
        renderhide$currentPos.set(pos.immutable());
        renderhide$currentState.set(state);
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
        BlockPos pos = renderhide$currentPos.get();
        return pos != null && RegionManager.isGhostRendered(pos, state) ? false : original.call(state);
    }

    @ModifyExpressionValue(
            method = "compile",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;forceOpaque(ZLnet/minecraft/world/level/block/state/BlockState;)Z"
            )
    )
    private boolean renderhide$ghostIsNotForcedOpaque(boolean original) {
        BlockPos pos = renderhide$currentPos.get();
        BlockState state = renderhide$currentState.get();
        return pos != null && state != null && RegionManager.isGhostRendered(pos, state) ? false : original;
    }

    @ModifyVariable(method = "getOrBeginLayer", at = @At("HEAD"), argsOnly = true)
    private ChunkSectionLayer renderhide$useTranslucentLayer(ChunkSectionLayer original) {
        BlockPos pos = renderhide$currentPos.get();
        BlockState state = renderhide$currentState.get();
        return pos != null && state != null && RegionManager.isGhostRendered(pos, state)
                ? ChunkSectionLayer.TRANSLUCENT : original;
    }

    @ModifyVariable(
            method = {"lambda$compile$0", "lambda$compile$1"},
            at = @At("HEAD"),
            argsOnly = true
    )
    private QuadInstance renderhide$applyBlockAlpha(QuadInstance instance) {
        BlockPos pos = renderhide$currentPos.get();
        BlockState state = renderhide$currentState.get();
        if (pos != null && state != null && RegionManager.isGhostRendered(pos, state)) {
            instance.multiplyColor(ARGB.white(RegionManager.hiddenBlockOpacity()));
        }
        return instance;
    }

    @Inject(method = "lambda$compile$2", at = @At("RETURN"), cancellable = true)
    private void renderhide$applyFluidAlpha(CallbackInfoReturnable<VertexConsumer> cir) {
        BlockPos pos = renderhide$currentPos.get();
        BlockState state = renderhide$currentState.get();
        if (pos != null && state != null && RegionManager.isGhostRendered(pos, state)) {
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
        renderhide$currentPos.remove();
        renderhide$currentState.remove();
    }
}
