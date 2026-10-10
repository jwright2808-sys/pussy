package com.example.scoutkit.mixin;

import com.example.scoutkit.ModConfig;
import com.example.scoutkit.freecam.Freecam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    /** Vanilla "glowing" outline is drawn through walls, so it makes a free, cheap ESP. Client-side only. */
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void scoutkit$glow(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (ModConfig.playerEsp && ModConfig.espGlow
                && self.getWorld().isClient
                && self instanceof PlayerEntity
                && self != MinecraftClient.getInstance().player) {
            cir.setReturnValue(true);
        }
    }

    /** Redirect mouse look to the freecam camera so the real player does not turn. */
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void scoutkit$look(double dx, double dy, CallbackInfo ci) {
        if (Freecam.isEnabled() && (Object) this == MinecraftClient.getInstance().player) {
            Freecam.look(dx, dy);
            ci.cancel();
        }
    }
}
