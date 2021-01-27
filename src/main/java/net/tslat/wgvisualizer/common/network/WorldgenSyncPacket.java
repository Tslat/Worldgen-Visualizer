package net.tslat.wgvisualizer.common.network;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tslat.wgvisualizer.Operations;
import net.tslat.wgvisualizer.client.ClientOperations;

import java.util.function.Supplier;

public class WorldgenSyncPacket {
	private final CompoundNBT dataNBT;

	public WorldgenSyncPacket(JsonObject worldgenData) {
		this.dataNBT = Operations.jsonToNBT(worldgenData);
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeCompoundTag(dataNBT);
	}

	public static WorldgenSyncPacket decode(PacketBuffer buffer) {
		return new WorldgenSyncPacket(Operations.nbtToJson(buffer.readCompoundTag()));
	}

	public void receiveMessage(Supplier<NetworkEvent.Context> context) {
		ClientOperations.handleSettingsSync(Operations.nbtToJson(dataNBT));

		context.get().setPacketHandled(true);
	}
}
