/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator
 *  net.fabricmc.fabric.impl.client.indigo.renderer.mesh.QuadViewImpl
 *  net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.QuadViewImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={AoCalculator.class}, remap=false)
abstract class IndigoAoCalculatorMixin {
    @Shadow
    @Final
    private BlockRenderInfo blockInfo;
    @Shadow
    @Final
    public float[] ao;

    IndigoAoCalculatorMixin() {
    }

    @Inject(method={"compute"}, at={@At(value="RETURN")}, remap=false)
    private void renderhide$applyVirtualAo(QuadViewImpl quad, boolean vanillaShade, CallbackInfo ci) {
        if (!RegionManager.isVirtualLightAffected(this.blockInfo.blockPos, quad.lightFace())) {
            return;
        }
        for (int vertex = 0; vertex < this.ao.length; ++vertex) {
            this.ao[vertex] = 1.0f;
        }
    }
}


