package com.moulberry.flashback.mixin.compat.fabric;

import com.moulberry.flashback.playback.FakePlayer;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayNetworking.class, remap = false)
public class MixinFabricServerPlayNetworking {

    @Inject(method = "canSend(Lnet/minecraft/server/network/ServerGamePacketListenerImpl;Lnet/fabricmc/fabric/api/networking/v1/PacketType;)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private static void canSend1(ServerGamePacketListenerImpl handler, PacketType<?> type, CallbackInfoReturnable<Boolean> cir) {
        if (handler.player instanceof FakePlayer) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canSend(Lnet/minecraft/server/network/ServerGamePacketListenerImpl;Lnet/minecraft/resources/ResourceLocation;)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private static void canSend2(ServerGamePacketListenerImpl handler, ResourceLocation channelName, CallbackInfoReturnable<Boolean> cir) {
        if (handler.player instanceof FakePlayer) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "send(Lnet/minecraft/server/level/ServerPlayer;Lnet/fabricmc/fabric/api/networking/v1/FabricPacket;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private static <T extends FabricPacket> void send(ServerPlayer player, T packet, CallbackInfo ci) {
        if (player instanceof FakePlayer) {
            ci.cancel();
        }
    }

}
