package com.moulberry.flashback.mixin.playback;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.playback.ReplayPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class MixinServerLevel {

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/border/WorldBorder;tick()V"))
    public boolean tickWorldBorder(WorldBorder instance) {
        return !Flashback.isInReplay();
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;advanceWeatherCycle()V"))
    public boolean tickWeatherCycle(ServerLevel instance) {
        return !Flashback.isInReplay();
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickTime()V"))
    public boolean tickTime(ServerLevel instance) {
        return !Flashback.isInReplay();
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isDebug()Z"))
    public boolean tickBlocksAndFluids(ServerLevel instance, Operation<Boolean> original) {
        return Flashback.isInReplay() || original.call(instance);
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raids;tick()V"))
    public boolean tickRaids(Raids instance) {
        return !Flashback.isInReplay();
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;runBlockEvents()V"))
    public boolean tickBlockEvents(ServerLevel instance) {
        return !Flashback.isInReplay();
    }

    @Inject(method = "method_31420", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", ordinal = 0), cancellable = true)
    public void tickEntity(ProfilerFiller filler, Entity entity, CallbackInfo ci) {
        if (Flashback.isInReplay() && !(entity instanceof ReplayPlayer)) {
            ci.cancel();
        }
    }

}
