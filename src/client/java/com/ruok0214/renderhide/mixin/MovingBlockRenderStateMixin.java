package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/renderer/block/MovingBlockRenderState", remap = false)
abstract class MovingBlockRenderStateMixin implements MovingBlockOpacityAccess {
    @Unique private float renderhide$opacity = Float.NaN;
    @Unique private Direction renderhide$movementDirection;

    @Override
    public float renderhide$getOpacity() {
        return renderhide$opacity;
    }

    @Override
    public void renderhide$setOpacity(float opacity) {
        renderhide$opacity = Math.max(0.0F, Math.min(1.0F, opacity));
    }

    @Override
    public Direction renderhide$getMovementDirection() {
        return renderhide$movementDirection;
    }

    @Override
    public void renderhide$setMovementDirection(Direction direction) {
        renderhide$movementDirection = direction;
    }

    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true, remap = false)
    private void renderhide$includeAdjacentMovingBlock(BlockPos queryPos,
            CallbackInfoReturnable<BlockState> cir) {
        if (!(renderhide$opacity > 0.0F && renderhide$opacity < 1.0F)
                || renderhide$movementDirection == null) {
            return;
        }

        MovingBlockRenderState self = (MovingBlockRenderState) (Object) this;
        if (queryPos.equals(self.blockPos)) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }

        BlockPos movingEntityPos = queryPos.relative(renderhide$movementDirection);
        BlockEntity blockEntity = client.level.getBlockEntity(movingEntityPos);
        if (!(blockEntity instanceof PistonMovingBlockEntity piston)
                || piston.getMovementDirection() != renderhide$movementDirection) {
            return;
        }

        BlockState neighborState = piston.getMovedState();
        if (neighborState.isAir()) {
            return;
        }

        float neighborOpacity = RegionManager.movingBlockRenderOpacity(
                neighborState, piston.getBlockPos(), queryPos, movingEntityPos);
        if (neighborOpacity > 0.0F) {
            cir.setReturnValue(neighborState);
        }
    }
}
