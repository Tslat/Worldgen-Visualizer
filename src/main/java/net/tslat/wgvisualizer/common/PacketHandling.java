package net.tslat.wgvisualizer.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.tslat.wgvisualizer.WorldGenVisualizer;

public class PacketHandling {
	private static final String REV = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(WorldGenVisualizer.MOD_ID, "default_channel"), () -> REV, REV::equals, REV::equals);

	public static void init() {
		INSTANCE.registerMessage(0, WorldgenSettingsPacket.class, WorldgenSettingsPacket::encode, WorldgenSettingsPacket::decode, WorldgenSettingsPacket::receiveMessage);
	}
}
