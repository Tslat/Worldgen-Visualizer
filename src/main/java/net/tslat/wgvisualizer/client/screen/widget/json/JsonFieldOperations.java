package net.tslat.wgvisualizer.client.screen.widget.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.tslat.wgvisualizer.WorldGenVisualizer;
import net.tslat.wgvisualizer.client.screen.widget.JsonValueWidget;
import org.apache.logging.log4j.Level;

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

	public static JsonValueWidget<?> jsonToWidget(int x, int y, String fieldId, JsonElement jsonElement, JsonElement titleObject) {
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive primitiveElement = jsonElement.getAsJsonPrimitive();

			if (primitiveElement.isString()) {
				return new JsonTextField(Minecraft.getInstance().fontRenderer, x, y, fieldId, primitiveElement.getAsString(), primitiveElement.getAsString(), new StringTextComponent(primitiveElement.getAsString()));
			}
			else if (primitiveElement.isBoolean()) {
				return new JsonBooleanButton(x, y, fieldId, primitiveElement.getAsBoolean(), primitiveElement.getAsBoolean(), new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".boolean." + primitiveElement.getAsBoolean()));
			}
			else if (primitiveElement.isNumber()) {

			}
		}
		else if (jsonElement.isJsonObject()) {
			JsonObjectsField data = new JsonObjectsField(fieldId, null, jsonElement.getAsJsonObject(), jsonElement.getAsJsonObject(), new StringTextComponent(""));

			return new JsonFieldsHolderButton<JsonObject>(x, y, fieldId, data, new StringTextComponent(fieldId), button -> {});
		}
		else if (jsonElement.isJsonArray()) {
			JsonListField data = new JsonListField(fieldId, null, jsonElement.getAsJsonArray(), jsonElement.getAsJsonArray(), new StringTextComponent(""));

			return new JsonFieldsHolderButton<JsonArray>(x, y, fieldId, data, new StringTextComponent(fieldId), button -> {});
		}

		return new JsonBooleanButton(x, y, fieldId, false, false, new TranslationTextComponent("button." + WorldGenVisualizer.MOD_ID + ".boolean." + false));
	}
}
