/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl
 *  net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractTerrainRenderContext
 *  net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo
 *  net.minecraft.LightLayer
 *  net.minecraft.Direction
 *  net.minecraft.LightTexture
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
import net.minecraft.world.level.LightLayer;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.LightTexture;
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
        Direction face;
        Direction class_23502 = face = quad.cullFace() != null ? quad.cullFace() : quad.lightFace();
        if (!RegionManager.isVirtualLightAffected(this.blockInfo.blockPos, face)) {
            return;
        }
        for (int vertex = 0; vertex < 4; ++vertex) {
            int original = quad.lightmap(vertex);
            int block = RegionManager.virtualLightLevel(LightLayer.BLOCK, this.blockInfo.blockPos, face, LightTexture.block((int)original));
            int sky = RegionManager.virtualLightLevel(LightLayer.SKY, this.blockInfo.blockPos, face, LightTexture.sky((int)original));
            int virtual = LightTexture.pack((int)block, (int)sky);
            int merged = LightTexture.pack((int)Math.max(LightTexture.block((int)original), LightTexture.block((int)virtual)), (int)Math.max(LightTexture.sky((int)original), LightTexture.sky((int)virtual)));
            quad.lightmap(vertex, merged);
        }
    }
}


