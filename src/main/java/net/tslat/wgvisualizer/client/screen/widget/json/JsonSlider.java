package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonPrimitive;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

public class JsonSlider extends Slider implements JsonValueWidget<JsonPrimitive> {
	private double defaultValue;
	private final String fieldId;
	private final String fieldPath;
	private final JsonFieldsHolder<?> parent;

	public JsonSlider(int xPos, int yPos, String fieldId, JsonFieldsHolder<?> parent, ITextComponent prefix, ITextComponent suf, double minVal, double maxVal, double currentVal, double defaultValue, boolean showDec, IPressable handler) {
		super(xPos, yPos, JSON_WIDGET_WIDTH, JSON_WIDGET_HEIGHT, prefix, suf, minVal, maxVal, currentVal, showDec, true, handler);

		this.defaultValue = defaultValue;
		this.fieldId = fieldId;
		this.parent = parent;
		this.fieldPath = parent.getFieldPath() + "." + fieldId;
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
		this.defaultValue = value.getAsDouble();
		this.setValue(value.getAsDouble());
	}

	@Override
	public JsonPrimitive getJsonValue() {
		return new JsonPrimitive(getValue());
	}

	@Override
	public boolean isEdited() {
		return this.defaultValue != getValue();
	}
}
