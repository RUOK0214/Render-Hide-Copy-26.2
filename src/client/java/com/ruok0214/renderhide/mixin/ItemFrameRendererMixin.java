package com.ruok0214.renderhide.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ruok0214.renderhide.EntityAlphaSubmitNodeCollector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/** Keeps a translucent item-frame body in front of its supporting block. */
@Environment(EnvType.CLIENT)
@Mixin(ItemFrameRenderer.class)
abstract class ItemFrameRendererMixin {
    @Unique
    private static final float RENDERHIDE_FRAME_Z_OFFSET = 1.0F / 64.0F;

    @WrapOperation(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/BlockModelRenderState;submitWithZOffset(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
            )
    )
    private void renderhide$submitTranslucentFrameInFront(
            BlockModelRenderState model, PoseStack poseStack,
            SubmitNodeCollector collector, int light, int overlay, int outlineColor,
            Operation<Void> original) {
        if (!(collector instanceof EntityAlphaSubmitNodeCollector)) {
            original.call(model, poseStack, collector, light, overlay, outlineColor);
            return;
        }

        poseStack.pushPose();
        try {
            poseStack.translate(0.0F, 0.0F, RENDERHIDE_FRAME_Z_OFFSET);
            original.call(model, poseStack, collector, light, overlay, outlineColor);
        } finally {
            poseStack.popPose();
        }
    }
}
