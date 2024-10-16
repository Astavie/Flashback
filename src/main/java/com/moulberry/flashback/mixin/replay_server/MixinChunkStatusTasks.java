package com.moulberry.flashback.mixin.replay_server;

import com.mojang.datafixers.util.Either;
import com.moulberry.flashback.Flashback;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ChunkStatus.class)
public class MixinChunkStatusTasks {

    @Inject(method = "initializeLight", at = @At("HEAD"), cancellable = true)
    private static void initializeLight(ThreadedLevelLightEngine threadedLevelLightEngine, ChunkAccess chunkAccess, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir) {
        if (Flashback.isInReplay()) {
            cir.setReturnValue(CompletableFuture.completedFuture(Either.left(chunkAccess)));
        }
    }

    @Inject(method = "lightChunk", at = @At("HEAD"), cancellable = true)
    private static void lightChunk(ThreadedLevelLightEngine threadedLevelLightEngine, ChunkAccess chunkAccess, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir) {
        if (Flashback.isInReplay()) {
            cir.setReturnValue(CompletableFuture.completedFuture(Either.left(chunkAccess)));
        }
    }

}
