package net.tslat.wgvisualizer.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.tslat.wgvisualizer.WorldGenVisualizer;
import net.tslat.wgvisualizer.client.screen.WorldgenSettingsScreen;
import net.tslat.wgvisualizer.common.PacketHandling;
import net.tslat.wgvisualizer.common.WorldgenSettingsPacket;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = WorldGenVisualizer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Keybinds {
	public static KeyBinding WORLDGEN_SETTINGS_GUI;

	public static void registerKeybinds() {
		ClientRegistry.registerKeyBinding(WORLDGEN_SETTINGS_GUI = new KeyBinding("key." + WorldGenVisualizer.MOD_ID + ".openGui", GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories." + WorldGenVisualizer.MOD_ID));
	}

	@SubscribeEvent
	public static void onKeyPress(final InputEvent.KeyInputEvent ev) {
		Minecraft mc = Minecraft.getInstance();

		if (WORLDGEN_SETTINGS_GUI.isPressed() && mc.player != null) {
			if (mc.currentScreen == null) {
				PacketHandling.INSTANCE.sendToServer(new WorldgenSettingsPacket());
				//mc.displayGuiScreen(new WorldgenSettingsScreen());
			}
			else if (mc.currentScreen instanceof WorldgenSettingsScreen) {
				//mc.displayGuiScreen(null);
			}
		}
	}
}
