/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
 *  net.minecraft.class_12249
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_259
 *  net.minecraft.class_265
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_9974
 */
package com.ruok0214.renderhide;

import com.ruok0214.renderhide.HiddenRegion;
import com.ruok0214.renderhide.RegionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.class_12249;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_9974;

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
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || client.field_1773 == null || client.field_1773.method_19418() == null || context.consumers() == null || context.matrices() == null) {
            return;
        }
        class_243 camera = client.field_1773.method_19418().method_71156();
        class_4588 lines = context.consumers().method_73477(class_12249.method_76668());
        if (RegionManager.isGloballyEnabled()) {
            class_2338 pos1 = RegionManager.getPos1();
            class_2338 pos2 = RegionManager.getPos2();
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
        String dimension = client.field_1687.method_27983().method_29177().toString();
        for (HiddenRegion region : RegionManager.regions()) {
            if (!region.enabled() || !region.dimension().equals(dimension)) continue;
            SelectionOverlayRenderer.drawBox(context, lines, camera, region.minX(), region.minY(), region.minZ(), (double)region.maxX() + 1.0, (double)region.maxY() + 1.0, (double)region.maxZ() + 1.0, -16121, 2.0f);
        }
    }

    private static void drawBlockBox(WorldRenderContext context, class_4588 lines, class_243 camera, class_2338 first, class_2338 second, int color, float width) {
        SelectionOverlayRenderer.drawBox(context, lines, camera, Math.min(first.method_10263(), second.method_10263()), Math.min(first.method_10264(), second.method_10264()), Math.min(first.method_10260(), second.method_10260()), (double)Math.max(first.method_10263(), second.method_10263()) + 1.0, (double)Math.max(first.method_10264(), second.method_10264()) + 1.0, (double)Math.max(first.method_10260(), second.method_10260()) + 1.0, color, width);
    }

    private static void drawBox(WorldRenderContext context, class_4588 lines, class_243 camera, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color, float width) {
        class_265 shape = class_259.method_1078((class_238)new class_238(-0.002, -0.002, -0.002, maxX - minX + 0.002, maxY - minY + 0.002, maxZ - minZ + 0.002));
        class_9974.method_62296((class_4587)context.matrices(), (class_4588)lines, (class_265)shape, (double)(minX - camera.field_1352), (double)(minY - camera.field_1351), (double)(minZ - camera.field_1350), (int)color, (float)width);
    }
}


