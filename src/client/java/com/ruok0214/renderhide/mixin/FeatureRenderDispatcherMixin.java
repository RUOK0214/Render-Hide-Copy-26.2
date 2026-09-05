package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.DeferredEntityBlockRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.class)
abstract class FeatureRenderDispatcherMixin {
    @Shadow
    @Final
    private MultiBufferSource.BufferSource bufferSource;

    @Shadow
    @Final
    private OutlineBufferSource outlineBufferSource;

    @Inject(method = "renderTranslucentAfterTerrain", at = @At("TAIL"))
    private void renderhide$renderDeferredEntityBlocks(CallbackInfo ci) {
        DeferredEntityBlockRenderer.render(this.bufferSource, this.outlineBufferSource);
    }
}
