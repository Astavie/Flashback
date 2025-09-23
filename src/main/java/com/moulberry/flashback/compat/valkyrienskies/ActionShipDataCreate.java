package com.moulberry.flashback.compat.valkyrienskies;

import com.google.common.collect.MutableClassToInstanceMap;
import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.action.Action;
import com.moulberry.flashback.playback.ReplayServer;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.core.impl.game.ships.ShipInertiaDataImpl;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;
import org.valkyrienskies.core.impl.networking.impl.PacketShipDataCreate;
import org.valkyrienskies.core.impl.networking.simple.SimplePackets;
import org.valkyrienskies.mod.common.IShipObjectWorldServerProvider;

public class ActionShipDataCreate implements Action {
    private static final ResourceLocation NAME = Flashback.createResourceLocation("action/valkyrien_skies_ship_data_create");
    public static final ActionShipDataCreate INSTANCE = new ActionShipDataCreate();
    private ActionShipDataCreate() {
    }

    @Override
    public ResourceLocation name() {
        return NAME;
    }

    @Override
    public void handle(ReplayServer replayServer, FriendlyByteBuf friendlyByteBuf) {
        var kClass = (KClass<PacketShipDataCreate>) Reflection.createKotlinClass(PacketShipDataCreate.class);
        var packet = SimplePackets.deserialize(kClass, friendlyByteBuf);
        friendlyByteBuf.skipBytes(friendlyByteBuf.readableBytes());

        var shipWorld = ((ShipObjectServerWorld) ((IShipObjectWorldServerProvider) replayServer).getShipObjectWorld());
        for (var toCreate : packet.getToCreate()) {
            shipWorld.getAllShips().add(new ShipData(
                    toCreate.getId(),
                    toCreate.getSlug(),
                    toCreate.getChunkClaim(),
                    toCreate.getChunkClaimDimension(),
                    toCreate.getPhysicsData(),
                    // we need SOME mass for VS to load the ship
                    new ShipInertiaDataImpl(new Vector3d(), 1, new Matrix3d()),
                    toCreate.getTransform(),
                    toCreate.getPrevTickTransform(),
                    toCreate.getWorldAABB(),
                    toCreate.getShipAABB(),
                    toCreate.getActiveChunksSet(),
                    false,
                    false,
                    MutableClassToInstanceMap.create(),
                    0
            ));
        }
    }

}
