package com.ruok0214.renderhide;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Environment(EnvType.CLIENT)
public final class SelectionOverlayRenderer {
    private static final int SELECTION_COLOR = -50384;
    private static final int SAVED_COLOR = -16121;
    private static final double EXPAND = 0.002;
    private static boolean showSavedRegions;

    private SelectionOverlayRenderer() {
    }

    public static void register() {
        LevelRenderEvents.COLLECT_SUBMITS.register(SelectionOverlayRenderer::render);
    }

    public static boolean toggleSavedRegions() {
        showSavedRegions = !showSavedRegions;
        RegionManager.message("Saved region outlines: " + (showSavedRegions ? "ON" : "OFF"));
        return showSavedRegions;
    }

    public static boolean areSavedRegionsShown() {
        return showSavedRegions;
    }

    private static void render(LevelRenderContext context) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || context.levelState().cameraRenderState == null
                || context.levelState().cameraRenderState.pos == null) {
            return;
        }

        Vec3 camera = context.levelState().cameraRenderState.pos;
        if (RegionManager.isGloballyEnabled()) {
            BlockPos pos1 = RegionManager.getPos1();
            BlockPos pos2 = RegionManager.getPos2();
            if (pos1 != null) {
                drawBlockBox(context, camera, pos1, pos2 == null ? pos1 : pos2, SELECTION_COLOR, 3.0F);
            }
        }

        if (!showSavedRegions) {
            return;
        }

        String dimension = client.level.dimension().identifier().toString();
        for (HiddenRegion region : RegionManager.regions()) {
            if (region.enabled() && region.dimension().equals(dimension)) {
                drawBox(context, camera, region.minX(), region.minY(), region.minZ(),
                        region.maxX() + 1.0, region.maxY() + 1.0, region.maxZ() + 1.0,
                        SAVED_COLOR, 2.0F);
            }
        }
    }

    private static void drawBlockBox(LevelRenderContext context, Vec3 camera, BlockPos first,
            BlockPos second, int color, float width) {
        drawBox(context, camera,
                Math.min(first.getX(), second.getX()),
                Math.min(first.getY(), second.getY()),
                Math.min(first.getZ(), second.getZ()),
                Math.max(first.getX(), second.getX()) + 1.0,
                Math.max(first.getY(), second.getY()) + 1.0,
                Math.max(first.getZ(), second.getZ()) + 1.0,
                color, width);
    }

    private static void drawBox(LevelRenderContext context, Vec3 camera,
            double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
            int color, float width) {
        VoxelShape shape = Shapes.create(new AABB(-EXPAND, -EXPAND, -EXPAND,
                maxX - minX + EXPAND, maxY - minY + EXPAND, maxZ - minZ + EXPAND));
        PoseStack poseStack = context.poseStack();
        poseStack.pushPose();
        poseStack.translate(minX - camera.x, minY - camera.y, minZ - camera.z);
        context.submitNodeCollector().submitShapeOutline(poseStack, shape,
                RenderTypes.linesTranslucent(), color, width, true);
        poseStack.popPose();
    }
}
