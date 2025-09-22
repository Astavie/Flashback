package com.moulberry.flashback.mixin.compat.valkyrienskies;

import com.moulberry.flashback.Flashback;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.entity.VSPhysicsEntity;

@IfModLoaded("valkyrienskies")
@Pseudo
@Mixin(value = VSPhysicsEntity.class, remap = false)
public class MixinVSPhysicsEntity {

    @Inject(method = "moveTo", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/Entity.moveTo (DDDFF)V", shift = At.Shift.AFTER), cancellable = true)
    private void moveTo(double d, double e, double f, float g, float h, CallbackInfo ci) {
        if (Flashback.isInReplay()) {
            ci.cancel();
        }
    }

    @Inject(method = "setLevelCallback", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/Entity.setLevelCallback (Lnet/minecraft/world/level/entity/EntityInLevelCallback;)V", shift = At.Shift.AFTER), cancellable = true)
    private void setLevelCallback(EntityInLevelCallback callback, CallbackInfo ci) {
        if (Flashback.isInReplay()) {
            ci.cancel();
        }
    }

}
