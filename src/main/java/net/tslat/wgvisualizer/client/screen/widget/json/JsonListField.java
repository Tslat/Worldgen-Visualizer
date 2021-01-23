package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.util.text.ITextComponent;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

import java.util.ArrayList;

public class JsonListField extends JsonFieldsHolder<JsonArray> {
	private ArrayList<JsonValueWidget<?>> subWidgets = new ArrayList<JsonValueWidget<?>>();

	public JsonListField(String fieldId, JsonFieldsHolder<?> parent, JsonArray currentValues, JsonArray defaultValues, ITextComponent title) {
		super(0, 26, parent, fieldId, title);

		for (int i = 0; i < currentValues.size(); i++) {
			JsonElement currentElement = currentValues.get(i);
			JsonElement defaultElement = defaultValues.size() > i ? defaultValues.get(i) : null;

			subWidgets.add(JsonFieldOperations.jsonToWidget(128, 5 + 20 * i, String.valueOf(i), currentElement, defaultElement));
		}
	}

	@Override
	public int getFGColor() {
		return isEdited() ? 0xFF6060 : super.getFGColor();
	}

	@Override
	public void updateValue(JsonArray value) {
		subWidgets = new ArrayList<JsonValueWidget<?>>();

		for (int i = 0; i < value.size(); i++) {
			JsonElement element = value.get(i);

			subWidgets.add(JsonFieldOperations.jsonToWidget(128, 5 + 20 * i, String.valueOf(i), element, element));
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		super.onClick(mouseX, mouseY);
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
	public boolean isEdited() {
		for (JsonValueWidget<?> widget : subWidgets) {
			if (widget.isEdited())
				return true;
		}

		return false;
	}
}
