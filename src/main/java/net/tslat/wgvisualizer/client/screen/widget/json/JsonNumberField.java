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

	public JsonNumberField(FontRenderer fontRenderer, int x, int y, String fieldId, Number defaultValue, Number currentValue, ITextComponent title) {
		super(fontRenderer, x, y, JSON_WIDGET_WIDTH, JSON_WIDGET_HEIGHT, title);

		this.defaultValue = defaultValue;
		this.fieldId = fieldId;

		setText(currentValue.toString());
		setValidator(getInputPredicate(defaultValue));
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
		this.defaultValue = value.getAsNumber();

		setText(value.getAsNumber().toString());
	}

	@Override
	public JsonPrimitive getJsonValue() {
		return new JsonPrimitive(textToNumber());
	}

	@Override
	public boolean isEdited() {
		return !defaultValue.equals(textToNumber());
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
			return string -> Pattern.compile("^(0|(-?[1-9]+[0-9]*))$").matcher(string).find();
		}
		else {
			return string -> Pattern.compile("^(-?[0-9]+(\\.?[0-9]*)?)$").matcher(string).find();
		}
	}
}
