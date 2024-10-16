package com.moulberry.flashback.mixin.playback;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "turnPlayer", at = @At(value = "HEAD"), cancellable = true)
    public void turnPlayer_noPlayer(CallbackInfo ci) {
        if (minecraft.player == null) {
            ci.cancel();
        }
    }

}
