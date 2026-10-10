package com.example.scoutkit;

import com.example.scoutkit.activity.ActivityTracker;
import com.example.scoutkit.freecam.Freecam;
import com.example.scoutkit.gui.ToggleScreen;
import com.example.scoutkit.render.Renderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ScoutKitClient implements ClientModInitializer {
    public static KeyBinding MENU_KEY;
    public static KeyBinding FREECAM_KEY;

    @Override
    public void onInitializeClient() {
        MENU_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.scoutkit.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.scoutkit"));
        FREECAM_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.scoutkit.freecam", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F6, "category.scoutkit"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (MENU_KEY.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new ToggleScreen());
            }
            while (FREECAM_KEY.wasPressed()) {
                Freecam.toggle();
            }
            Freecam.tick();
            ActivityTracker.tick();
        });

        WorldRenderEvents.LAST.register(Renderers::render);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            Freecam.reset();
            ActivityTracker.clear();
        });
    }
}
