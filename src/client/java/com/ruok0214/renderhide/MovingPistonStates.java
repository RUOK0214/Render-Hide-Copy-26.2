package com.ruok0214.renderhide;

import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Publishes immutable moved states; render workers never retain live block entities. */
public final class MovingPistonStates {
    private record Snapshot(ClientLevel level, ConcurrentHashMap<BlockPos, BlockState> states) {}
    private static volatile Snapshot snapshot = new Snapshot(null, new ConcurrentHashMap<>());

    private MovingPistonStates() {}

    public static void useLevel(ClientLevel level) {
        if (!Minecraft.getInstance().isSameThread()) return;
        if (snapshot.level() != level) {
            snapshot = new Snapshot(level, new ConcurrentHashMap<>());
        }
    }

    /** Called after the chunk installs a fully initialized block entity. */
    public static void put(Level level, BlockEntity entity) {
        if (!(level instanceof ClientLevel clientLevel)
                || !Minecraft.getInstance().isSameThread()) return;
        useLevel(clientLevel);
        Snapshot current = snapshot;
        BlockPos pos = entity.getBlockPos().immutable();
        if (entity instanceof PistonMovingBlockEntity piston) {
            current.states().put(pos, piston.getMovedState());
        } else {
            current.states().remove(pos);
        }
    }

    public static void remove(Level level, BlockPos pos) {
        Snapshot current = snapshot;
        if (current.level() == level) current.states().remove(pos);
    }

    public static void removeChunk(ClientLevel level, ChunkPos chunk) {
        Snapshot current = snapshot;
        if (current.level() != level) return;
        current.states().keySet().removeIf(pos ->
                (pos.getX() >> 4) == chunk.x && (pos.getZ() >> 4) == chunk.z);
    }

    public static BlockState get(ClientLevel level, BlockPos pos) {
        Snapshot current = snapshot;
        return level != null && current.level() == level ? current.states().get(pos) : null;
    }
}
