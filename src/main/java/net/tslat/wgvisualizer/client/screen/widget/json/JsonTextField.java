package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

public class JsonTextField extends TextFieldWidget implements JsonValueWidget<JsonPrimitive> {
	private String defaultValue;
	private final String fieldId;

	public JsonTextField(FontRenderer fontRenderer, int x, int y, String fieldId, String defaultValue, String currentValue, ITextComponent title) {
		super(fontRenderer, x, y, JSON_WIDGET_WIDTH, JSON_WIDGET_HEIGHT, title);

		this.defaultValue = defaultValue;
		this.fieldId = fieldId;

		setText(currentValue);
	}

	@Override
	public int getFGColor() {
		return isEdited() ? 0xFF6060 : super.getFGColor();
	}

	@Override
	public String getFieldId() {
		return fieldId;
	}

	@Override
	public void updateValue(JsonPrimitive value) {
		this.defaultValue = value.getAsString();

		setText(value.getAsString());
	}

	@Override
	public JsonPrimitive getJsonValue() {
		return new JsonPrimitive(getText());
	}

	@Override
	public boolean isEdited() {
		return !defaultValue.equals(getText());
	}
}
