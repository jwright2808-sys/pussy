package com.example.scoutkit.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class EspRenderer {
    private EspRenderer() {}

    public static void draw(BufferBuilder buf, Matrix4f m, Vec3d cam, MinecraftClient mc, float tickDelta) {
        for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player || p.isSpectator() || p.isRemoved()) continue;

            // interpolate so the box doesn't stutter at 20 TPS
            Vec3d lerped = p.getLerpedPos(tickDelta);
            Box bb = p.getBoundingBox().offset(lerped.subtract(p.getPos())).expand(0.02);

            // closer players = more red, farther = more yellow
            double dist = Math.sqrt(mc.player.squaredDistanceTo(p));
            int g = (int) Math.min(220, dist * 2.5);
            int color = RenderUtil.argb(255, 255, g, 40);

            RenderUtil.box(buf, m, cam, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color);
        }
    }
}
