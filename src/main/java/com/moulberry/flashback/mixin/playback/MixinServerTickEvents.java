package com.moulberry.flashback.mixin.playback;

import com.moulberry.flashback.playback.ReplayServer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerTickEvents.class, remap = false)
public class MixinServerTickEvents {

    @Inject(method = "lambda$static$0", at = @At("HEAD"), cancellable = true, remap = false)
    private static void startServerTick(ServerTickEvents.StartTick[] callbacks, MinecraftServer server, CallbackInfo ci) {
        if (server instanceof ReplayServer) {
            ci.cancel();
        }
    }

    @Inject(method = "lambda$static$2", at = @At("HEAD"), cancellable = true, remap = false)
    private static void endServerTick(ServerTickEvents.EndTick[] callbacks, MinecraftServer server, CallbackInfo ci) {
        if (server instanceof ReplayServer) {
            ci.cancel();
        }
    }

    @Inject(method = "lambda$static$4", at = @At("HEAD"), cancellable = true, remap = false)
    private static void startWorldTick(ServerTickEvents.StartWorldTick[] callbacks, ServerLevel level, CallbackInfo ci) {
        if (level.getServer() instanceof ReplayServer) {
            ci.cancel();
        }
    }

    @Inject(method = "lambda$static$6", at = @At("HEAD"), cancellable = true, remap = false)
    private static void endWorldTick(ServerTickEvents.EndWorldTick[] callbacks, ServerLevel level, CallbackInfo ci) {
        if (level.getServer() instanceof ReplayServer) {
            ci.cancel();
        }
    }

}
