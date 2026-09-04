package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/renderer/feature/BlockFeatureRenderer", remap = false)
abstract class FallingBlockCommandRendererMixin {
    @Unique
    private static final ThreadLocal<MovingBlockRenderState> renderhide$currentMovingState = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<Float> renderhide$currentOpacity =
            ThreadLocal.withInitial(() -> 1.0F);

    @Redirect(
            method = "render(Lnet/minecraft/client/renderer/SubmitNodeCollection;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;Lnet/minecraft/client/renderer/OutlineBufferSource;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeStorage$MovingBlockSubmit;movingBlockRenderState()Lnet/minecraft/client/renderer/block/MovingBlockRenderState;")
    )
    private MovingBlockRenderState renderhide$captureMovingState(SubmitNodeStorage.MovingBlockSubmit command) {
        MovingBlockRenderState state = command.movingBlockRenderState();
        renderhide$currentMovingState.set(state);
        renderhide$currentOpacity.set(1.0F);
        return state;
    }

    @Redirect(
            method = "render(Lnet/minecraft/client/renderer/SubmitNodeCollection;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;Lnet/minecraft/client/renderer/OutlineBufferSource;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getMovingBlockRenderType(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/rendertype/RenderType;")
    )
    private RenderType renderhide$chooseMovingLayer(BlockState state) {
        MovingBlockRenderState moving = renderhide$currentMovingState.get();
        float opacity = moving == null ? 1.0F : renderhide$opacity(moving, state);
        renderhide$currentOpacity.set(opacity);
        return opacity < 1.0F ? RenderTypes.translucentMovingBlock() : ItemBlockRenderTypes.getMovingBlockRenderType(state);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/renderer/SubmitNodeCollection;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;Lnet/minecraft/client/renderer/OutlineBufferSource;)V",
            at = @At("RETURN")
    )
    private void renderhide$clearMovingState(CallbackInfo ci) {
        renderhide$currentMovingState.remove();
        renderhide$currentOpacity.remove();
    }

    @Unique
    private static float renderhide$opacity(MovingBlockRenderState moving, BlockState state) {
        float stored = ((MovingBlockOpacityAccess) moving).renderhide$getOpacity();
        if (!Float.isNaN(stored)) return stored;
        if ((moving.randomSeedPos != null && RegionManager.isFullyHidden(moving.randomSeedPos, state))
                || (moving.blockPos != null && RegionManager.isFullyHidden(moving.blockPos, state))) return 0.0F;
        if ((moving.randomSeedPos != null && RegionManager.isGhostRendered(moving.randomSeedPos, state))
                || (moving.blockPos != null && RegionManager.isGhostRendered(moving.blockPos, state))) {
            return RegionManager.hiddenBlockOpacity();
        }
        return 1.0F;
    }
}

