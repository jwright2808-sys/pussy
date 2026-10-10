package com.example.scoutkit.activity;

import com.example.scoutkit.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Remembers underground blocks that were removed (mined) while they were inside your loaded range.
 * Everything is derived from block-update packets the server already sends to your client.
 */
public final class ActivityTracker {
    private ActivityTracker() {}

    public static final class ChunkStat {
        public int count;
        public long lastTime;
        public int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
    }

    public record Hit(BlockPos pos, long time) {}

    private static final Map<Long, ChunkStat> CHUNKS = new HashMap<>();
    private static final ArrayDeque<Hit> RECENT = new ArrayDeque<>();
    private static final int MAX_RECENT = 600;
    private static int tickCounter;

    public static Map<Long, ChunkStat> chunks() { return CHUNKS; }
    public static Iterable<Hit> recent() { return RECENT; }

    public static void onBlockChange(ClientWorld world, BlockPos pos, BlockState oldState, BlockState newState) {
        // a solid-ish block turned into air => mined / exploded / removed
        if (oldState.isAir() || !newState.isAir()) return;
        if (!oldState.getFluidState().isEmpty()) return;

        int surface = world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ());
        if (surface - pos.getY() < ModConfig.undergroundDepth) return;

        long now = System.currentTimeMillis();
        ChunkStat s = CHUNKS.computeIfAbsent(new ChunkPos(pos).toLong(), k -> new ChunkStat());
        s.count++;
        s.lastTime = now;
        s.minY = Math.min(s.minY, pos.getY());
        s.maxY = Math.max(s.maxY, pos.getY());

        RECENT.addLast(new Hit(pos.toImmutable(), now));
        while (RECENT.size() > MAX_RECENT) RECENT.pollFirst();
    }

    public static void tick() {
        if (++tickCounter % 20 != 0) return;
        long cutoff = System.currentTimeMillis() - ModConfig.activityTtlMs;
        for (Iterator<ChunkStat> it = CHUNKS.values().iterator(); it.hasNext(); ) {
            if (it.next().lastTime < cutoff) it.remove();
        }
        while (!RECENT.isEmpty() && RECENT.peekFirst().time() < cutoff) RECENT.pollFirst();
    }

    public static void clear() {
        CHUNKS.clear();
        RECENT.clear();
    }
}
