package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

public class JsonFieldsHolderButton<T extends JsonElement> extends ExtendedButton implements JsonValueWidget<T> {
	private final JsonFieldsHolder<T> subData;
	private final String fieldId;

	public JsonFieldsHolderButton(int x, int y, String fieldId, JsonFieldsHolder<T> data, ITextComponent title, Button.IPressable handler) {
		super(x, y, JSON_WIDGET_WIDTH, JSON_WIDGET_HEIGHT, title, handler);

		this.fieldId = fieldId;
		this.subData = data;
	}

	@Override
	public int getFGColor() {
		return isEdited() ? 0xFF6060 : super.getFGColor();
	}

	@Override
	public void updateValue(T value) {
		subData.updateValue(value);
	}

	@Override
	public T getJsonValue() {
		return subData.getJsonValue();
	}

	@Override
	public boolean isEdited() {
		return subData.isEdited();
	}

	@Override
	public String getFieldId() {
		return this.fieldId;
	}
}
