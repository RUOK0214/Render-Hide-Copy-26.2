/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl
 *  net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractTerrainRenderContext
 *  net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo
 *  net.minecraft.class_1944
 *  net.minecraft.class_2350
 *  net.minecraft.class_765
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
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractTerrainRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.class_1944;
import net.minecraft.class_2350;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={AbstractTerrainRenderContext.class}, remap=false)
abstract class IndigoTerrainLightMixin {
    @Shadow
    @Final
    protected BlockRenderInfo blockInfo;

    IndigoTerrainLightMixin() {
    }

    @Inject(method={"shadeQuad"}, at={@At(value="RETURN")}, remap=false)
    private void renderhide$applyVirtualLight(MutableQuadViewImpl quad, boolean ao, boolean emissive, boolean vanillaShade, CallbackInfo ci) {
        class_2350 face;
        class_2350 class_23502 = face = quad.cullFace() != null ? quad.cullFace() : quad.lightFace();
        if (!RegionManager.isVirtualLightAffected(this.blockInfo.blockPos, face)) {
            return;
        }
        for (int vertex = 0; vertex < 4; ++vertex) {
            int original = quad.lightmap(vertex);
            int block = RegionManager.virtualLightLevel(class_1944.field_9282, this.blockInfo.blockPos, face, class_765.method_24186((int)original));
            int sky = RegionManager.virtualLightLevel(class_1944.field_9284, this.blockInfo.blockPos, face, class_765.method_24187((int)original));
            int virtual = class_765.method_23687((int)block, (int)sky);
            int merged = class_765.method_23687((int)Math.max(class_765.method_24186((int)original), class_765.method_24186((int)virtual)), (int)Math.max(class_765.method_24187((int)original), class_765.method_24187((int)virtual)));
            quad.lightmap(vertex, merged);
        }
    }
}


