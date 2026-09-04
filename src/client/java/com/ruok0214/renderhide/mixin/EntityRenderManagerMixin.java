/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.Entity
 *  net.minecraft.Frustum
 *  net.minecraft.EntityRenderDispatcher
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.ruok0214.renderhide.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ruok0214.renderhide.EntityAlphaSubmitNodeCollector;
import com.ruok0214.renderhide.EntityRenderStateOpacityAccess;
import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={EntityRenderDispatcher.class})
public abstract class EntityRenderManagerMixin {
    @Inject(method={"shouldRender"}, at={@At(value="HEAD")}, cancellable=true)
    private <E extends Entity> void renderhide$hideEntity(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (RegionManager.entityRenderOpacity(entity) <= 0.0F) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "extractEntity", at = @At("RETURN"))
    private <E extends Entity> void renderhide$storeEntityOpacity(E entity,
            float tickProgress, CallbackInfoReturnable<EntityRenderState> cir) {
        ((EntityRenderStateOpacityAccess) cir.getReturnValue())
                .renderhide$setOpacity(RegionManager.entityRenderOpacity(entity));
    }

    @WrapOperation(
            method = "submit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"
            )
    )
    private <S extends EntityRenderState> void renderhide$submitWithOpacity(
            EntityRenderer<?, ? super S> renderer, S state, PoseStack poseStack,
            SubmitNodeCollector collector, CameraRenderState cameraState,
            Operation<Void> original) {
        float opacity = ((EntityRenderStateOpacityAccess) state).renderhide$getOpacity();
        SubmitNodeCollector adjusted = opacity > 0.0F && opacity < 1.0F
                ? new EntityAlphaSubmitNodeCollector(collector, opacity)
                : collector;
        original.call(renderer, state, poseStack, adjusted, cameraState);
    }
}
