package net.tslat.wgvisualizer.client.screen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.tslat.wgvisualizer.WorldGenVisualizer;
import net.tslat.wgvisualizer.client.RenderUtils;
import net.tslat.wgvisualizer.client.screen.widget.BackButton;
import net.tslat.wgvisualizer.client.screen.widget.json.JsonObjectsField;

public class StructuresSettingsScreen extends Screen {
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/demo_background.png");
	protected static final TranslationTextComponent TITLE = new TranslationTextComponent("screen." + WorldGenVisualizer.MOD_ID + ".structures");
	private static final int backgroundWidth = 248;
	private static final int backgroundHeight = 166;

	private int guiRootX;
	private int guiRootY;

	private static JsonObject currentSettings = null;
	protected static JsonObject editedSettings = null;
	private static JsonObjectsField rootWidget = null;

	protected StructuresSettingsScreen() {
		this(getCurrentStructuresJson());
	}

	protected StructuresSettingsScreen(JsonObject featuresObject) {
		super(TITLE);

		if (currentSettings == null) {
			currentSettings = featuresObject;
			editedSettings = featuresObject;
		}
	}

	protected static boolean isModified() {
		return currentSettings != null && !currentSettings.equals(editedSettings);
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
				button -> {
					if (rootWidget.visible) {
						mc.displayGuiScreen(new WorldgenSettingsScreen(isModified()));
					}
					else {
						rootWidget.upOneLevel();
					}
				}));

		addButton(rootWidget = new JsonObjectsField(guiRootX, guiRootY + 20, null, null, currentSettings, editedSettings, new StringTextComponent(""), this::addButton).setSaveFunction(StructuresSettingsScreen::saveChanges));
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

	public static JsonObject saveChanges() {
		JsonObject jsonObject;

		if (rootWidget != null) {
			jsonObject = rootWidget.getJsonValue();
		}
		else {
			jsonObject = new JsonObject();
		}

		editedSettings = jsonObject;

		return editedSettings;
	}

	protected static void updateSettings(JsonObject data) {
		currentSettings = data;
		editedSettings = data;

		if (Minecraft.getInstance().currentScreen instanceof StructuresSettingsScreen)
			((StructuresSettingsScreen)Minecraft.getInstance().currentScreen).init();
	}

	private static JsonObject getCurrentStructuresJson() {
		if (Minecraft.getInstance().world == null)
			return new JsonObject();

		ClientWorld world = Minecraft.getInstance().world;
		Biome biome = world.getBiome(Minecraft.getInstance().player.getPosition());
		JsonObject biomeJson = Biome.CODEC.encodeStart(JsonOps.INSTANCE, biome).result().get().getAsJsonObject();
		JsonObject container = new JsonObject();

		if (biomeJson.has("starts")) {
			container.add("starts", biomeJson.get("starts"));
		}
		else {
			container.add("starts", new JsonArray());
		}

		return container;
	}
}
