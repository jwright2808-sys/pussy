package com.example.scoutkit.render;

import com.example.scoutkit.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class Renderers {
    private Renderers() {}

    public static void render(WorldRenderContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        boolean esp = ModConfig.playerEsp && ModConfig.espBoxes;
        boolean act = ModConfig.activity;
        if (!esp && !act) return;

        MatrixStack ms = ctx.matrixStack();
        if (ms == null) return;

        Vec3d cam = ctx.camera().getPos();
        float tickDelta = ctx.tickCounter().getTickDelta(false);
        Matrix4f m = ms.peek().getPositionMatrix();

        RenderUtil.begin();
        BufferBuilder buf = RenderUtil.buffer();
        if (esp) EspRenderer.draw(buf, m, cam, mc, tickDelta);
        if (act) ActivityRenderer.draw(buf, m, cam);
        RenderUtil.finish(buf);
    }
}
