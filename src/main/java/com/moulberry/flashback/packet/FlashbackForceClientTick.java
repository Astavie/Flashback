package com.moulberry.flashback.packet;

import com.moulberry.flashback.Flashback;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;

public class FlashbackForceClientTick implements FabricPacket {
    public static final FlashbackForceClientTick INSTANCE = new FlashbackForceClientTick();
    public static final PacketType<FlashbackForceClientTick> TYPE = PacketType.create(Flashback.createResourceLocation("force_client_tick"), buf -> INSTANCE);

    private FlashbackForceClientTick() {
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
