package com.example.scoutkit.freecam;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Freecam state. While enabled:
 *  - the camera is driven by x/y/z/yaw/pitch here (see CameraMixin)
 *  - the real player's input is replaced with an empty Input, so the body stands still
 *  - mouse look is redirected to the camera (EntityMixin)
 *  - attacking / using items / breaking blocks is blocked (MinecraftClientMixin)
 *  - it auto-disables if the real player takes damage
 */
public final class Freecam {
    private Freecam() {}

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    private static boolean enabled;
    private static double x, y, z;
    private static float yaw, pitch;
    private static long lastNanos;

    public static double baseSpeed = 12.0; // blocks per second

    public static boolean isEnabled() { return enabled; }
    public static double x() { return x; }
    public static double y() { return y; }
    public static double z() { return z; }
    public static float yaw() { return yaw; }
    public static float pitch() { return pitch; }

    public static void toggle() {
        if (enabled) disable(); else enable();
    }

    public static void enable() {
        ClientPlayerEntity p = MC.player;
        if (enabled || p == null || MC.world == null) return;
        Vec3d eye = p.getEyePos();
        x = eye.x; y = eye.y; z = eye.z;
        yaw = p.getYaw();
        pitch = p.getPitch();
        p.input = new Input(); // all-zero input: character stays put
        lastNanos = System.nanoTime();
        enabled = true;
    }

    public static void disable() {
        if (!enabled) return;
        enabled = false;
        if (MC.player != null) MC.player.input = new KeyboardInput(MC.options);
    }

    /** Called on disconnect: just drop state, the old player object is gone. */
    public static void reset() {
        enabled = false;
    }

    public static void tick() {
        if (!enabled) return;
        ClientPlayerEntity p = MC.player;
        if (p == null || MC.world == null) { enabled = false; return; }
        if (p.hurtTime > 0) disable(); // safety: you are being hit, snap back to your body
    }

    /** Mouse movement redirected from Entity#changeLookDirection. */
    public static void look(double dx, double dy) {
        yaw += (float) dx * 0.15f;
        pitch = MathHelper.clamp(pitch + (float) dy * 0.15f, -90f, 90f);
    }

    /** Called once per rendered frame (from CameraMixin) so movement is smooth at any FPS. */
    public static void updateFrame() {
        long now = System.nanoTime();
        double dt = Math.min((now - lastNanos) / 1_000_000_000.0, 0.1);
        lastNanos = now;
        if (MC.currentScreen != null) return; // no flying while a menu is open

        var o = MC.options;
        double speed = baseSpeed * (o.sprintKey.isPressed() ? 3.0 : 1.0) * dt;

        Vec3d forward = Vec3d.fromPolar(pitch, yaw);
        Vec3d right = Vec3d.fromPolar(0, yaw + 90f);
        Vec3d move = Vec3d.ZERO;
        if (o.forwardKey.isPressed()) move = move.add(forward);
        if (o.backKey.isPressed()) move = move.subtract(forward);
        if (o.rightKey.isPressed()) move = move.add(right);
        if (o.leftKey.isPressed()) move = move.subtract(right);
        if (o.jumpKey.isPressed()) move = move.add(0, 1, 0);
        if (o.sneakKey.isPressed()) move = move.add(0, -1, 0);

        if (move.lengthSquared() > 1.0E-6) {
            move = move.normalize().multiply(speed);
            x += move.x; y += move.y; z += move.z;
        }
    }
}
