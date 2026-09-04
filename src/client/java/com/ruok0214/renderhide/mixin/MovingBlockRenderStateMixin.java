package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.MovingBlockOpacityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "net/minecraft/class_11791", remap = false)
abstract class MovingBlockRenderStateMixin implements MovingBlockOpacityAccess {
    @Unique private float renderhide$opacity = Float.NaN;

    @Override
    public float renderhide$getOpacity() {
        return renderhide$opacity;
    }

    @Override
    public void renderhide$setOpacity(float opacity) {
        renderhide$opacity = Math.max(0.0F, Math.min(1.0F, opacity));
    }
}

