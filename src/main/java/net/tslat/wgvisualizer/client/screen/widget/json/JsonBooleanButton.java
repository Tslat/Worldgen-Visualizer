package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonPrimitive;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.tslat.wgvisualizer.WorldGenVisualizer;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

public class JsonBooleanButton extends ExtendedButton implements JsonValueWidget<JsonPrimitive> {
	private boolean defaultState;
	private boolean state;

	private final String fieldId;
	private final JsonFieldsHolder<?> parent;

	public JsonBooleanButton(int x, int y, String fieldId, JsonFieldsHolder<?> parent, boolean defaultState, boolean currentState, ITextComponent title) {
		super(x, y, JSON_WIDGET_WIDTH, JSON_WIDGET_HEIGHT, title, button -> ((JsonBooleanButton)button).toggle());

		this.defaultState = defaultState;
		this.state = currentState;
		this.fieldId = fieldId;
		this.parent = parent;
	}

	@Override
	public int getFGColor() {
		return isEdited() ? 0xFF6060 : super.getFGColor();
	}

	@Override
	public void updateValue(JsonPrimitive value) {
		this.defaultState = value.getAsBoolean();
		this.state = value.getAsBoolean();
	}

	@Override
	public JsonPrimitive getJsonValue() {
		return new JsonPrimitive(state);
	}

	@Override
	public boolean isEdited() {
		return state != defaultState;
	}

	@Override
	public String getFieldId() {
		return this.fieldId;
	}

	private void toggle() {
		this.state = !state;

		this.setMessage(new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".boolean." + state));
		parent.updateChanges();
	}
}
