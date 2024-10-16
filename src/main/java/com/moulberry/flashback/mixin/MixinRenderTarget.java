package com.moulberry.flashback.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.moulberry.flashback.WindowSizeTracker;
import com.moulberry.flashback.editor.ui.ReplayUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = RenderTarget.class, priority = 800)
public abstract class MixinRenderTarget {

    @Shadow
    public int colorTextureId;

    @Inject(method = "blitToScreen(IIZ)V", at = @At("HEAD"), cancellable = true)
    public void blitToScreenSodium(int width, int height, boolean noBlend, CallbackInfo ci) {
        if ((Object)this == Minecraft.getInstance().getMainRenderTarget() && ReplayUI.isActive()) {
            var window = Minecraft.getInstance().getWindow();
            float frameLeft = (float) ReplayUI.frameX / ReplayUI.viewportSizeX;
            float frameTop = (float) ReplayUI.frameY / ReplayUI.viewportSizeY;
            float frameWidth = (float) Math.max(1, ReplayUI.frameWidth) / ReplayUI.viewportSizeX;
            float frameHeight = (float) Math.max(1, ReplayUI.frameHeight) / ReplayUI.viewportSizeY;

            int realWidth = WindowSizeTracker.getWidth(window);
            int realHeight = WindowSizeTracker.getHeight(window);

            RenderSystem.assertOnRenderThread();
            GlStateManager._colorMask(true, true, true, false);
            GlStateManager._disableDepthTest();
            GlStateManager._depthMask(false);
            GlStateManager._viewport((int)(realWidth * frameLeft), (int)(realHeight * (1 - (frameTop+frameHeight))), Math.max(1, (int)(realWidth * frameWidth)), Math.max(1, (int)(realHeight * frameHeight)));
            if (noBlend) {
                GlStateManager._disableBlend();
            }
            Minecraft minecraft = Minecraft.getInstance();
            ShaderInstance shaderInstance = Objects.requireNonNull(minecraft.gameRenderer.blitShader, "Blit shader not loaded");
            shaderInstance.setSampler("DiffuseSampler", this.colorTextureId);
            Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, (float)Math.max(1, (int)(realWidth * frameWidth)), (float)Math.max(1, (int)(realHeight * frameHeight)), 0.0F, 1000.0F, 3000.0F);
            RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
            if (shaderInstance.MODEL_VIEW_MATRIX != null) {
                shaderInstance.MODEL_VIEW_MATRIX.set((new Matrix4f()).translation(0.0F, 0.0F, -2000.0F));
            }

            if (shaderInstance.PROJECTION_MATRIX != null) {
                shaderInstance.PROJECTION_MATRIX.set(matrix4f);
            }
            shaderInstance.apply();
            float f = (float) Math.max(1, (int)(realWidth * frameWidth));
            float g = (float) Math.max(1, (int)(realHeight * frameHeight));
            float h = (float)1;
            float k = (float)1;
            BufferBuilder bufferBuilder = RenderSystem.renderThreadTesselator().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex(0.0, (double)g, 0.0).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
            bufferBuilder.vertex((double)f, (double)g, 0.0).uv(h, 0.0F).color(255, 255, 255, 255).endVertex();
            bufferBuilder.vertex((double)f, 0.0, 0.0).uv(h, k).color(255, 255, 255, 255).endVertex();
            bufferBuilder.vertex(0.0, 0.0, 0.0).uv(0.0F, k).color(255, 255, 255, 255).endVertex();
            BufferUploader.draw(bufferBuilder.end());
            shaderInstance.clear();
            GlStateManager._depthMask(true);
            GlStateManager._colorMask(true, true, true, true);
            ci.cancel();
        }
    }

}
