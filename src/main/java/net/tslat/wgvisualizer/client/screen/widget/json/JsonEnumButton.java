package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonPrimitive;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.tslat.wgvisualizer.Operations;
import net.tslat.wgvisualizer.WorldGenVisualizer;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

import java.util.Arrays;
import java.util.TreeMap;

public class JsonEnumButton<T extends Enum<?>> extends ExtendedButton implements JsonValueWidget<JsonPrimitive> {
	private T defaultValue;
	private T currentValue;
	private final TreeMap<String, T> enumMap = new TreeMap<String, T>();
	private final String fieldId;
	private final String fieldPath;
	private final JsonFieldsHolder<?> parent;

	public JsonEnumButton(int x, int y, String fieldId, JsonFieldsHolder<?> parent, T currentValue, T defaultValue, ITextComponent title) {
		super(x, y, JSON_WIDGET_WIDTH, JSON_WIDGET_HEIGHT, title, button -> ((JsonEnumButton<?>)button).cycleValue());

		this.defaultValue = defaultValue;
		this.currentValue = currentValue;
		this.fieldId = fieldId;
		this.fieldPath = parent.getFieldPath() + "." + fieldId;
		this.parent = parent;

		Arrays.stream(currentValue.getDeclaringClass().getEnumConstants()).forEach(value -> enumMap.put(value.toString(), (T)value));
	}

	@Override
	public int getFGColor() {
		return isEdited() ? 0xFF6060 : super.getFGColor();
	}

	@Override
	public String getFieldPath() {
		return this.fieldPath;
	}

	@Override
	public String getFieldId() {
		return fieldId;
	}

	@Override
	public void updateValue(JsonPrimitive value) {
		T enumValue = enumMap.get(value.getAsString());

		this.defaultValue = enumValue;
		this.currentValue = enumValue;
	}

	@Override
	public JsonPrimitive getJsonValue() {
		return new JsonPrimitive(currentValue.toString());
	}

	@Override
	public boolean isEdited() {
		return currentValue != defaultValue;
	}

	private void cycleValue() {
		T value = null;
		boolean grabNext = false;

		for (T val : enumMap.values()) {
			if (grabNext) {
				currentValue = val;

				break;
			}
			else if (val == currentValue) {
				grabNext = true;
			}
			else if (value == null) {
				value = val;
			}
		}

		this.currentValue = value;

		this.setMessage(new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".enum", Operations.toTitleCase(currentValue.toString())));
		parent.updateChanges();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}
}
