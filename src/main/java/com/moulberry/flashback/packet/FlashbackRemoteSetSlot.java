package com.moulberry.flashback.packet;

import com.moulberry.flashback.Flashback;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record FlashbackRemoteSetSlot(int entityId, int slot, ItemStack itemStack) implements FabricPacket {
    public static final PacketType<FlashbackRemoteSetSlot> TYPE = PacketType.create(Flashback.createResourceLocation("remote_set_slot"), friendlyByteBuf -> {
        int entityId = friendlyByteBuf.readVarInt();
        int slot = friendlyByteBuf.readByte();
        ItemStack itemStack = friendlyByteBuf.readItem();
        return new FlashbackRemoteSetSlot(entityId, slot, itemStack);
    });

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(this.entityId);
        friendlyByteBuf.writeByte(this.slot);
        friendlyByteBuf.writeItem(this.itemStack);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

}
