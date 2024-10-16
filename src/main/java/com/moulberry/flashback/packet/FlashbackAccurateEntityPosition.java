package com.moulberry.flashback.packet;

import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.action.PositionAndAngle;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record FlashbackAccurateEntityPosition(int entityId, List<PositionAndAngle> positionAndAngles) implements FabricPacket {
    public static final PacketType<FlashbackAccurateEntityPosition> TYPE = PacketType.create(Flashback.createResourceLocation("accurate_entity_position"), friendlyByteBuf -> {
        int entityId = friendlyByteBuf.readVarInt();
        int interpolatedCount = friendlyByteBuf.readVarInt();

        List<PositionAndAngle> interpolatedPositions = new ArrayList<>(interpolatedCount);
        for (int i = 0; i < interpolatedCount; i++) {
            double x = friendlyByteBuf.readDouble();
            double y = friendlyByteBuf.readDouble();
            double z = friendlyByteBuf.readDouble();
            float yaw = friendlyByteBuf.readFloat();
            float pitch = friendlyByteBuf.readFloat();

            interpolatedPositions.add(new PositionAndAngle(x, y, z, yaw, pitch));
        }

        return new FlashbackAccurateEntityPosition(entityId, interpolatedPositions);
    });

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(this.entityId);
        friendlyByteBuf.writeVarInt(this.positionAndAngles.size());
        for (PositionAndAngle interpolatedPosition : this.positionAndAngles) {
            friendlyByteBuf.writeDouble(interpolatedPosition.x());
            friendlyByteBuf.writeDouble(interpolatedPosition.y());
            friendlyByteBuf.writeDouble(interpolatedPosition.z());
            friendlyByteBuf.writeFloat(interpolatedPosition.yaw());
            friendlyByteBuf.writeFloat(interpolatedPosition.pitch());
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

}
