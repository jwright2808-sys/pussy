package com.example.scoutkit.mixin;

import com.example.scoutkit.freecam.Freecam;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private boolean thirdPerson;
    @Shadow protected abstract void setPos(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    private void scoutkit$freecam(BlockView area, Entity focused, boolean third, boolean inverse,
                                  float tickDelta, CallbackInfo ci) {
        if (!Freecam.isEnabled()) return;
        Freecam.updateFrame();
        setRotation(Freecam.yaw(), Freecam.pitch());
        setPos(Freecam.x(), Freecam.y(), Freecam.z());
        this.thirdPerson = true; // makes the game render your real body where you left it
    }
}
