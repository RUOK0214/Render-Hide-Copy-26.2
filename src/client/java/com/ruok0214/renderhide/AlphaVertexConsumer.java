/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4588
 */
package com.ruok0214.renderhide;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4588;

@Environment(value=EnvType.CLIENT)
public final class AlphaVertexConsumer
implements class_4588 {
    private final class_4588 delegate;
    private final float opacity;

    public AlphaVertexConsumer(class_4588 delegate, float opacity) {
        this.delegate = delegate;
        this.opacity = Math.max(0.0f, Math.min(1.0f, opacity));
    }

    private int alpha(int value) {
        return Math.max(0, Math.min(255, Math.round((float)value * this.opacity)));
    }

    public class_4588 method_22912(float x, float y, float z) {
        this.delegate.method_22912(x, y, z);
        return this;
    }

    public class_4588 method_1336(int red, int green, int blue, int alpha) {
        this.delegate.method_1336(red, green, blue, this.alpha(alpha));
        return this;
    }

    public class_4588 method_39415(int color) {
        int adjusted = color & 0xFFFFFF | this.alpha(color >>> 24 & 0xFF) << 24;
        this.delegate.method_39415(adjusted);
        return this;
    }

    public class_4588 method_22913(float u, float v) {
        this.delegate.method_22913(u, v);
        return this;
    }

    public class_4588 method_60796(int u, int v) {
        this.delegate.method_60796(u, v);
        return this;
    }

    public class_4588 method_22921(int u, int v) {
        this.delegate.method_22921(u, v);
        return this;
    }

    public class_4588 method_22914(float x, float y, float z) {
        this.delegate.method_22914(x, y, z);
        return this;
    }

    public class_4588 method_75298(float width) {
        this.delegate.method_75298(width);
        return this;
    }
}


