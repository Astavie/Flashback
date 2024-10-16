package com.moulberry.flashback.mixin.playback;

import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.packet.FinishedServerTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URL;
import java.util.UUID;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientCommonPacketListenerImpl {

    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    @Nullable
    private static URL parseResourcePackUrl(String string) {
        return null;
    }

    /**
     * Removes the resource pack prompt screen in replays
     */
    @Inject(method = "handleResourcePack", at = @At("HEAD"), cancellable = true)
    public void handleResourcePack(ClientboundResourcePackPacket clientboundResourcePackPacket, CallbackInfo ci) {
        if (Flashback.isInReplay()) {
            PacketUtils.ensureRunningOnSameThread(clientboundResourcePackPacket, (ClientPacketListener)(Object)this, this.minecraft);

            URL uRL = parseResourcePackUrl(clientboundResourcePackPacket.getUrl());
            if (uRL != null) {
                String string = clientboundResourcePackPacket.getHash();
                this.minecraft.getDownloadedPackSource().downloadAndSelectResourcePack(uRL, string, true);
            }

            ci.cancel();
        }
    }

    @Inject(method = "handleCustomPayload(Lnet/minecraft/network/protocol/game/ClientboundCustomPayloadPacket;)V", at = @At("HEAD"), cancellable = true)
    public void handleCustomPayload(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket, CallbackInfo ci) {
        if (clientboundCustomPayloadPacket.getIdentifier() == FinishedServerTick.TYPE.getId()) {
            if (Flashback.EXPORT_JOB != null) {
                Flashback.EXPORT_JOB.onFinishedServerTick();
            }
            ci.cancel();
        }
    }

}
