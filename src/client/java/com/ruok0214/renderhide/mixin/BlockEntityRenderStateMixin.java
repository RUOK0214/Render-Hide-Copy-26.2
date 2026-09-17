package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.BlockEntityOpacityAccess;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntityRenderState.class)
abstract class BlockEntityRenderStateMixin implements BlockEntityOpacityAccess {
    @Unique private float renderhide$blockEntityOpacity = 1.0F;

    @Override
    public float renderhide$getBlockEntityOpacity() {
        return this.renderhide$blockEntityOpacity;
    }

    @Override
    public void renderhide$setBlockEntityOpacity(float opacity) {
        this.renderhide$blockEntityOpacity = Math.max(0.0F, Math.min(1.0F, opacity));
    }
}
