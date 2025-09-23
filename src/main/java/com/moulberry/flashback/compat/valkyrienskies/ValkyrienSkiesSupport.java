package com.moulberry.flashback.compat.valkyrienskies;

import com.moulberry.flashback.action.Action;
import com.moulberry.flashback.action.ActionRegistry;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;

public class ValkyrienSkiesSupport {

    public static void register() {
        ActionRegistry.register(ActionShipDataDeltaUpdate.INSTANCE);
        ActionRegistry.register(ActionShipDataCreate.INSTANCE);
        ActionRegistry.register(ActionShipRemove.INSTANCE);
    }

    public static void clearDataForPlayingSnapshot(ServerShipWorldCore world) {
        // preventing a ConcurrentModificationException here
        for (var ship : world.getAllShips().stream().toList()) {
            world.deleteShip(ship);
        }
    }

    @Nullable
    public static Action getAction(FriendlyByteBuf data) {
        Action action = switch (data.getInt(data.readerIndex())) {
            case 0 -> ActionShipDataDeltaUpdate.INSTANCE;
            case 3 -> ActionShipDataCreate.INSTANCE;
            case 4 -> ActionShipRemove.INSTANCE;
            default -> null;
        };
        if (action != null) {
            data.readInt();
        }
        return action;
    }

}
