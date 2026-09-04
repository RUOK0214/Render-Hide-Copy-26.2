/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11515
 *  net.minecraft.class_2338
 *  net.minecraft.class_2586
 *  net.minecraft.class_2680
 *  net.minecraft.class_853
 *  net.minecraft.class_9810
 *  net.minecraft.class_9810$class_9811
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.ruok0214.renderhide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11515;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_853;
import net.minecraft.class_9810;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_9810.class})
abstract class SectionBuilderMixin {
    private static final ThreadLocal<class_2338> renderhide$currentPos = new ThreadLocal();
    private static final ThreadLocal<class_2680> renderhide$currentState = new ThreadLocal();

    SectionBuilderMixin() {
    }

    @WrapOperation(method={"method_60904"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_853;method_8320(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2680;")})
    private class_2680 renderhide$captureCurrentBlock(class_853 world, class_2338 pos, Operation<class_2680> original) {
        class_2680 state = (class_2680)original.call(new Object[]{world, pos});
        renderhide$currentPos.set(pos.method_10062());
        renderhide$currentState.set(state);
        return state;
    }

    @WrapOperation(method={"method_60904"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_2680;method_26216()Z")})
    private boolean renderhide$ghostDoesNotOcclude(class_2680 state, Operation<Boolean> original) {
        class_2338 pos = renderhide$currentPos.get();
        return pos != null && RegionManager.isGhostRendered(pos, state) ? false : (Boolean)original.call(new Object[]{state});
    }

    @ModifyExpressionValue(method={"method_60904"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_4696;method_23679(Lnet/minecraft/class_2680;)Lnet/minecraft/class_11515;")})
    private class_11515 renderhide$useTranslucentBlockLayer(class_11515 original) {
        class_2338 pos = renderhide$currentPos.get();
        class_2680 state = renderhide$currentState.get();
        return pos != null && state != null && RegionManager.isGhostRendered(pos, state) ? class_11515.field_60926 : original;
    }

    @ModifyExpressionValue(method={"method_60904"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_4696;method_23680(Lnet/minecraft/class_3610;)Lnet/minecraft/class_11515;")})
    private class_11515 renderhide$useTranslucentFluidLayer(class_11515 original) {
        class_2338 pos = renderhide$currentPos.get();
        class_2680 state = renderhide$currentState.get();
        return pos != null && state != null && RegionManager.isGhostRendered(pos, state) ? class_11515.field_60926 : original;
    }

    @Inject(method={"method_60902"}, at={@At(value="HEAD")}, cancellable=true)
    private <E extends class_2586> void renderhide$skipBlockEntity(class_9810.class_9811 data, E blockEntity, CallbackInfo ci) {
        if (RegionManager.isFullyHidden(blockEntity.method_11016(), blockEntity.method_11010())) {
            ci.cancel();
        }
    }
}

