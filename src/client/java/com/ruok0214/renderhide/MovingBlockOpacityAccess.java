package com.ruok0214.renderhide;

import net.minecraft.core.Direction;

public interface MovingBlockOpacityAccess {
    float renderhide$getOpacity();
    void renderhide$setOpacity(float opacity);

    Direction renderhide$getMovementDirection();
    void renderhide$setMovementDirection(Direction direction);
}
