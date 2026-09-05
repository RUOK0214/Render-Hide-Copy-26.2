package com.ruok0214.renderhide;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public interface MovingBlockOpacityAccess {
    float renderhide$getOpacity();
    void renderhide$setOpacity(float opacity);

    Direction renderhide$getMovementDirection();
    void renderhide$setMovementDirection(Direction direction);

    float renderhide$getOpacity(BlockPos queryPos);
}
