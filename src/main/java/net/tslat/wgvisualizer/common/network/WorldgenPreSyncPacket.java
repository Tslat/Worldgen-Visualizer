package net.tslat.wgvisualizer.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tslat.wgvisualizer.client.ClientOperations;

import java.util.function.Supplier;

public class WorldgenPreSyncPacket {
	public WorldgenPreSyncPacket() {}

	public void encode(PacketBuffer buffer) {}

	public static WorldgenPreSyncPacket decode(PacketBuffer buffer) {
		return new WorldgenPreSyncPacket();
	}

	public void receiveMessage(Supplier<NetworkEvent.Context> context) {
		ClientOperations.handleSettingsPreSync();

		context.get().setPacketHandled(true);
	}
}
