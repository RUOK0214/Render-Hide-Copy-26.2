package com.ruok0214.renderhide.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.ruok0214.renderhide.RegionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.data.BuiltSectionInfo;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net/caffeinemc/mods/sodium/client/render/chunk/compile/tasks/ChunkBuilderMeshingTask", remap = false)
abstract class SodiumSectionCompilerMixin {
    @Unique
    private BlockPos renderhide$currentPos;

    @WrapOperation(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;",
                    remap = false
            ),
            remap = false
    )
    private BlockState renderhide$captureBlock(LevelSlice level, int x, int y, int z,
            Operation<BlockState> original) {
        BlockState state = original.call(level, x, y, z);
        this.renderhide$currentPos = new BlockPos(x, y, z);
        return state;
    }

    @WrapOperation(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isSolidRender()Z"
            ),
            remap = false
    )
    private boolean renderhide$hiddenBlockDoesNotOcclude(BlockState state,
            Operation<Boolean> original) {
        return this.renderhide$currentPos != null
                && RegionManager.affectsOcclusion(this.renderhide$currentPos, state)
                ? false
                : original.call(state);
    }

    @WrapOperation(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/data/BuiltSectionInfo$Builder;addBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;Z)V",
                    remap = false
            ),
            remap = false
    )
    private void renderhide$skipHiddenBlockEntity(BuiltSectionInfo.Builder builder,
            BlockEntity blockEntity, boolean culled, Operation<Void> original) {
        if (!RegionManager.isFullyHidden(blockEntity.getBlockPos(), blockEntity.getBlockState())) {
            original.call(builder, blockEntity, culled);
        }
    }
}
