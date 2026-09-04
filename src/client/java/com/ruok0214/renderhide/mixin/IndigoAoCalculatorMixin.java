package com.ruok0214.renderhide.mixin;

import com.ruok0214.renderhide.RegionManager;
import java.util.Arrays;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.QuadViewImpl;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator", remap = false)
abstract class IndigoAoCalculatorMixin {
    @Shadow
    private BlockAndTintGetter level;

    @Shadow
    private BlockPos pos;

    @Shadow
    @Final
    public float[] ao;

    @Shadow
    @Final
    public int[] light;

    @Inject(method = "compute", at = @At("RETURN"), remap = false)
    private void renderhide$removeHiddenNeighborShadow(QuadViewImpl quad,
            boolean vanillaShade, CallbackInfo ci) {
        if (this.level == null || this.pos == null
                || !RegionManager.shouldImproveVisibleBlockLighting(this.pos)) {
            return;
        }

        Direction face = quad.lightFace();
        float directionalShade = face == null
                ? this.level.cardinalLighting().up()
                : this.level.cardinalLighting().byFace(face);
        Arrays.fill(this.ao, directionalShade);

        for (int vertex = 0; vertex < this.light.length; vertex++) {
            int original = this.light[vertex];
            int originalBlock = LightCoordsUtil.smoothBlock(original) >> 4;
            int originalSky = LightCoordsUtil.smoothSky(original) >> 4;
            int block = RegionManager.virtualLightLevel(
                    LightLayer.BLOCK, this.pos, face, originalBlock);
            int sky = RegionManager.virtualLightLevel(
                    LightLayer.SKY, this.pos, face, originalSky);
            this.light[vertex] = LightCoordsUtil.smoothPack(
                    Math.max(LightCoordsUtil.smoothBlock(original), block << 4),
                    Math.max(LightCoordsUtil.smoothSky(original), sky << 4));
        }
    }
}
