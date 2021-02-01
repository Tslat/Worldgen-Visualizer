package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class JsonFieldsHolder<T extends JsonElement> extends Widget implements JsonValueWidget<T> {
	public ArrayList<JsonValueWidget<?>> subWidgets = new ArrayList<JsonValueWidget<?>>();

	@Nullable
	public final JsonFieldsHolder<?> parent;
	private final String fieldId;
	private final String fieldPath;
	protected final Consumer<JsonFieldsHolder<?>> buttonAddFunction;

	protected float scrollAmount = 0;

	public JsonFieldsHolder(int x, int y, JsonFieldsHolder<?> parent, String fieldId, ITextComponent title, Consumer<JsonFieldsHolder<?>> buttonAddFunction) {
		super(x, y, 248, 142, title);

		this.fieldId = fieldId;
		this.parent = parent;
		this.fieldPath = parent != null ? parent.getFieldPath() + "." + fieldId : fieldId;
		this.buttonAddFunction = buttonAddFunction;
	}

	@Override
	public String getFieldPath() {
		return this.fieldPath;
	}

	protected boolean isEmpty() {
		return subWidgets.isEmpty();
	}

	@Override
	public String getFieldId() {
		return fieldId;
	}

	@Override
	public int getFGColor() {
		return isEdited() ? 0xFF6060 : super.getFGColor();
	}

	@Override
	public boolean isEdited() {
		for (JsonValueWidget<?> widget : subWidgets) {
			if (widget.isEdited())
				return true;
		}

		return false;
	}

	public void updateChanges() {
		if (parent != null) {
			parent.updateChanges();
		}
		else {
			((JsonObjectsField)this).rootSaveFunction.get();
		}
	}

	public void upOneLevel() {
		if (visible) {
			for (JsonValueWidget<?> jsonWidget : subWidgets) {
				if (jsonWidget instanceof TextFieldWidget)
					((TextFieldWidget)jsonWidget).setFocused2(false);
			}

			if (parent != null)
				parent.visible = true;

			visible = false;

			return;
		}

		for (JsonValueWidget<?> jsonWidget : subWidgets) {
			if (jsonWidget instanceof JsonFieldsHolderButton)
				((JsonFieldsHolderButton)jsonWidget).subData.upOneLevel();
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		scrollAmount += -delta;

		scrollAmount = MathHelper.clamp(scrollAmount, 0, Math.max(0, subWidgets.size() - 7));

		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		boolean result = false;

		for (JsonValueWidget<?> jsonWidget : subWidgets) {
			result |= ((Widget)jsonWidget).keyPressed(keyCode, scanCode, modifiers);
		}

		return result;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		boolean result = false;

		for (JsonValueWidget<?> jsonWidget : subWidgets) {
			result |= ((Widget)jsonWidget).charTyped(codePoint, modifiers);
		}

		return result;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		boolean result = false;

		for (JsonValueWidget<?> jsonWidget : subWidgets) {
			result |= ((Widget)jsonWidget).keyReleased(keyCode, scanCode, modifiers);
		}

		return result;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (active && visible) {
			if (isValidClickButton(button)) {
				if (clicked(mouseX, mouseY))
					onClick(mouseX, mouseY);

				for (JsonValueWidget<?> jsonWidget : subWidgets) {
					((Widget)jsonWidget).mouseClicked(mouseX, mouseY, button);
				}

				return true;
			}

			return false;
		}
		else {
			return false;
		}
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
		super.onDrag(mouseX, mouseY, dragX, dragY);

		if (subWidgets.size() > 7) {
			scrollAmount -= dragY / 20;
			scrollAmount = MathHelper.clamp(scrollAmount, 0, Math.max(0, subWidgets.size() - 7));
		}
	}
}
