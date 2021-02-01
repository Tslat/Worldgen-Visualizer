package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.tslat.wgvisualizer.Operations;
import net.tslat.wgvisualizer.WorldGenVisualizer;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class JsonFieldOperations {
	public static HashMap<String, Object> jsonToMap(JsonObject jsonObject) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if (entry.getValue().isJsonPrimitive()) {
				JsonPrimitive entryValue = entry.getValue().getAsJsonPrimitive();

				if (entryValue.isBoolean()) {
					map.put(entry.getKey(), entryValue.getAsBoolean());
				}
				else if (entryValue.isNumber()) {
					map.put(entry.getKey(), entryValue.getAsNumber());
				}
				else if (entryValue.isString()) {
					map.put(entry.getKey(), entryValue.getAsString());
				}
			}
			else if (entry.getValue().isJsonObject()) {
				map.put(entry.getKey(), jsonToMap(entry.getValue().getAsJsonObject()));
			}
			else if (entry.getValue().isJsonArray()) {
				map.put(entry.getKey(), jsonToArray(entry.getValue().getAsJsonArray()));
			}
		}

		return map;
	}

	public static ArrayList<?> jsonToArray(JsonArray jsonArray) {
		if (jsonArray.size() == 0)
			return new ArrayList<>();

		try {
			JsonElement testElement = jsonArray.get(0);

			if (testElement.isJsonPrimitive()) {
				JsonPrimitive testElementPrimitive = testElement.getAsJsonPrimitive();

				if (testElementPrimitive.isBoolean()) {
					ArrayList<Boolean> array = new ArrayList<Boolean>();

					for (JsonElement element : jsonArray) {
						array.add(element.getAsBoolean());
					}

					return array;
				}
				else if (testElementPrimitive.isString()) {
					ArrayList<String> array = new ArrayList<String>();

					for (JsonElement element : jsonArray) {
						array.add(element.getAsString());
					}

					return array;
				}
				else if (testElementPrimitive.isNumber()) {
					ArrayList<Number> array = new ArrayList<Number>();
					Number numType = testElement.getAsNumber();

					if (numType instanceof Integer) {
						for (JsonElement element : jsonArray) {
							array.add(element.getAsJsonPrimitive().getAsInt());
						}
					}
					else if (numType instanceof Float) {
						for (JsonElement element : jsonArray) {
							array.add(element.getAsJsonPrimitive().getAsFloat());
						}
					}
					else if (numType instanceof Double) {
						for (JsonElement element : jsonArray) {
							array.add(element.getAsJsonPrimitive().getAsDouble());
						}
					}
					else if (numType instanceof Long) {
						for (JsonElement element : jsonArray) {
							array.add(element.getAsJsonPrimitive().getAsLong());
						}
					}
					else if (numType instanceof Byte) {
						for (JsonElement element : jsonArray) {
							array.add(element.getAsJsonPrimitive().getAsByte());
						}
					}
				}
			}
			else if (testElement.isJsonArray()) {
				ArrayList<ArrayList<?>> array = new ArrayList<ArrayList<?>>();

				for (JsonElement element : jsonArray) {
					array.add(jsonToArray(element.getAsJsonArray()));
				}

				return array;
			}
			else if (testElement.isJsonObject()) {
				ArrayList<HashMap<String, ?>> array = new ArrayList<HashMap<String, ?>>();

				for (JsonElement element : jsonArray) {
					array.add(jsonToMap(element.getAsJsonObject()));
				}

				return array;
			}
		}
		catch (ClassCastException ex) {
			WorldGenVisualizer.LOGGER.log(Level.ERROR, "Attempted to convert multi-type JSON Array. This functionality is not currently supported.");

			ex.printStackTrace();
		}

		return new ArrayList<>();
	}

	public static JsonValueWidget<?> jsonToWidget(int x, int y, String fieldId, @Nullable JsonElement defaultElement, JsonElement currentElement, JsonFieldsHolder<?> parent) {
		if (currentElement.isJsonPrimitive()) {
			JsonPrimitive primitiveElement = currentElement.getAsJsonPrimitive();

			if (primitiveElement.isString()) {
				return new JsonTextField(Minecraft.getInstance().fontRenderer, x, y, fieldId, parent, defaultElement != null ? defaultElement.getAsString() : "NULL", currentElement.getAsString(), new StringTextComponent(Operations.toTitleCase(primitiveElement.getAsString())));
			}
			else if (primitiveElement.isBoolean()) {
				return new JsonBooleanButton(x, y, fieldId, parent, defaultElement != null ? defaultElement.getAsBoolean() : !currentElement.getAsBoolean(), currentElement.getAsBoolean(), new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".boolean." + primitiveElement.getAsBoolean()));
			}
			else if (primitiveElement.isNumber()) {
				return new JsonNumberField(Minecraft.getInstance().fontRenderer, x, y, fieldId, parent, defaultElement != null ? defaultElement.getAsNumber() : currentElement.getAsNumber(), currentElement.getAsNumber(), new TranslationTextComponent(primitiveElement.getAsNumber().toString()));
			}
		}
		else if (currentElement.isJsonObject()) {
			JsonObjectsField data = new JsonObjectsField(parent.x, parent.y, fieldId, parent, defaultElement != null ? defaultElement.getAsJsonObject() : new JsonObject(), currentElement.getAsJsonObject(), new StringTextComponent(""), parent.buttonAddFunction);
			data.visible = false;

			parent.buttonAddFunction.accept(data);

			return new JsonFieldsHolderButton<JsonObject>(x, y, fieldId, data, new StringTextComponent(Operations.toTitleCase(fieldId)));
		}
		else if (currentElement.isJsonArray()) {
			JsonListField data = new JsonListField(parent.x, parent.y, fieldId, parent, defaultElement != null ? defaultElement.getAsJsonArray() : new JsonArray(), currentElement.getAsJsonArray(), new StringTextComponent(""), parent.buttonAddFunction);
			data.visible = false;

			parent.buttonAddFunction.accept(data);

			return new JsonFieldsHolderButton<JsonArray>(x, y, fieldId, data, new StringTextComponent(Operations.toTitleCase(fieldId)));
		}

		return new JsonBooleanButton(x, y, fieldId, parent, false, false, new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".boolean." + false));
	}

	public static ArrayList<String> createBreadcrumb(FontRenderer fontRenderer, String fieldPath) {
		ArrayList<String> newBreadcrumb = new ArrayList<String>();
		String rawString = Operations.toTitleCase(fieldPath).replaceAll("\\.", " > ");

		while (newBreadcrumb.isEmpty() || fontRenderer.getStringWidth(newBreadcrumb.get(newBreadcrumb.size() - 1)) > 240) {
			if (newBreadcrumb.size() >= 3) {
				newBreadcrumb.clear();

				rawString = rawString.replaceFirst(">", "");
				rawString = "... > " + rawString.substring(rawString.indexOf(">") + 2);
			}
			else {
				StringBuilder builder = new StringBuilder();
				String[] splitString = rawString.split(" > ");

				for (String crumb : splitString) {
					String fancyCrumb = Operations.toTitleCase(crumb);

					if (fontRenderer.getStringWidth(builder.toString() + fancyCrumb + " > ") > 240) {
						newBreadcrumb.add(builder.toString() + " >");

						builder = new StringBuilder();
						builder.append(fancyCrumb);
					}
					else {
						builder.append(" > ").append(fancyCrumb);
					}
				}

				newBreadcrumb.add(builder.toString());
			}
		}

		return newBreadcrumb;
	}
}
