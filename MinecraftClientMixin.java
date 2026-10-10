package com.example.scoutkit.mixin;

import com.example.scoutkit.freecam.Freecam;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** While in freecam your body must not attack, place, use items or mine. */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void scoutkit$noAttack(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled()) cir.setReturnValue(false);
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void scoutkit$noUse(CallbackInfo ci) {
        if (Freecam.isEnabled()) ci.cancel();
    }

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void scoutkit$noBreak(boolean breaking, CallbackInfo ci) {
        if (Freecam.isEnabled()) ci.cancel();
    }
}
