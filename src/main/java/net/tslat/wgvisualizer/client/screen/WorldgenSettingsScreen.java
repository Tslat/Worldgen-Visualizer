package net.tslat.wgvisualizer.client.screen;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.tslat.wgvisualizer.Operations;
import net.tslat.wgvisualizer.WorldGenVisualizer;
import net.tslat.wgvisualizer.client.RenderUtils;
import net.tslat.wgvisualizer.client.screen.widget.StateTrackingButton;
import net.tslat.wgvisualizer.common.network.PacketHandling;
import net.tslat.wgvisualizer.common.network.WorldgenHandshakePacket;
import net.tslat.wgvisualizer.common.network.WorldgenUpdatePacket;

import javax.annotation.Nullable;

public class WorldgenSettingsScreen extends Screen {
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/demo_background.png");
	protected static final TranslationTextComponent TITLE = new TranslationTextComponent("screen." + WorldGenVisualizer.MOD_ID + ".settings");
	private static final int backgroundWidth = 248;
	private static final int backgroundHeight = 166;

	private int guiRootX;
	private int guiRootY;

	private TextFieldWidget presetIdField;
	private static boolean hasChangedSettings;
	private Button applyButton;

	public WorldgenSettingsScreen() {
		this(BiomeSettingsScreen.isModified() || DimensionSettingsScreen.isModified() || DimensionTypeSettingsScreen.isModified()
			|| FeaturesSettingsScreen.isModified() || StructuresSettingsScreen.isModified() || SurfaceBuilderSettingsScreen.isModified());
	}

	public WorldgenSettingsScreen(boolean changedSettings) {
		super(TITLE);

		hasChangedSettings |= changedSettings;
	}

	@Override
	protected void init() {
		super.init();

		Minecraft mc = Minecraft.getInstance();
		guiRootX = (width - backgroundWidth) / 2;
		guiRootY = (height - backgroundHeight) / 2;
		TranslationTextComponent text;

		presetIdField = new TextFieldWidget(font, guiRootX + font.getStringPropertyWidth(text = new TranslationTextComponent("field." + WorldGenVisualizer.MOD_ID + ".presetId")) + 17, guiRootY + 25, backgroundWidth - 34 - font.getStringPropertyWidth(text), 15, new StringTextComponent("default"));

		presetIdField.setText("default");
		presetIdField.setMaxStringLength(20);
		presetIdField.setEnableBackgroundDrawing(true);
		presetIdField.setVisible(true);
		presetIdField.setCanLoseFocus(true);
		children.add(presetIdField);
		Button cancelButton;

		addButton(applyButton = new ExtendedButton(
				guiRootX + 30,
				guiRootY + backgroundHeight - 30,
				font.getStringPropertyWidth(text = new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".apply")) + 10,
				20,
				text,
				this::attemptToSaveSettings));
		addButton(cancelButton = new ExtendedButton(
				guiRootX + backgroundWidth - 40 - font.getStringPropertyWidth(text = new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".cancel")) - 10,
				guiRootY + backgroundHeight - 30,
				font.getStringPropertyWidth(text) + 10,
				20,
				text,
				button -> closeScreen()));
		/*addButton(new ExtendedButton(
				((applyButton.x + applyButton.getWidth() + cancelButton.x) / 2) - (font.getStringPropertyWidth(text = new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".save")) + 10) / 2,
				guiRootY + backgroundHeight - 30,
				font.getStringPropertyWidth(text) + 10,
				20,
				text,
				this::saveToFiles));*/
		addButton(new StateTrackingButton(
				guiRootX + 15,
				guiRootY + 50,
				backgroundWidth / 2 - 15,
				20, new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".dimension"),
				button -> mc.displayGuiScreen(new DimensionSettingsScreen()),
				DimensionSettingsScreen::isModified));
		addButton(new StateTrackingButton(
				guiRootX + backgroundWidth / 2,
				guiRootY + 50,
				backgroundWidth / 2 - 15,
				20,
				new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".dimensionType"),
				button -> mc.displayGuiScreen(new DimensionTypeSettingsScreen()),
				DimensionTypeSettingsScreen::isModified));
		addButton(new StateTrackingButton(
				guiRootX + 15,
				guiRootY + 71,
				backgroundWidth / 2 - 15,
				20,
				new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".biome"),
				button -> mc.displayGuiScreen(new BiomeSettingsScreen()),
				BiomeSettingsScreen::isModified));
		addButton(new StateTrackingButton(
				guiRootX + backgroundWidth / 2,
				guiRootY + 71,
				backgroundWidth / 2 - 15,
				20,
				new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".surfaceBuilder"),
				button -> mc.displayGuiScreen(new SurfaceBuilderSettingsScreen()),
				SurfaceBuilderSettingsScreen::isModified));
		addButton(new StateTrackingButton(
				guiRootX + 15,
				guiRootY + 92,
				backgroundWidth / 2 - 15,
				20,
				new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".features"),
				button -> mc.displayGuiScreen(new FeaturesSettingsScreen()),
				FeaturesSettingsScreen::isModified));
		addButton(new StateTrackingButton(
				guiRootX + backgroundWidth / 2,
				guiRootY + 92,
				backgroundWidth / 2 - 15,
				20,
				new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".structures"),
				button -> mc.displayGuiScreen(new StructuresSettingsScreen()),
				StructuresSettingsScreen::isModified));
	}

	@Override
	public void renderBackground(MatrixStack matrixStack, int vOffset) {
		super.renderBackground(matrixStack, vOffset);

		matrixStack.push();
		matrixStack.translate(guiRootX, guiRootY, 0);

		Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		RenderUtils.renderTexture(matrixStack, 0, 0, 0, 0, 256, 256);
		font.func_243248_b(matrixStack, TITLE, backgroundWidth / 2 - font.getStringPropertyWidth(TITLE) / 2, 6, 4210752);
		font.func_243248_b(matrixStack, new TranslationTextComponent("field." + WorldGenVisualizer.MOD_ID + ".presetId"), 15, 29, 4210752);

		matrixStack.pop();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		presetIdField.render(matrixStack, mouseX, mouseY, partialTicks);

		if (applyButton != null)
			applyButton.active = hasChangedSettings;

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	private void saveToFiles(Button saveButton) {

	}

	public static void updateSettings(JsonObject data) {
		if (data.has(Operations.GenCategory.BIOME.toString()))
			BiomeSettingsScreen.updateSettings(data.get(Operations.GenCategory.BIOME.toString()).getAsJsonObject());

		if (data.has(Operations.GenCategory.DIMENSION.toString()))
			DimensionSettingsScreen.updateSettings(data.get(Operations.GenCategory.DIMENSION.toString()).getAsJsonObject());

		if (data.has(Operations.GenCategory.DIMENSION_TYPE.toString()))
			DimensionTypeSettingsScreen.updateSettings(data.get(Operations.GenCategory.DIMENSION_TYPE.toString()).getAsJsonObject());

		if (data.has(Operations.GenCategory.FEATURES.toString()))
			FeaturesSettingsScreen.updateSettings(data.get(Operations.GenCategory.FEATURES.toString()).getAsJsonObject());

		if (data.has(Operations.GenCategory.STRUCTURES.toString()))
			StructuresSettingsScreen.updateSettings(data.get(Operations.GenCategory.STRUCTURES.toString()).getAsJsonObject());

		if (data.has(Operations.GenCategory.SURFACE_BUILDER.toString()))
			SurfaceBuilderSettingsScreen.updateSettings(data.get(Operations.GenCategory.SURFACE_BUILDER.toString()).getAsJsonObject());
	}

	private static void applySettings() {
		JsonObject data = new JsonObject();

		if (BiomeSettingsScreen.isModified())
			data.add(Operations.GenCategory.BIOME.toString(), BiomeSettingsScreen.editedSettings);

		if (DimensionSettingsScreen.isModified())
			data.add(Operations.GenCategory.DIMENSION.toString(), DimensionSettingsScreen.editedSettings);

		if (DimensionTypeSettingsScreen.isModified())
			data.add(Operations.GenCategory.DIMENSION_TYPE.toString(), DimensionTypeSettingsScreen.editedSettings);

		if (FeaturesSettingsScreen.isModified())
			data.add(Operations.GenCategory.FEATURES.toString(), FeaturesSettingsScreen.editedSettings);

		if (StructuresSettingsScreen.isModified())
			data.add(Operations.GenCategory.STRUCTURES.toString(), StructuresSettingsScreen.editedSettings);

		if (SurfaceBuilderSettingsScreen.isModified())
			data.add(Operations.GenCategory.SURFACE_BUILDER.toString(), SurfaceBuilderSettingsScreen.editedSettings);

		PacketHandling.INSTANCE.sendToServer(new WorldgenUpdatePacket(data));
	}

	public void attemptToSaveSettings(Button applyButton) {
		PacketHandling.INSTANCE.sendToServer(new WorldgenHandshakePacket());
		closeScreen();
	}

	public static void receiveHandshakeResponse(@Nullable String collidedPlayer) {
		if (collidedPlayer == null) {
			applySettings();
		}
		else {
			Minecraft mc = Minecraft.getInstance();

			if (mc.player == null)
				return;

			if (mc.currentScreen instanceof WorldgenSettingsScreen)
				mc.displayGuiScreen(null);

			mc.player.sendMessage(new TranslationTextComponent("message." + WorldGenVisualizer.MOD_ID + ".feedback.updateInProgress", collidedPlayer).mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
		}
	}
}
