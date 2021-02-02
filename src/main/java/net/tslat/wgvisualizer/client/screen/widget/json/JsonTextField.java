package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

public class JsonTextField extends TextFieldWidget implements JsonValueWidget<JsonPrimitive> {
	private String defaultValue;
	private final String fieldId;
	private final String fieldPath;
	private final JsonFieldsHolder<?> parent;

	public JsonTextField(FontRenderer fontRenderer, int x, int y, String fieldId, JsonFieldsHolder<?> parent, String defaultValue, String currentValue, ITextComponent title) {
		super(fontRenderer, x, y, JSON_WIDGET_WIDTH, JSON_WIDGET_HEIGHT, title);

		this.defaultValue = defaultValue;
		this.fieldId = fieldId;
		this.fieldPath = parent.getFieldPath() + "." + fieldId;
		this.parent = parent;

		setMaxStringLength(100);
		setText(currentValue);
		setTextColour(getText());
		setResponder(this::setTextColour);
		setCursorPosition(0);
	}

	@Override
	public void tickWidget() {
		super.tick();
	}

	@Override
	public String getFieldPath() {
		return this.fieldPath;
	}

	private void setTextColour(String text) {
		if (!getText().equals(defaultValue)) {
			setTextColor(0xFF6060);
			setDisabledTextColour(0xFF6060);
		}
		else {
			setTextColor(14737632);
			setDisabledTextColour(7368816);
		}
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

	@Override
	public void setFocused2(boolean focused) {
		if (this.focused && !focused) {
			parent.updateChanges();

			if (getText().isEmpty())
				setText(defaultValue);
		}

		super.setFocused2(focused);
	}
}
