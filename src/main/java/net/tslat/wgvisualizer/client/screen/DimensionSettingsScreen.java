package net.tslat.wgvisualizer.client.screen;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.tslat.wgvisualizer.WorldGenVisualizer;
import net.tslat.wgvisualizer.client.RenderUtils;
import net.tslat.wgvisualizer.client.screen.widget.BackButton;

public class DimensionSettingsScreen extends Screen {
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/demo_background.png");
	protected static final TranslationTextComponent TITLE = new TranslationTextComponent("screen." + WorldGenVisualizer.MOD_ID + ".dimension");
	private static final int backgroundWidth = 248;
	private static final int backgroundHeight = 166;

	private int guiRootX;
	private int guiRootY;

	private static JsonObject currentSettings = new JsonObject();
	protected static JsonObject editedSettings = new JsonObject();

	protected DimensionSettingsScreen() {
		super(TITLE);
	}

	protected static boolean isModified() {
		return !currentSettings.equals(editedSettings);
	}

	@Override
	protected void init() {
		super.init();

		Minecraft mc = Minecraft.getInstance();
		guiRootX = (width - backgroundWidth) / 2;
		guiRootY = (height - backgroundHeight) / 2;

		addButton(new BackButton(
				guiRootX + 5,
				guiRootY + 5,
				22,
				22,
				new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".back"),
				button -> mc.displayGuiScreen(new WorldgenSettingsScreen(isModified()))));
	}

	@Override
	public void renderBackground(MatrixStack matrixStack, int vOffset) {
		super.renderBackground(matrixStack, vOffset);

		matrixStack.push();
		matrixStack.translate(guiRootX, guiRootY, 0);

		Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		RenderUtils.renderTexture(matrixStack, 0, 0, 0, 0, 256, 256);
		font.func_243248_b(matrixStack, TITLE, backgroundWidth / 2 - font.getStringPropertyWidth(TITLE) / 2, 6, 4210752);

		matrixStack.pop();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	protected static void updateSettings(JsonObject data) {

	}
}
