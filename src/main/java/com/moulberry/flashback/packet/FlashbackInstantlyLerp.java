package com.moulberry.flashback.packet;

import com.moulberry.flashback.Flashback;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;

public class FlashbackInstantlyLerp implements FabricPacket {
    public static final FlashbackInstantlyLerp INSTANCE = new FlashbackInstantlyLerp();
    public static final PacketType<FlashbackInstantlyLerp> TYPE = PacketType.create(Flashback.createResourceLocation("instantly_lerp"), buf -> INSTANCE);

    private FlashbackInstantlyLerp() {
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
