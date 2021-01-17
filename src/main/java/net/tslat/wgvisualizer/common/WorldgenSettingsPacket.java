package net.tslat.wgvisualizer.common;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class WorldgenSettingsPacket {
	public void encode(PacketBuffer buffer) {}

	public static WorldgenSettingsPacket decode(PacketBuffer buffer) {
		return new WorldgenSettingsPacket();
	}

	public void receiveMessage(Supplier<NetworkEvent.Context> context) {


		context.get().setPacketHandled(true);
	}
}
