package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.MovingPistonStates;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
abstract class LevelChunkMovingPistonMixin {
    @Inject(method = "setBlockEntity", at = @At("TAIL"))
    private void renderhide$publishMovedState(BlockEntity entity, CallbackInfo ci) {
        MovingPistonStates.put(((LevelChunk) (Object) this).getLevel(), entity);
    }

    @Inject(method = "clearAllBlockEntities", at = @At("HEAD"))
    private void renderhide$clearMovedStates(CallbackInfo ci) {
        LevelChunk chunk = (LevelChunk) (Object) this;
        MovingPistonStates.removeChunk(chunk.getLevel(), chunk.getPos());
    }

    @Inject(method = "removeBlockEntity", at = @At("HEAD"))
    private void renderhide$removeMovedState(BlockPos pos, CallbackInfo ci) {
        MovingPistonStates.remove(((LevelChunk) (Object) this).getLevel(), pos);
    }
}
