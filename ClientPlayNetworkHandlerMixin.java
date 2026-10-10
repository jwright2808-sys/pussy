package com.example.scoutkit.mixin;

import com.example.scoutkit.ModConfig;
import com.example.scoutkit.activity.ActivityTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    // Packet handlers run once on the network thread (which re-queues them) and again on the
    // main thread. isOnThread() makes sure we only record once, before the world is modified.

    @Inject(method = "onBlockUpdate", at = @At("HEAD"))
    private void scoutkit$single(BlockUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (!ModConfig.activity || world == null || !mc.isOnThread()) return;
        ActivityTracker.onBlockChange(world, packet.getPos(), world.getBlockState(packet.getPos()), packet.getState());
    }

    @Inject(method = "onChunkDeltaUpdate", at = @At("HEAD"))
    private void scoutkit$multi(ChunkDeltaUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (!ModConfig.activity || world == null || !mc.isOnThread()) return;
        packet.visitUpdates((pos, state) ->
                ActivityTracker.onBlockChange(world, pos.toImmutable(), world.getBlockState(pos), state));
    }
}
