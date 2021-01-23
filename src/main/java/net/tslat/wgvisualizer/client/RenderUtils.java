package net.tslat.wgvisualizer.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

public class RenderUtils {
	public static void renderTexture(MatrixStack matrix, int x, int y, float u, float v, float width, float height) {
		renderCustomSizedTexture(matrix, x, y, u, v, (int)width, (int)height, width, height);
	}

	public static void renderCustomSizedTexture(MatrixStack matrix, int x, int y, float u, float v, int uWidth, int vHeight, float textureWidth, float textureHeight) {
		renderScaledCustomSizedTexture(matrix, x, y, u, v, uWidth, vHeight, uWidth, vHeight, textureWidth, textureHeight);
	}

	public static void renderScaledCustomSizedTexture(MatrixStack matrixStack, float x, float y, float u, float v, float uWidth, float vHeight, float renderWidth, float renderHeight, float textureWidth, float textureHeight) {
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrixStack.getLast().getMatrix();
		float widthRatio = 1.0F / textureWidth;
		float heightRatio = 1.0F / textureHeight;

		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(matrix, x, y + renderHeight, 0f).tex(u * widthRatio, (v + vHeight) * heightRatio).endVertex();
		buffer.pos(matrix, x + renderWidth, y + renderHeight, 0f).tex((u + uWidth) * widthRatio, (v + vHeight) * heightRatio).endVertex();
		buffer.pos(matrix, x + renderWidth, y, 0f).tex((u + uWidth) * widthRatio, v * heightRatio).endVertex();
		buffer.pos(matrix, x, y, 0f).tex(u * widthRatio, v * heightRatio).endVertex();
		buffer.finishDrawing();
		WorldVertexBufferUploader.draw(buffer);
	}
}
