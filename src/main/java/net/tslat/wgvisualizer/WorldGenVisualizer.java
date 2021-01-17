package net.tslat.wgvisualizer;

import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.tslat.wgvisualizer.client.Keybinds;
import net.tslat.wgvisualizer.common.PacketHandling;

import static net.tslat.wgvisualizer.WorldGenVisualizer.MOD_ID;

@Mod(MOD_ID)
public class WorldGenVisualizer {
	public static final String VERSION = "1.0";
	public static final String MOD_ID = "wgvisualizer";

	public WorldGenVisualizer() {
		initRegistries();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.addListener(this::playerLogin);
	}

	private void initRegistries() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		Registries.WORLD_TYPES.register(modEventBus);
	}

	@SubscribeEvent
	public void commonSetup(final FMLCommonSetupEvent ev) {
		ev.enqueueWork(Registries::handleDynamicRegistrations);
		PacketHandling.init();
	}

	@SubscribeEvent
	public void clientSetup(final FMLClientSetupEvent ev) {
		Keybinds.registerKeybinds();
	}

	@SubscribeEvent
	public void playerLogin(final PlayerEvent.PlayerLoggedInEvent ev) {
		ev.getPlayer().sendMessage(new TranslationTextComponent("message." + MOD_ID + ".feedback.loginWarning").mergeStyle(TextFormatting.DARK_RED), Util.DUMMY_UUID);
	}
}
