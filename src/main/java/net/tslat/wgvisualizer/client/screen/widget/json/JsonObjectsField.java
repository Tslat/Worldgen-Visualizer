package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.text.ITextComponent;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

import java.util.ArrayList;
import java.util.Map;

public class JsonObjectsField extends JsonFieldsHolder<JsonObject> {
	private ArrayList<JsonValueWidget<?>> subWidgets = new ArrayList<JsonValueWidget<?>>();

	private int scrollAmount = 0;

	public JsonObjectsField(String fieldId, JsonFieldsHolder<?> parent, JsonObject currentValues, JsonObject defaultValues, ITextComponent title) {
		super(0, 26, parent, fieldId, title);

		int yOffset = 5;

		for (Map.Entry<String, JsonElement> value : currentValues.entrySet()) {
			JsonElement element = defaultValues.get(value.getKey());

			if (element == null)
				element = value.getValue();

			subWidgets.add(JsonFieldOperations.jsonToWidget(128, yOffset, value.getKey(), element, element));

			yOffset += 20;
		}
	}

	@Override
	public int getFGColor() {
		return isEdited() ? 0xFF6060 : super.getFGColor();
	}

	@Override
	public void updateValue(JsonObject values) {
		subWidgets = new ArrayList<JsonValueWidget<?>>();
		int yOffset = 5;

		for (Map.Entry<String, JsonElement> value : values.entrySet()) {
			subWidgets.add(JsonFieldOperations.jsonToWidget(128, yOffset, value.getKey(), value.getValue(), value.getValue()));

			yOffset += 20;
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		super.onClick(mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		return false;
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
	public boolean isEdited() {
		for (JsonValueWidget<?> widget : subWidgets) {
			if (widget.isEdited())
				return true;
		}

		return false;
	}
}
