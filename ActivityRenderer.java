package com.example.scoutkit.render;

import com.example.scoutkit.ModConfig;
import com.example.scoutkit.activity.ActivityTracker;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;
import java.util.Map;

public final class ActivityRenderer {
    private ActivityRenderer() {}

    public static void draw(BufferBuilder buf, Matrix4f m, Vec3d cam) {
        long now = System.currentTimeMillis();

        // 1) chunk columns, coloured by heat (green -> red) and faded by age
        for (Map.Entry<Long, ActivityTracker.ChunkStat> e : ActivityTracker.chunks().entrySet()) {
            ActivityTracker.ChunkStat s = e.getValue();
            if (s.count < ModConfig.activityThreshold) continue;

            ChunkPos cp = new ChunkPos(e.getKey());
            float heat = Math.min(1f, s.count / (ModConfig.activityThreshold * 6f));
            float hue = (1f - heat) * 0.33f; // 0.33 = green, 0 = red
            int rgb = Color.HSBtoRGB(hue, 1f, 1f);

            double age = (now - s.lastTime) / (double) ModConfig.activityTtlMs; // 0 = fresh, 1 = about to expire
            int alpha = (int) (255 - 150 * Math.min(1.0, age));
            int color = (alpha << 24) | (rgb & 0xFFFFFF);

            double x0 = cp.getStartX(), z0 = cp.getStartZ();
            RenderUtil.box(buf, m, cam, x0, s.minY, z0, x0 + 16, s.maxY + 1, z0 + 16, color);
        }

        // 2) individual recently mined blocks (small cyan boxes = the actual tunnel shape)
        for (ActivityTracker.Hit h : ActivityTracker.recent()) {
            double age = (now - h.time()) / (double) ModConfig.activityTtlMs;
            int alpha = (int) (230 - 160 * Math.min(1.0, age));
            int color = RenderUtil.argb(alpha, 60, 220, 255);
            var p = h.pos();
            RenderUtil.box(buf, m, cam, p.getX(), p.getY(), p.getZ(), p.getX() + 1, p.getY() + 1, p.getZ() + 1, color);
        }
    }
}
