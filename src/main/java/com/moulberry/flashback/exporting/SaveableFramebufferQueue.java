package com.moulberry.flashback.exporting;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.moulberry.flashback.visuals.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL32C;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SaveableFramebufferQueue implements AutoCloseable {

    private final int width;
    private final int height;

    private static final int CAPACITY = 3;

    private final List<SaveableFramebuffer> available = new ArrayList<>();
    private final List<SaveableFramebuffer> waiting = new ArrayList<>();

    private final RenderTarget flipBuffer;

    public SaveableFramebufferQueue(int width, int height) {
        this.width = width;
        this.height = height;
        this.flipBuffer = new TextureTarget(width, height, false, false);

        for (int i = 0; i < CAPACITY; i++) {
            this.available.add(new SaveableFramebuffer());
        }
    }

    public SaveableFramebuffer take() {
        if (this.available.isEmpty()) {
            throw new IllegalStateException("No textures available!");
        }
        return this.available.remove(0);
    }

    private void blitFlip(RenderTarget src, boolean supersampling) {
        int oldFilterMode = src.filterMode;
        if (supersampling) {
            src.setFilterMode(GL11.GL_LINEAR);
        }

        GlStateManager._colorMask(true, true, true, true);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._viewport(0, 0, src.width, src.height);
        GlStateManager._disableBlend();
        RenderSystem.disableCull();

        this.flipBuffer.bindWrite(true);
        ShaderInstance flipShader = Objects.requireNonNull(Minecraft.getInstance().gameRenderer.blitShader, "Blit shader not loaded");
        flipShader.setSampler("DiffuseSampler", src.colorTextureId);
        flipShader.apply();
        Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, src.width, 0.0F, src.height, 1000.0F, 3000.0F); // we flip bottom and top
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
        if (flipShader.MODEL_VIEW_MATRIX != null) {
            flipShader.MODEL_VIEW_MATRIX.set((new Matrix4f()).translation(0.0F, 0.0F, -2000.0F));
        }

        if (flipShader.PROJECTION_MATRIX != null) {
            flipShader.PROJECTION_MATRIX.set(matrix4f);
        }
        flipShader.apply();
        float f = (float) src.width;
        float g = (float) src.height;
        float h = (float)1;
        float k = (float)1;
        BufferBuilder bufferBuilder = RenderSystem.renderThreadTesselator().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.vertex(0.0, (double)g, 0.0).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex((double)f, (double)g, 0.0).uv(h, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex((double)f, 0.0, 0.0).uv(h, k).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(0.0, 0.0, 0.0).uv(0.0F, k).color(255, 255, 255, 255).endVertex();
        BufferUploader.draw(bufferBuilder.end());
        flipShader.clear();

        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
        RenderSystem.enableCull();

        if (supersampling) {
            src.setFilterMode(oldFilterMode);
        }
    }

    public void startDownload(RenderTarget target, SaveableFramebuffer texture, boolean supersampling) {
        // Do an inline flip
        this.blitFlip(target, supersampling);

        texture.startDownload(this.flipBuffer, this.width, this.height);
        this.waiting.add(texture);
    }

    record DownloadedFrame(NativeImage image, @Nullable FloatBuffer audioBuffer) {}

    public @Nullable DownloadedFrame finishDownload(boolean drain) {
        if (this.waiting.isEmpty()) {
            return null;
        }

        if (!drain && !this.available.isEmpty()) {
            return null;
        }

        SaveableFramebuffer texture = this.waiting.remove(0);

        NativeImage nativeImage = texture.finishDownload(this.width, this.height);
        FloatBuffer audioBuffer = texture.audioBuffer;
        texture.audioBuffer = null;

        this.available.add(texture);
        return new DownloadedFrame(nativeImage, audioBuffer);
    }

    @Override
    public void close() {
        for (SaveableFramebuffer texture : this.waiting) {
            texture.close();
        }
        for (SaveableFramebuffer texture : this.available) {
            texture.close();
        }
        this.waiting.clear();
        this.available.clear();
        this.flipBuffer.destroyBuffers();
    }


}
