/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.ChunkSectionLayer
 *  net.minecraft.BlockPos
 *  net.minecraft.BlockEntity
 *  net.minecraft.BlockState
 *  net.minecraft.RenderSectionRegion
 *  net.minecraft.SectionCompiler
 *  net.minecraft.SectionCompiler$Results
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.ruok0214.renderhide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={SectionCompiler.class})
abstract class SectionBuilderMixin {
    private static final ThreadLocal<BlockPos> renderhide$currentPos = new ThreadLocal();
    private static final ThreadLocal<BlockState> renderhide$currentState = new ThreadLocal();

    SectionBuilderMixin() {
    }

    @WrapOperation(method={"compile"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")})
    private BlockState renderhide$captureCurrentBlock(RenderSectionRegion world, BlockPos pos, Operation<BlockState> original) {
        BlockState state = (BlockState)original.call(new Object[]{world, pos});
        renderhide$currentPos.set(pos.immutable());
        renderhide$currentState.set(state);
        return state;
    }

    @WrapOperation(method={"compile"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/block/state/BlockState;isSolidRender()Z")})
    private boolean renderhide$ghostDoesNotOcclude(BlockState state, Operation<Boolean> original) {
        BlockPos pos = renderhide$currentPos.get();
        return pos != null && RegionManager.isGhostRendered(pos, state) ? false : (Boolean)original.call(new Object[]{state});
    }

    @ModifyExpressionValue(method={"compile"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getChunkRenderType(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/chunk/ChunkSectionLayer;")})
    private ChunkSectionLayer renderhide$useTranslucentBlockLayer(ChunkSectionLayer original) {
        BlockPos pos = renderhide$currentPos.get();
        BlockState state = renderhide$currentState.get();
        return pos != null && state != null && RegionManager.isGhostRendered(pos, state) ? ChunkSectionLayer.TRANSLUCENT : original;
    }

    @ModifyExpressionValue(method={"compile"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getRenderLayer(Lnet/minecraft/world/level/material/FluidState;)Lnet/minecraft/client/renderer/chunk/ChunkSectionLayer;")})
    private ChunkSectionLayer renderhide$useTranslucentFluidLayer(ChunkSectionLayer original) {
        BlockPos pos = renderhide$currentPos.get();
        BlockState state = renderhide$currentState.get();
        return pos != null && state != null && RegionManager.isGhostRendered(pos, state) ? ChunkSectionLayer.TRANSLUCENT : original;
    }

    @Inject(method={"handleBlockEntity"}, at={@At(value="HEAD")}, cancellable=true)
    private <E extends BlockEntity> void renderhide$skipBlockEntity(SectionCompiler.Results data, E blockEntity, CallbackInfo ci) {
        if (RegionManager.isFullyHidden(blockEntity.getBlockPos(), blockEntity.getBlockState())) {
            ci.cancel();
        }
    }
}

