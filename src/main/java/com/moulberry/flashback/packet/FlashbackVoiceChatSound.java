package com.moulberry.flashback.packet;

import com.moulberry.flashback.Flashback;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public interface FlashbackVoiceChatSound extends FabricPacket {
    byte TYPE_STATIC_SOUND = 0;
    byte TYPE_LOCATIONAL_SOUND = 1;
    byte TYPE_ENTITY_SOUND = 2;

    PacketType<FlashbackVoiceChatSound> TYPE = PacketType.create(Flashback.createResourceLocation("voice_chat_sound"), friendlyByteBuf -> {
        UUID uuid = friendlyByteBuf.readUUID();

        int sampleCount = friendlyByteBuf.readVarInt();
        short[] samples = new short[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            samples[i] = friendlyByteBuf.readShort();
        }

        byte type = friendlyByteBuf.readByte();

        switch (type) {
            case TYPE_STATIC_SOUND -> {
                return new SoundStatic(uuid, samples);
            }
            case TYPE_LOCATIONAL_SOUND -> {
                double x = friendlyByteBuf.readDouble();
                double y = friendlyByteBuf.readDouble();
                double z = friendlyByteBuf.readDouble();
                Vec3 position = new Vec3(x, y, z);
                float distance = friendlyByteBuf.readFloat();
                return new SoundLocational(uuid, samples, position, distance);
            }
            case TYPE_ENTITY_SOUND -> {
                boolean whispering = friendlyByteBuf.readBoolean();
                float distance = friendlyByteBuf.readFloat();
                return new SoundEntity(uuid, samples, whispering, distance);
            }
            default -> throw new DecoderException("Unknown voice chat type: " + type);
        }
    });

    UUID source();
    short[] samples();
    void writeExtraData(FriendlyByteBuf friendlyByteBuf);

    @Override
    default PacketType<?> getType() {
        return TYPE;
    }

    @Override
    default void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(this.source());

        short[] samples = this.samples();
        friendlyByteBuf.writeVarInt(samples.length);
        for (short sample : samples) {
            friendlyByteBuf.writeShort(sample);
        }

        this.writeExtraData(friendlyByteBuf);
    }

    record SoundStatic(UUID source, short[] samples) implements FlashbackVoiceChatSound {
        @Override
        public void writeExtraData(FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeByte(TYPE_STATIC_SOUND);
        }
    }

    record SoundLocational(UUID source, short[] samples, Vec3 position, float distance) implements FlashbackVoiceChatSound {
        @Override
        public void writeExtraData(FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeByte(TYPE_LOCATIONAL_SOUND);
            friendlyByteBuf.writeDouble(this.position.x);
            friendlyByteBuf.writeDouble(this.position.y);
            friendlyByteBuf.writeDouble(this.position.z);
            friendlyByteBuf.writeFloat(this.distance);
        }
    }

    record SoundEntity(UUID source, short[] samples, boolean whispering, float distance) implements FlashbackVoiceChatSound {
        @Override
        public void writeExtraData(FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeByte(TYPE_ENTITY_SOUND);
            friendlyByteBuf.writeBoolean(this.whispering);
            friendlyByteBuf.writeFloat(this.distance);
        }
    }

}
