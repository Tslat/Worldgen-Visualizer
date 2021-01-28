package net.tslat.wgvisualizer.client;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.tslat.wgvisualizer.client.screen.WorldgenSettingsScreen;

import javax.annotation.Nullable;

public class ClientOperations {
	public static JsonObject currentWorldgenData = null;

	public static void handleSettingsSync(JsonObject data) {
		currentWorldgenData = data;

		WorldgenSettingsScreen.updateSettings(data);
	}

	public static void handleWorldJoin() {
		WorldgenSettingsScreen.reset();
	}

	public static void handleSettingsPreSync() {
		ClientWorld world = Minecraft.getInstance().world;

		if (world != null) {
			for (Entity entity : world.getAllEntities()) {
				if (entity.getType() != EntityType.PLAYER)
					entity.remove(false);
			}

			world.removeAllEntities();
		}
	}

	public static void receiveHandshakeResponse(@Nullable String collidedPlayer) {
		WorldgenSettingsScreen.receiveHandshakeResponse(collidedPlayer);
	}
}
