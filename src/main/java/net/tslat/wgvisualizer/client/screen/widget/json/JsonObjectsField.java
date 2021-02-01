package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.tslat.wgvisualizer.Operations;
import net.tslat.wgvisualizer.client.RenderUtils;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JsonObjectsField extends JsonFieldsHolder<JsonObject> {
	private static final ResourceLocation ARROWS_TEXTURE = new ResourceLocation("textures/gui/server_selection.png");
	protected Supplier<JsonObject> rootSaveFunction;
	private final ArrayList<String> breadcrumb;

	public JsonObjectsField(int x, int y, String fieldId, JsonFieldsHolder<?> parent, JsonObject defaultValues, JsonObject currentValues, ITextComponent title, Consumer<JsonFieldsHolder<?>> swapConsumer) {
		super(x, y, parent, fieldId, title, swapConsumer);

		int yOffset = y + 5;

		for (Map.Entry<String, JsonElement> value : currentValues.entrySet()) {
			JsonElement currentElement = value.getValue();
			JsonElement defaultElement = defaultValues.get(value.getKey());

			subWidgets.add(JsonFieldOperations.jsonToWidget(x + width - 105, yOffset, value.getKey(), defaultElement, currentElement, this));

			yOffset += 20;
		}

		breadcrumb = JsonFieldOperations.createBreadcrumb(Minecraft.getInstance().fontRenderer, getFieldPath());
	}

	public void tickWidget() {
		for (JsonValueWidget<?> jsonWidget : subWidgets) {
			jsonWidget.tickWidget();
		}
	}

	public JsonObjectsField setSaveFunction(Supplier<JsonObject> rootSaveFunction) {
		this.rootSaveFunction = rootSaveFunction;

		return this;
	}

	@Override
	public void updateValue(JsonObject values) {
		subWidgets = new ArrayList<JsonValueWidget<?>>();
		int yOffset = 5;

		for (Map.Entry<String, JsonElement> value : values.entrySet()) {
			subWidgets.add(JsonFieldOperations.jsonToWidget(x + width - 105, yOffset, value.getKey(), value.getValue(), value.getValue(), this));

			yOffset += 20;
		}
	}

	@Override
	public JsonObject getJsonValue() {
		JsonObject object = new JsonObject();

		for (JsonValueWidget<?> widget : subWidgets) {
			object.add(widget.getFieldId(), widget.getJsonValue());
		}

		return object;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
			int breadcrumbYOffset = -10 * breadcrumb.size() + 10;

			for (String line : breadcrumb) {
				fontRenderer.drawStringWithShadow(matrixStack, line, x + 5, y - 30 + breadcrumbYOffset, 0xDDDDDD);

				breadcrumbYOffset += 10;
			}

			fillGradient(matrixStack, x, y, x + width, y + height, -1072689136, -804253680);

			for (int i = 0; i < subWidgets.size(); i++) {
				JsonValueWidget<?> jsonWidget = subWidgets.get(i);
				Widget widget = (Widget)jsonWidget;
				String id = jsonWidget.getFieldId();
				int yPos = y + 5 + i * 20 - (int)scrollAmount * 20;
				widget.y = yPos;

				if (widget.y + 5 < y + height && widget.y > y) {
					widget.visible = true;

					widget.render(matrixStack, mouseX, mouseY, partialTicks);
					fontRenderer.drawStringWithShadow(matrixStack, Operations.toTitleCase(id), x + 5, yPos, 0xDDDDDD);
				}
				else {
					widget.visible = false;
				}
			}

			boolean bound = false;
			RenderSystem.enableAlphaTest();

			if ((int)scrollAmount > 0) {
				bound = true;

				Minecraft.getInstance().textureManager.bindTexture(ARROWS_TEXTURE);
				RenderUtils.renderCustomSizedTexture(matrixStack, x + width / 2 - 3, y - 3, 99, 5, 11, 7, 256, 256);
			}

			if ((int)scrollAmount < subWidgets.size() - 7) {
				if (!bound)
					Minecraft.getInstance().textureManager.bindTexture(ARROWS_TEXTURE);

				RenderUtils.renderCustomSizedTexture(matrixStack, x + width / 2 - 3, y + height - 4, 67, 20, 11, 7, 256, 256);
			}
		}
	}
}
