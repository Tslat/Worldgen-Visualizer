package net.tslat.wgvisualizer.client;

import com.google.gson.JsonObject;
import net.tslat.wgvisualizer.client.screen.WorldgenSettingsScreen;

import javax.annotation.Nullable;

public class ClientOperations {
	public static void handleSettingsSync(JsonObject data) {
		WorldgenSettingsScreen.updateSettings(data);
	}

	public static void receiveHandshakeResponse(@Nullable String collidedPlayer) {
		WorldgenSettingsScreen.receiveHandshakeResponse(collidedPlayer);
	}
}
