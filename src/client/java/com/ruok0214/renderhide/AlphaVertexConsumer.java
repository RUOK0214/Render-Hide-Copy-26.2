/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.VertexConsumer
 */
package com.ruok0214.renderhide;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.blaze3d.vertex.VertexConsumer;

@Environment(value=EnvType.CLIENT)
public final class AlphaVertexConsumer
implements VertexConsumer {
    private final VertexConsumer delegate;
    private final float opacity;

    public AlphaVertexConsumer(VertexConsumer delegate, float opacity) {
        this.delegate = delegate;
        this.opacity = Math.max(0.0f, Math.min(1.0f, opacity));
    }

    private int alpha(int value) {
        return Math.max(0, Math.min(255, Math.round((float)value * this.opacity)));
    }

    public VertexConsumer addVertex(float x, float y, float z) {
        this.delegate.addVertex(x, y, z);
        return this;
    }

    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        this.delegate.setColor(red, green, blue, this.alpha(alpha));
        return this;
    }

    public VertexConsumer setColor(int color) {
        int adjusted = color & 0xFFFFFF | this.alpha(color >>> 24 & 0xFF) << 24;
        this.delegate.setColor(adjusted);
        return this;
    }

    public VertexConsumer setUv(float u, float v) {
        this.delegate.setUv(u, v);
        return this;
    }

    public VertexConsumer setUv1(int u, int v) {
        this.delegate.setUv1(u, v);
        return this;
    }

    public VertexConsumer setUv2(int u, int v) {
        this.delegate.setUv2(u, v);
        return this;
    }

    public VertexConsumer setNormal(float x, float y, float z) {
        this.delegate.setNormal(x, y, z);
        return this;
    }

    public VertexConsumer setLineWidth(float width) {
        this.delegate.setLineWidth(width);
        return this;
    }
}


