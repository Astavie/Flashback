package com.moulberry.flashback.compat.valkyrienskies;

import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.action.Action;
import com.moulberry.flashback.playback.ReplayServer;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;
import org.valkyrienskies.core.impl.networking.impl.PacketShipRemove;
import org.valkyrienskies.core.impl.networking.simple.SimplePackets;
import org.valkyrienskies.mod.common.IShipObjectWorldServerProvider;

public class ActionShipRemove implements Action {
    private static final ResourceLocation NAME = Flashback.createResourceLocation("action/valkyrien_skies_ship_remove");
    public static final ActionShipRemove INSTANCE = new ActionShipRemove();
    private ActionShipRemove() {
    }

    @Override
    public ResourceLocation name() {
        return NAME;
    }

    @Override
    public void handle(ReplayServer replayServer, FriendlyByteBuf friendlyByteBuf) {
        var kClass = (KClass<PacketShipRemove>) Reflection.createKotlinClass(PacketShipRemove.class);
        var packet = SimplePackets.deserialize(kClass, friendlyByteBuf);
        friendlyByteBuf.skipBytes(friendlyByteBuf.readableBytes());

        var shipWorld = ((ShipObjectServerWorld) ((IShipObjectWorldServerProvider) replayServer).getShipObjectWorld());
        for (var toRemove : packet.getToRemove()) {
            var ship = shipWorld.getAllShips().getById(toRemove);
            if (ship != null) {
                shipWorld.deleteShip(ship);
            }
        }
    }

}
