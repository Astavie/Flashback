package com.moulberry.flashback.packet;

import com.moulberry.flashback.Flashback;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;

public class FinishedServerTick implements FabricPacket {
    public static final FinishedServerTick INSTANCE = new FinishedServerTick();
    public static final PacketType<FinishedServerTick> TYPE = PacketType.create(Flashback.createResourceLocation("finished_server_tick"), buf -> INSTANCE);

    private FinishedServerTick() {
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
