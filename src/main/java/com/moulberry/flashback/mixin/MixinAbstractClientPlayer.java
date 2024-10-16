package com.moulberry.flashback.mixin;

import com.mojang.authlib.GameProfile;
import com.moulberry.flashback.state.EditorState;
import com.moulberry.flashback.state.EditorStateManager;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends Player {

    public MixinAbstractClientPlayer(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Unique
    private PlayerInfo skinOverridePlayerInfo = null;

    @Inject(method = "getSkinTextureLocation", at = @At("HEAD"), cancellable = true, require = 0)
    public void getSkinTextureLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        EditorState editorState = EditorStateManager.getCurrent();
        if (editorState != null) {
            GameProfile skinOverride = editorState.skinOverride.get(this.uuid);
            if (skinOverride != null) {
                if (skinOverridePlayerInfo == null || skinOverridePlayerInfo.getProfile() != skinOverride) {
                    skinOverridePlayerInfo = new PlayerInfo(skinOverride, false);
                }
                cir.setReturnValue(skinOverridePlayerInfo.getSkinLocation());
            }
        }
    }

}
