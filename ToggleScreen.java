package com.example.scoutkit.gui;

import com.example.scoutkit.ModConfig;
import com.example.scoutkit.activity.ActivityTracker;
import com.example.scoutkit.freecam.Freecam;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ToggleScreen extends Screen {
    private static final int W = 210, H = 20, GAP = 24;

    public ToggleScreen() {
        super(Text.literal("ScoutKit"));
    }

    @Override
    protected void init() {
        int x = this.width / 2 - W / 2;
        int y = this.height / 2 - 78;

        addDrawableChild(toggle(x, y, "Player ESP", () -> ModConfig.playerEsp, v -> ModConfig.playerEsp = v)); y += GAP;
        addDrawableChild(toggle(x, y, "  ESP boxes", () -> ModConfig.espBoxes, v -> ModConfig.espBoxes = v)); y += GAP;
        addDrawableChild(toggle(x, y, "  ESP glow outline", () -> ModConfig.espGlow, v -> ModConfig.espGlow = v)); y += GAP;
        addDrawableChild(toggle(x, y, "SUS chunks / activity", () -> ModConfig.activity, v -> ModConfig.activity = v)); y += GAP;
        addDrawableChild(toggle(x, y, "Freecam", Freecam::isEnabled, v -> {
            if (v) Freecam.enable(); else Freecam.disable();
        })); y += GAP;
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear activity data"), b -> ActivityTracker.clear())
                .dimensions(x, y, W, H).build()); y += GAP;
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(x, y, W, H).build());
    }

    private static ButtonWidget toggle(int x, int y, String label, BooleanSupplier get, Consumer<Boolean> set) {
        return ButtonWidget.builder(labelFor(label, get.getAsBoolean()), b -> {
            set.accept(!get.getAsBoolean());
            // re-read in case the setter refused (e.g. freecam without a world)
            b.setMessage(labelFor(label, get.getAsBoolean()));
        }).dimensions(x, y, W, H).build();
    }

    private static Text labelFor(String label, boolean on) {
        return Text.literal(label + ": ")
                .append(Text.literal(on ? "ON" : "OFF").formatted(on ? Formatting.GREEN : Formatting.RED));
    }

    /** No vanilla blur/dim background: avoids the "blur twice per frame" crash and keeps the world visible. */
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int left = this.width / 2 - W / 2 - 10;
        int top = this.height / 2 - 100;
        ctx.fill(left, top, left + W + 20, this.height / 2 + 100, 0xB0101018);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, top + 8, 0xFFFFFF);
        super.render(ctx, mouseX, mouseY, delta);
    }

    /** Keep the world running so freecam/ESP stay live while the menu is open. */
    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
