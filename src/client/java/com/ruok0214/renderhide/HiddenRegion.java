/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 */
package com.ruok0214.renderhide;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;

@Environment(value=EnvType.CLIENT)
public record HiddenRegion(String name, String dimension, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean enabled) {
    public static HiddenRegion create(String name, String dimension, class_2338 a, class_2338 b) {
        return new HiddenRegion(name, dimension, Math.min(a.method_10263(), b.method_10263()), Math.min(a.method_10264(), b.method_10264()), Math.min(a.method_10260(), b.method_10260()), Math.max(a.method_10263(), b.method_10263()), Math.max(a.method_10264(), b.method_10264()), Math.max(a.method_10260(), b.method_10260()), true);
    }

    public boolean contains(class_2338 pos, String activeDimension) {
        return this.enabled && this.dimension.equals(activeDimension) && pos.method_10263() >= this.minX && pos.method_10263() <= this.maxX && pos.method_10264() >= this.minY && pos.method_10264() <= this.maxY && pos.method_10260() >= this.minZ && pos.method_10260() <= this.maxZ;
    }

    public HiddenRegion withEnabled(boolean value) {
        return new HiddenRegion(this.name, this.dimension, this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, value);
    }

    public long blockCount() {
        return (long)(this.maxX - this.minX + 1) * (long)(this.maxY - this.minY + 1) * (long)(this.maxZ - this.minZ + 1);
    }
}


