package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class JsonNumberField extends TextFieldWidget implements JsonValueWidget<JsonPrimitive> {
	private Number defaultValue;
	private final String fieldId;
	private final JsonFieldsHolder<?> parent;

	public JsonNumberField(FontRenderer fontRenderer, int x, int y, String fieldId, JsonFieldsHolder<?> parent, Number defaultValue, Number currentValue, ITextComponent title) {
		super(fontRenderer, x, y, JSON_WIDGET_WIDTH, JSON_WIDGET_HEIGHT, title);

		this.defaultValue = defaultValue;
		this.fieldId = fieldId;
		this.parent = parent;

		setText(currentValue.toString());
		setValidator(getInputPredicate(defaultValue));
		setTextColour(getText());
		setResponder(this::setTextColour);
		setCursorPosition(0);
	}

	private void setTextColour(String text) {
		if (!getText().equals(defaultValue.toString())) {
			setTextColor(0xFF6060);
			setDisabledTextColour(0xFF6060);
		}
		else {
			setTextColor(14737632);
			setDisabledTextColour(7368816);
		}
	}

	@Override
	public String getFieldId() {
		return fieldId;
	}

	@Override
	public void updateValue(JsonPrimitive value) {
		this.defaultValue = value.getAsNumber();

		setText(value.getAsNumber().toString());
	}

	@Override
	public JsonPrimitive getJsonValue() {
		return new JsonPrimitive(textToNumber());
	}

	@Override
	public boolean isEdited() {
		return !defaultValue.toString().equals(getText());
	}

	@Override
	public void setFocused2(boolean focused) {
		if (this.focused && !focused)
			parent.updateChanges();

		super.setFocused2(focused);
	}

	private Number textToNumber() {
		if (defaultValue instanceof Double) {
			return Double.parseDouble(getText());
		}
		else if (defaultValue instanceof Integer) {
			return Integer.parseInt(getText());
		}
		else if (defaultValue instanceof Float) {
			return Float.parseFloat(getText());
		}
		else if (defaultValue instanceof Byte) {
			return Byte.parseByte(getText());
		}

		return 0;
	}

	private Predicate<String> getInputPredicate(Number defaultValue) {
		if (defaultValue instanceof Integer || defaultValue instanceof Long || defaultValue instanceof Byte) {
			return string -> string.isEmpty() || Pattern.compile("^(0|(-?[1-9]+[0-9]*))$").matcher(string).find();
		}
		else {
			return string -> string.isEmpty() || Pattern.compile("^(-?[0-9]+(\\.?[0-9]*)?)$").matcher(string).find();
		}
	}
}
