package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

import java.math.BigDecimal;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class JsonNumberField extends TextFieldWidget implements JsonValueWidget<JsonPrimitive> {
	private static final Pattern WHOLE_NUMBER_PATTERN = Pattern.compile("^(-?[1-9]+[0-9]*)$");
	private static final Pattern DECIMAL_NUMBER_PATTERN = Pattern.compile("^(-?[0-9]+(\\.?[0-9]*)?)$");
	private static final Pattern PARTIAL_DECIMAL_NUMBER_PATTERN = Pattern.compile("^(-|(-?[0-9]+\\.?0*))$");

	private static final Predicate<String> INTEGER_FORMAT_PREDICATE = integerPredicate(false);
	private static final Predicate<String> LONG_FORMAT_PREDICATE = longPredicate(false);
	private static final Predicate<String> BYTE_FORMAT_PREDICATE = bytePredicate(false);
	private static final Predicate<String> SHORT_FORMAT_PREDICATE = shortPredicate(false);
	private static final Predicate<String> FLOAT_FORMAT_PREDICATE = floatPredicate(false);
	private static final Predicate<String> DOUBLE_FORMAT_PREDICATE = doublePredicate(false);

	private Number defaultValue;
	private final String fieldId;
	private final String fieldPath;
	private final JsonFieldsHolder<?> parent;

	public JsonNumberField(FontRenderer fontRenderer, int x, int y, String fieldId, JsonFieldsHolder<?> parent, Number defaultValue, Number currentValue, ITextComponent title) {
		super(fontRenderer, x, y, JSON_WIDGET_WIDTH, JSON_WIDGET_HEIGHT, title);

		this.defaultValue = convertDefaultForCompatibility(defaultValue);
		this.fieldId = fieldId;
		this.fieldPath = parent.getFieldPath() + "." + fieldId;
		this.parent = parent;

		setText(currentValue.toString());
		setValidator(getInputPredicate(this.defaultValue, false));
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
	public String getFieldPath() {
		return this.fieldPath;
	}

	@Override
	public void tickWidget() {
		super.tick();
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
		if (this.focused && !focused) {
			if (!getInputPredicate(defaultValue, true).test(getText()))
				setText(defaultValue.toString());

			parent.updateChanges();
		}

		super.setFocused2(focused);
	}

	private Number textToNumber() {
		if (getText().equals("-") || getText().isEmpty()) {
			setText("0");

			return 0;
		}

		if (defaultValue instanceof Double) {
			return Double.parseDouble(getText());
		}
		else if (defaultValue instanceof Float) {
			return Float.parseFloat(getText());
		}
		else if (defaultValue instanceof Integer || defaultValue instanceof Short || defaultValue instanceof Byte) {
			return Integer.parseInt(getText());
		}

		return 0;
	}

	private static Predicate<String> integerPredicate(boolean isExitingField) {
		Predicate<String> predicate = isExitingField ? value -> false : value -> value.isEmpty() || value.equals("-") || value.equals("0");

		return predicate.or(value -> {
			if (!WHOLE_NUMBER_PATTERN.matcher(value).find())
				return false;

			BigDecimal numericValue = new BigDecimal(value);

			if (numericValue.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) > 0)
				return false;

			return numericValue.compareTo(BigDecimal.valueOf(Integer.MIN_VALUE)) >= 0;
		});
	}

	private static Predicate<String> longPredicate(boolean isExitingField) {
		Predicate<String> predicate = isExitingField ? value -> false : value -> value.isEmpty() || value.equals("-") || value.equals("0");

		return predicate.or(value -> {
			if (!WHOLE_NUMBER_PATTERN.matcher(value).find())
				return false;

			BigDecimal numericValue = new BigDecimal(value);

			if (numericValue.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) > 0)
				return false;

			return numericValue.compareTo(BigDecimal.valueOf(Long.MIN_VALUE)) >= 0;
		});
	}

	private static Predicate<String> bytePredicate(boolean isExitingField) {
		Predicate<String> predicate = isExitingField ? value -> false : value -> value.isEmpty() || value.equals("-") || value.equals("0");

		return predicate.or(value -> {
			if (!WHOLE_NUMBER_PATTERN.matcher(value).find())
				return false;

			BigDecimal numericValue = new BigDecimal(value);

			if (numericValue.compareTo(BigDecimal.valueOf(Byte.MAX_VALUE)) > 0)
				return false;

			return numericValue.compareTo(BigDecimal.valueOf(Byte.MIN_VALUE)) >= 0;
		});
	}

	private static Predicate<String> shortPredicate(boolean isExitingField) {
		Predicate<String> predicate = isExitingField ? value -> false : value -> value.isEmpty() || value.equals("-") || value.equals("0");

		return predicate.or(value -> {
			if (!WHOLE_NUMBER_PATTERN.matcher(value).find())
				return false;

			BigDecimal numericValue = new BigDecimal(value);

			if (numericValue.compareTo(BigDecimal.valueOf(Short.MAX_VALUE)) > 0)
				return false;

			return numericValue.compareTo(BigDecimal.valueOf(Short.MIN_VALUE)) >= 0;
		});
	}

	private static Predicate<String> floatPredicate(boolean isExitingField) {
		Predicate<String> predicate = isExitingField ? value -> false : value -> value.isEmpty() || PARTIAL_DECIMAL_NUMBER_PATTERN.matcher(value).find();

		return predicate.or(value -> {
			if (!DECIMAL_NUMBER_PATTERN.matcher(value).find())
				return false;

			BigDecimal numericValue = new BigDecimal(value);

			if (numericValue.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) > 0)
				return false;

			return numericValue.compareTo(BigDecimal.valueOf(Float.MIN_VALUE)) >= 0;
		});
	}

	private static Predicate<String> doublePredicate(boolean isExitingField) {
		Predicate<String> predicate = isExitingField ? value -> false : value -> value.isEmpty() || PARTIAL_DECIMAL_NUMBER_PATTERN.matcher(value).find();

		return predicate.or(value -> {
			if (!DECIMAL_NUMBER_PATTERN.matcher(value).find())
				return false;

			BigDecimal numericValue = new BigDecimal(value);

			if (numericValue.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0)
				return false;

			return numericValue.compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) >= 0;
		});
	}

	private Predicate<String> getInputPredicate(Number defaultValue, boolean isExitingField) {
		if (defaultValue instanceof Integer) {
			return isExitingField ? integerPredicate(true) : INTEGER_FORMAT_PREDICATE;
		}
		else if (defaultValue instanceof Long) {
			return isExitingField ? longPredicate(true) : LONG_FORMAT_PREDICATE;
		}
		else if (defaultValue instanceof Byte) {
			return isExitingField ? bytePredicate(true) : BYTE_FORMAT_PREDICATE;
		}
		else if (defaultValue instanceof Short) {
			return isExitingField ? shortPredicate(true) : SHORT_FORMAT_PREDICATE;
		}
		else if (defaultValue instanceof Float) {
			return isExitingField ? floatPredicate(true) : FLOAT_FORMAT_PREDICATE;
		}
		else if (defaultValue instanceof Double) {
			return isExitingField ? doublePredicate(true) : DOUBLE_FORMAT_PREDICATE;
		}

		return value -> value.isEmpty() || value.equals("-") || value.equals("0") || DECIMAL_NUMBER_PATTERN.matcher(value).find();
	}

	private static Number convertDefaultForCompatibility(Number defaultValue) {
		return defaultValue instanceof Double || defaultValue instanceof Float ? defaultValue : defaultValue.longValue();
	}
}
