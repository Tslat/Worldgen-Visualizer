package net.tslat.wgvisualizer.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class BackButton extends ExtendedButton {
	private static final ResourceLocation ARROWS_TEXTURE = new ResourceLocation("textures/gui/server_selection.png");

	public BackButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction) {
		super(x, y, width, height, title, pressedAction);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			isHovered = mouseX >= x + 1 && mouseY >= y && mouseX < (x + (width - 6) / 2) && mouseY < (y + height / 2);

			if (wasHovered != isHovered()) {
				if (isHovered()) {
					if (focused) {
						queueNarration(200);
					}
					else {
						queueNarration(750);
					}
				}
				else {
					nextNarration = Long.MAX_VALUE;
				}
			}

			if (visible)
				renderButton(matrixStack, mouseX, mouseY, partialTicks);

			narrate();
			wasHovered = isHovered();
		}
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY) {
		return active && visible && isHovered;
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();

		mc.getTextureManager().bindTexture(ARROWS_TEXTURE);
		RenderSystem.color4f(1, 1.0F, 1, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		matrixStack.push();
		matrixStack.scale(0.5f, 0.5f, 0.5f);
		blit(matrixStack, x * 2, y * 2, 32, getVPos(isHovered()), width, height);
		matrixStack.pop();
		renderBg(matrixStack, mc, mouseX, mouseY);

		if (isHovered())
			renderToolTip(matrixStack, mouseX, mouseY);
	}

	private static int getVPos(boolean isHovered) {
		return isHovered ? 37 : 5;
	}
}
