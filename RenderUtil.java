package com.example.scoutkit.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/** Draws depth-test-free (see-through-walls) wireframe boxes. Targets Minecraft 1.21.4. */
public final class RenderUtil {
    private RenderUtil() {}

    public static void begin() {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
    }

    public static BufferBuilder buffer() {
        return Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
    }

    public static void finish(BufferBuilder buf) {
        BuiltBuffer built = buf.endNullable();
        if (built != null) BufferRenderer.drawWithGlobalProgram(built);
        RenderSystem.lineWidth(1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    public static int argb(int a, int r, int g, int b) {
        return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
    }

    /** World-space box, converted to camera-relative coordinates here to keep float precision at large coordinates. */
    public static void box(BufferBuilder b, Matrix4f m, Vec3d cam,
                           double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        float ax = (float) (x1 - cam.x), ay = (float) (y1 - cam.y), az = (float) (z1 - cam.z);
        float bx = (float) (x2 - cam.x), by = (float) (y2 - cam.y), bz = (float) (z2 - cam.z);

        // bottom
        line(b, m, ax, ay, az, bx, ay, az, color);
        line(b, m, bx, ay, az, bx, ay, bz, color);
        line(b, m, bx, ay, bz, ax, ay, bz, color);
        line(b, m, ax, ay, bz, ax, ay, az, color);
        // top
        line(b, m, ax, by, az, bx, by, az, color);
        line(b, m, bx, by, az, bx, by, bz, color);
        line(b, m, bx, by, bz, ax, by, bz, color);
        line(b, m, ax, by, bz, ax, by, az, color);
        // verticals
        line(b, m, ax, ay, az, ax, by, az, color);
        line(b, m, bx, ay, az, bx, by, az, color);
        line(b, m, bx, ay, bz, bx, by, bz, color);
        line(b, m, ax, ay, bz, ax, by, bz, color);
    }

    private static void line(BufferBuilder b, Matrix4f m,
                             float x1, float y1, float z1, float x2, float y2, float z2, int color) {
        b.vertex(m, x1, y1, z1).color(color);
        b.vertex(m, x2, y2, z2).color(color);
    }
}
