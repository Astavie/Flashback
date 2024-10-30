package com.moulberry.flashback.mixin.compat.valkyrienskies;

import com.moulberry.flashback.Flashback;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils;

@IfModLoaded("valkyrienskies")
@Pseudo
@Mixin(value = EntityShipCollisionUtils.class, remap = false)
public class MixinEntityShipCollisionUtils {

    @Inject(method = "isCollidingWithUnloadedShips", at = @At("HEAD"), cancellable = true)
    private static void isCollidingWithUnloadedShips(Entity e, CallbackInfoReturnable<Boolean> ci) {
        if (Flashback.isInReplay()) {
            ci.setReturnValue(false);
        }
    }

}
