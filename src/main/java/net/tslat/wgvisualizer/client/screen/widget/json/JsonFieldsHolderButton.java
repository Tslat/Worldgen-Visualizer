package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

public class JsonFieldsHolderButton<T extends JsonElement> extends ExtendedButton implements JsonValueWidget<T> {
	protected final JsonFieldsHolder<T> subData;
	private final String fieldId;

	public JsonFieldsHolderButton(int x, int y, String fieldId, JsonFieldsHolder<T> data, ITextComponent title) {
		super(x, y, JSON_WIDGET_WIDTH, JSON_WIDGET_HEIGHT, title, button -> {
			data.visible = true;
			JsonFieldsHolder.breadcrumb += fieldId + " > ";

			if (data.parent != null)
				data.parent.visible = false;
		});

		this.fieldId = fieldId;
		this.subData = data;

		if (subData.isEmpty()) {
			active = false;
		}
		else {
			active = false;

			for (JsonValueWidget<?> jsonWidget : subData.subWidgets) {
				if (((Widget)jsonWidget).active) {
					active = true;

					break;
				}
			}
		}
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
