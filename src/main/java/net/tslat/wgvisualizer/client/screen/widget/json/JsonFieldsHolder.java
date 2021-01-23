package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class JsonFieldsHolder<T extends JsonElement> extends Widget implements JsonValueWidget<T> {
	@Nullable
	public final JsonFieldsHolder<?> parent;
	protected final ArrayList<JsonValueWidget<?>> subFields = new ArrayList<JsonValueWidget<?>>();
	private final String fieldId;

	public JsonFieldsHolder(int x, int y, JsonFieldsHolder<?> parent, String fieldId, ITextComponent title) {
		super(x, y, 248, 135, title);

		this.fieldId = fieldId;
		this.parent = parent;
	}

	public final JsonFieldsHolder<T> withFields(JsonValueWidget<?>... fields) {
		this.subFields.addAll(Arrays.asList(fields));

		return this;
	}

	@Override
	public String getFieldId() {
		return fieldId;
	}
}
