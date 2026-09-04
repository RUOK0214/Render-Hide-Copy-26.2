package com.ruok0214.renderhide.mixin;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.class)
public interface RenderTypeAccessor {
    @Accessor("state")
    RenderSetup renderhide$getState();

    @Invoker("create")
    static RenderType renderhide$create(String name, RenderSetup setup) {
        throw new AssertionError();
    }
}
