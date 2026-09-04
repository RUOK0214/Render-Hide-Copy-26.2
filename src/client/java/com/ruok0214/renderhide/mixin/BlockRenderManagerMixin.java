package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.AlphaVertexConsumer;
import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import java.util.List;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/renderer/block/BlockRenderDispatcher", remap = false)
abstract class BlockRenderManagerMixin {
    @Unique
    private static boolean renderhide$isFullyHidden(BlockAndTintGetter world, BlockPos suppliedPos,
            BlockState state) {
        if (RegionManager.isFullyHidden(suppliedPos, state)) return true;
        if (world instanceof MovingBlockRenderState moving) {
            if (moving.randomSeedPos != null && RegionManager.isFullyHidden(moving.randomSeedPos, state)) return true;
            if (moving.blockPos != null && RegionManager.isFullyHidden(moving.blockPos, state)) return true;
        }
        return false;
    }

    @Unique
    private static float renderhide$opacity(BlockAndTintGetter world, BlockPos suppliedPos,
            BlockState state) {
        // Piston models can use several different buffers during extension and
        // retraction. Apply the opacity where every model is actually rendered,
        // instead of wrapping only the first buffer request in the command renderer.
        if (world instanceof MovingBlockRenderState moving) {
            float stored = ((MovingBlockOpacityAccess) moving).renderhide$getOpacity();
            if (!Float.isNaN(stored)) return stored;
            if ((moving.randomSeedPos != null && RegionManager.isGhostRendered(moving.randomSeedPos, state))
                    || (moving.blockPos != null && RegionManager.isGhostRendered(moving.blockPos, state))) {
                return RegionManager.hiddenBlockOpacity();
            }
        }
        return RegionManager.isGhostRendered(suppliedPos, state)
                ? RegionManager.hiddenBlockOpacity() : 1.0F;
    }

    @Inject(method = "renderBatched", at = @At("HEAD"), cancellable = true)
    private void renderhide$skipBlock(BlockState state, BlockPos pos, BlockAndTintGetter world,
            PoseStack matrices, VertexConsumer vertices, boolean cull, List<BlockModelPart> parts,
            CallbackInfo ci) {
        if (renderhide$isFullyHidden(world, pos, state)) ci.cancel();
    }

    @ModifyVariable(method = "renderBatched", at = @At("HEAD"), argsOnly = true)
    private VertexConsumer renderhide$ghostBlock(VertexConsumer vertices, BlockState state,
            BlockPos pos, BlockAndTintGetter world) {
        float opacity = renderhide$opacity(world, pos, state);
        return opacity < 1.0F ? new AlphaVertexConsumer(vertices, opacity) : vertices;
    }

    @Inject(method = "renderLiquid", at = @At("HEAD"), cancellable = true)
    private void renderhide$skipFluid(BlockPos pos, BlockAndTintGetter world, VertexConsumer vertices,
            BlockState state, FluidState fluid, CallbackInfo ci) {
        if (renderhide$isFullyHidden(world, pos, state)) ci.cancel();
    }

    @ModifyVariable(method = "renderLiquid", at = @At("HEAD"), argsOnly = true)
    private VertexConsumer renderhide$ghostFluid(VertexConsumer vertices, BlockPos pos,
            BlockAndTintGetter world, VertexConsumer ignored, BlockState state) {
        float opacity = renderhide$opacity(world, pos, state);
        return opacity < 1.0F ? new AlphaVertexConsumer(vertices, opacity) : vertices;
    }
}

