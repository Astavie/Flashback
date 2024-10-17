package com.moulberry.flashback.mixin.visuals;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.visuals.WorldRenderHook;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Inject(method="renderLevel", at=@At(
        value = "INVOKE",
        target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
        ordinal = 3,
        shift = At.Shift.BEFORE
    ))
    public void renderLevelPost(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {

        if (!Flashback.isInReplay()) {
            return;
        }

        this.renderBuffers.bufferSource().endBatch();

        // Set model view stack to identity
        var modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.setIdentity();
        RenderSystem.applyModelViewMatrix();

        WorldRenderHook.renderHook(poseStack, f, bl, camera, gameRenderer, lightTexture, matrix4f);

        this.renderBuffers.bufferSource().endBatch();

        // Pop model view stack
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

}
