package com.moulberry.flashback.mixin.playback;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.ext.MinecraftExt;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class MixinLevel {

    @Shadow
    @Final
    public boolean isClientSide;

    private boolean runsNormally = true;

    @Inject(method="tickBlockEntities", at=@At("HEAD"))
    public void tickBlockEntities(CallbackInfo ci) {
        if (isClientSide && Flashback.isInReplay()) {
            runsNormally = ((MinecraftExt) Minecraft.getInstance()).flashback$getReplayTimer().manager.runsNormally();
        } else {
            runsNormally = true;
        }
    }

    @WrapWithCondition(method="tickBlockEntities", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V"))
    public boolean tickBlockEntity(TickingBlockEntity instance) {
        return runsNormally;
    }

    @WrapOperation(method="tickBlockEntities", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;shouldTickBlocksAt(Lnet/minecraft/core/BlockPos;)Z"))
    public boolean shouldTickBlocksAt(Level instance, BlockPos pos, Operation<Boolean> original) {
        return (isClientSide || !Flashback.isInReplay()) && original.call(instance, pos);
    }

}
