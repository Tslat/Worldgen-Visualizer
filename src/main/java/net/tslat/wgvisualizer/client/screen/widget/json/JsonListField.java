package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.tslat.wgvisualizer.WorldGenVisualizer;
import net.tslat.wgvisualizer.client.RenderUtils;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

import java.util.ArrayList;
import java.util.function.Consumer;

public class JsonListField extends JsonFieldsHolder<JsonArray> {
	private static final ResourceLocation ARROWS_TEXTURE = new ResourceLocation("textures/gui/server_selection.png");
	private final ArrayList<String> breadcrumb;

	public JsonListField(int x, int y, String fieldId, JsonFieldsHolder<?> parent, JsonArray defaultValues, JsonArray currentValues, ITextComponent title, Consumer<JsonFieldsHolder<?>> swapConsumer) {
		super(x, y, parent, fieldId, title, swapConsumer);

		for (int i = 0; i < currentValues.size(); i++) {
			JsonElement currentElement = currentValues.get(i);
			JsonElement defaultElement = defaultValues.size() > i ? defaultValues.get(i) : null;

			subWidgets.add(JsonFieldOperations.jsonToWidget(x + width - 105, y + 5 + 20 * i, String.valueOf(i), defaultElement, currentElement, this));
		}

		breadcrumb = JsonFieldOperations.createBreadcrumb(Minecraft.getInstance().fontRenderer, getFieldPath());
	}

	@Override
	public void tickWidget() {
		for (JsonValueWidget<?> widget : subWidgets) {
			widget.tickWidget();
		}
	}

	@Override
	public void updateValue(JsonArray value) {
		subWidgets = new ArrayList<JsonValueWidget<?>>();

		for (int i = 0; i < value.size(); i++) {
			JsonElement element = value.get(i);

			subWidgets.add(JsonFieldOperations.jsonToWidget(x + width - 105, y + 5 + 20 * i, String.valueOf(i), element, element, this));
		}
	}

	@Override
	public JsonArray getJsonValue() {
		JsonArray array = new JsonArray();

		for (JsonValueWidget<?> widget : subWidgets) {
			array.add(widget.getJsonValue());
		}

		return array;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
			String openText = I18n.format("button." + WorldGenVisualizer.MOD_ID + ".list.open");
			int breadcrumbYOffset = -10 * breadcrumb.size() + 10;

			for (String line : breadcrumb) {
				fontRenderer.drawStringWithShadow(matrixStack, line, x + 5, y - 30 + breadcrumbYOffset, 0xDDDDDD);

				breadcrumbYOffset += 10;
			}
			fillGradient(matrixStack, x, y, x + width, y + height, -1072689136, -804253680);

			for (int i = 0; i < subWidgets.size(); i++) {
				JsonValueWidget<?> jsonWidget = subWidgets.get(i);
				Widget widget = (Widget)jsonWidget;
				int yPos = y + 5 + i * 20 - (int)scrollAmount * 20;
				widget.y = yPos;

				if (widget.y + 5 < y + height && widget.y > y) {
					widget.visible = true;

					if (widget.getMessage() instanceof StringTextComponent && ((StringTextComponent)widget.getMessage()).getText().equals(String.valueOf(i)))
						widget.setMessage(new StringTextComponent(openText));

					widget.render(matrixStack, mouseX, mouseY, partialTicks);
					fontRenderer.drawStringWithShadow(matrixStack, i + ":", x + 5, yPos, 0xDDDDDD);
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
