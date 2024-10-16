package com.moulberry.flashback.action;

import com.moulberry.flashback.playback.ReplayServer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface Action {

    ResourceLocation name();
    void handle(ReplayServer replayServer, FriendlyByteBuf friendlyByteBuf);

}
