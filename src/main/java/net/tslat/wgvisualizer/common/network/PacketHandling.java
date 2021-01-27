package net.tslat.wgvisualizer.common.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.tslat.wgvisualizer.WorldGenVisualizer;

public class PacketHandling {
	private static final String REV = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(WorldGenVisualizer.MOD_ID, "default_channel"), () -> REV, REV::equals, REV::equals);

	public static void init() {
		INSTANCE.registerMessage(0, WorldgenSyncPacket.class, WorldgenSyncPacket::encode, WorldgenSyncPacket::decode, WorldgenSyncPacket::receiveMessage);
		INSTANCE.registerMessage(1, WorldgenUpdatePacket.class, WorldgenUpdatePacket::encode, WorldgenUpdatePacket::decode, WorldgenUpdatePacket::receiveMessage);
		INSTANCE.registerMessage(2, WorldgenHandshakePacket.class, WorldgenHandshakePacket::encode, WorldgenHandshakePacket::decode, WorldgenHandshakePacket::receiveMessage);
		INSTANCE.registerMessage(3, WorldgenResponsePacket.class, WorldgenResponsePacket::encode, WorldgenResponsePacket::decode, WorldgenResponsePacket::receiveMessage);
		INSTANCE.registerMessage(4, WorldgenPreSyncPacket.class, WorldgenPreSyncPacket::encode, WorldgenPreSyncPacket::decode, WorldgenPreSyncPacket::receiveMessage);
	}
}
