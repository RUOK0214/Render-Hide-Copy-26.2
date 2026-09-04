/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.BlockPos
 */
package com.ruok0214.renderhide;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;

@Environment(value=EnvType.CLIENT)
public record HiddenRegion(String name, String dimension, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean enabled) {
    public static HiddenRegion create(String name, String dimension, BlockPos a, BlockPos b) {
        return new HiddenRegion(name, dimension, Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()), Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()), true);
    }

    public boolean contains(BlockPos pos, String activeDimension) {
        return this.enabled && this.dimension.equals(activeDimension) && pos.getX() >= this.minX && pos.getX() <= this.maxX && pos.getY() >= this.minY && pos.getY() <= this.maxY && pos.getZ() >= this.minZ && pos.getZ() <= this.maxZ;
    }

    public HiddenRegion withEnabled(boolean value) {
        return new HiddenRegion(this.name, this.dimension, this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, value);
    }

    public long blockCount() {
        return (long)(this.maxX - this.minX + 1) * (long)(this.maxY - this.minY + 1) * (long)(this.maxZ - this.minZ + 1);
    }
}


