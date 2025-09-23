package com.moulberry.flashback.mixin.compat.valkyrienskies;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.flashback.Flashback;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.shadow.Aj;
import org.valkyrienskies.core.impl.shadow.Aq;

@IfModLoaded("valkyrienskies")
@Pseudo
@Mixin(value = org.valkyrienskies.core.impl.shadow.At.class, remap = false)
public abstract class MixinVSPipeline {

    @Shadow(remap = false)
    public abstract ServerShipWorldCore getShipWorld();

    @WrapMethod(method = "getArePhysicsRunning", remap = false)
    public boolean getArePhysicsRunning(Operation<Boolean> original) {
        // Do not run the physics engine
        return !Flashback.isInReplay() && original.call();
    }

    @WrapWithCondition(method = "postTickGame", at = @At(value = "INVOKE", target = "Lorg/valkyrienskies/core/impl/shadow/Aq;a(Lorg/valkyrienskies/core/impl/shadow/Aj;)V"), remap = false)
    public boolean postTickGame(Aq aq, Aj aj) {
        // Do not accumulate game frames
        return !Flashback.isInReplay();
    }

}
