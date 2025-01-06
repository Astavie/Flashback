package com.moulberry.flashback.mixin.playback;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.ext.MinecraftExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ClientLevel.class)
public class MixinClientLevel {

    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapWithCondition(method = "method_32124", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;guardEntityTick(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/Entity;)V"))
    public <T extends Entity> boolean tickEntity(ClientLevel instance, Consumer<T> consumer, T entity) {
        return !Flashback.isInReplay() || !((MinecraftExt) minecraft).flashback$getReplayTimer().manager.isEntityFrozen(entity);
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;tickTime()V"))
    public boolean tickTime(ClientLevel instance) {
        return !Flashback.isInReplay() || ((MinecraftExt) minecraft).flashback$getReplayTimer().manager.runsNormally();
    }

}
