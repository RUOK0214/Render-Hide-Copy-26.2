package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.EntityRenderStateOpacityAccess;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
abstract class EntityRenderStateMixin implements EntityRenderStateOpacityAccess {
    @Unique
    private float renderhide$opacity = 1.0F;

    @Override
    public float renderhide$getOpacity() {
        return this.renderhide$opacity;
    }

    @Override
    public void renderhide$setOpacity(float opacity) {
        this.renderhide$opacity = Math.max(0.0F, Math.min(1.0F, opacity));
    }
}
