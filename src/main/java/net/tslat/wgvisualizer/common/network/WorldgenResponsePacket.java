package net.tslat.wgvisualizer.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tslat.wgvisualizer.client.ClientOperations;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class WorldgenResponsePacket {
	@Nullable
	private String collidedPlayer;

	public WorldgenResponsePacket(String collidedPlayer) {
		this.collidedPlayer = collidedPlayer;
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeBoolean(collidedPlayer != null);

		if (collidedPlayer != null)
			buffer.writeString(collidedPlayer);
	}

	public static WorldgenResponsePacket decode(PacketBuffer buffer) {
		return new WorldgenResponsePacket(buffer.readBoolean() ? buffer.readString() : null);
	}

	public void receiveMessage(Supplier<NetworkEvent.Context> context) {
		ClientOperations.receiveHandshakeResponse(collidedPlayer);

		context.get().setPacketHandled(true);
	}
}
