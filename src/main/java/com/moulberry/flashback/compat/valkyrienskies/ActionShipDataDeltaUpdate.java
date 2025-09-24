package com.moulberry.flashback.compat.valkyrienskies;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.action.Action;
import com.moulberry.flashback.playback.ReplayServer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon;
import org.valkyrienskies.core.impl.game.ships.ShipObject;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;
import org.valkyrienskies.core.impl.util.serialization.VSJacksonUtil;
import org.valkyrienskies.mod.common.IShipObjectWorldServerProvider;

import java.io.IOException;

public class ActionShipDataDeltaUpdate implements Action {
    private static final ResourceLocation NAME = Flashback.createResourceLocation("action/valkyrien_skies_ship_data_delta_update");
    public static final ActionShipDataDeltaUpdate INSTANCE = new ActionShipDataDeltaUpdate();
    private ActionShipDataDeltaUpdate() {
    }

    @Override
    public ResourceLocation name() {
        return NAME;
    }

    @Override
    public void handle(ReplayServer replayServer, FriendlyByteBuf friendlyByteBuf) {
        var shipWorld = ((ShipObjectServerWorld) ((IShipObjectWorldServerProvider) replayServer).getShipObjectWorld());
        while (friendlyByteBuf.isReadable()) {
            try {
                long id = friendlyByteBuf.readLong();
                var ship = shipWorld.getAllShips().getById(id);

                var oldNode = (ObjectNode) VSJacksonUtil.INSTANCE.getDeltaMapper().valueToTree(ship);
                var newNode = (ObjectNode) ShipObject.getJsonDiffDeltaAlgorithm().apply(oldNode, friendlyByteBuf);

                oldNode.set("transform", newNode.get("transform"));
                oldNode.set("prevTickTransform", newNode.get("prevTickTransform"));

                VSJacksonUtil.INSTANCE.getDeltaMapper()
                        .readerFor(ShipDataCommon.class)
                        .withValueToUpdate(ship)
                        .readValue(oldNode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
//        friendlyByteBuf.skipBytes(friendlyByteBuf.readableBytes());
    }

}
