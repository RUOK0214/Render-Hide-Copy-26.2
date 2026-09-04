/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
 *  net.minecraft.RenderTypes
 *  net.minecraft.BlockPos
 *  net.minecraft.AABB
 *  net.minecraft.Vec3
 *  net.minecraft.Shapes
 *  net.minecraft.VoxelShape
 *  net.minecraft.Minecraft
 *  net.minecraft.PoseStack
 *  net.minecraft.VertexConsumer
 *  net.minecraft.ShapeRenderer
 */
package com.ruok0214.renderhide;

import com.ruok0214.renderhide.HiddenRegion;
import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ShapeRenderer;

@Environment(value=EnvType.CLIENT)
public final class SelectionOverlayRenderer {
    private static final int SELECTION_COLOR = -50384;
    private static final int SAVED_COLOR = -16121;
    private static final double EXPAND = 0.002;
    private static boolean showSavedRegions;

    private SelectionOverlayRenderer() {
    }

    public static void register() {
        WorldRenderEvents.END_MAIN.register(SelectionOverlayRenderer::render);
    }

    public static boolean toggleSavedRegions() {
        showSavedRegions = !showSavedRegions;
        RegionManager.message("Saved region outlines: " + (showSavedRegions ? "ON" : "OFF"));
        return showSavedRegions;
    }

    public static boolean areSavedRegionsShown() {
        return showSavedRegions;
    }

    private static void render(WorldRenderContext context) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.gameRenderer == null || client.gameRenderer.getMainCamera() == null || context.consumers() == null || context.matrices() == null) {
            return;
        }
        Vec3 camera = client.gameRenderer.getMainCamera().position();
        VertexConsumer lines = context.consumers().getBuffer(RenderTypes.linesTranslucent());
        if (RegionManager.isGloballyEnabled()) {
            BlockPos pos1 = RegionManager.getPos1();
            BlockPos pos2 = RegionManager.getPos2();
            if (pos1 != null) {
                if (pos2 == null) {
                    SelectionOverlayRenderer.drawBlockBox(context, lines, camera, pos1, pos1, -50384, 3.0f);
                } else {
                    SelectionOverlayRenderer.drawBlockBox(context, lines, camera, pos1, pos2, -50384, 3.0f);
                }
            }
        }
        if (!showSavedRegions) {
            return;
        }
        String dimension = client.level.dimension().identifier().toString();
        for (HiddenRegion region : RegionManager.regions()) {
            if (!region.enabled() || !region.dimension().equals(dimension)) continue;
            SelectionOverlayRenderer.drawBox(context, lines, camera, region.minX(), region.minY(), region.minZ(), (double)region.maxX() + 1.0, (double)region.maxY() + 1.0, (double)region.maxZ() + 1.0, -16121, 2.0f);
        }
    }

    private static void drawBlockBox(WorldRenderContext context, VertexConsumer lines, Vec3 camera, BlockPos first, BlockPos second, int color, float width) {
        SelectionOverlayRenderer.drawBox(context, lines, camera, Math.min(first.getX(), second.getX()), Math.min(first.getY(), second.getY()), Math.min(first.getZ(), second.getZ()), (double)Math.max(first.getX(), second.getX()) + 1.0, (double)Math.max(first.getY(), second.getY()) + 1.0, (double)Math.max(first.getZ(), second.getZ()) + 1.0, color, width);
    }

    private static void drawBox(WorldRenderContext context, VertexConsumer lines, Vec3 camera, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color, float width) {
        VoxelShape shape = Shapes.create((AABB)new AABB(-0.002, -0.002, -0.002, maxX - minX + 0.002, maxY - minY + 0.002, maxZ - minZ + 0.002));
        ShapeRenderer.renderShape((PoseStack)context.matrices(), (VertexConsumer)lines, (VoxelShape)shape, (double)(minX - camera.x), (double)(minY - camera.y), (double)(minZ - camera.z), (int)color, (float)width);
    }
}


