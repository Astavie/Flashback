package com.moulberry.flashback.ext;

import com.moulberry.flashback.playback.ReplayTimer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.nio.file.Path;
import java.util.UUID;

public interface MinecraftExt {

    void flashback$applyKeyframes();
    void flashback$startReplayServer(LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem stem, UUID playbackUUID, Path path);
    float flashback$getLocalPlayerPartialTick(float originalPartialTick);
    ReplayTimer flashback$getReplayTimer();

}
