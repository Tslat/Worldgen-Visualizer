package net.tslat.wgvisualizer.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.tslat.wgvisualizer.WorldGenVisualizer;

public class WorldgenSettingsScreen extends Screen {
	protected WorldgenSettingsScreen() {
		super(new TranslationTextComponent("screen." + WorldGenVisualizer.MOD_ID + ".settings.title"));
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);


	}
}
