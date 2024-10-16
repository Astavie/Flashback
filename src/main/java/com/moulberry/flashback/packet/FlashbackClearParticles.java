package com.moulberry.flashback.packet;

import com.moulberry.flashback.Flashback;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;

public class FlashbackClearParticles implements FabricPacket {
    public static final FlashbackClearParticles INSTANCE = new FlashbackClearParticles();
    public static final PacketType<FlashbackClearParticles> TYPE = PacketType.create(Flashback.createResourceLocation("clear_particles"), buf -> INSTANCE);

    private FlashbackClearParticles() {
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
