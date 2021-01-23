package net.tslat.wgvisualizer.client.screen.widget;

import com.google.gson.JsonElement;

public interface JsonValueWidget<T extends JsonElement> {
	int JSON_WIDGET_WIDTH = 100;
	int JSON_WIDGET_HEIGHT = 15;

	void updateValue(T value);

	T getJsonValue();

	String getFieldId();

	boolean isEdited();

	default int getElementHeight() {
		return 15;
	}
}
