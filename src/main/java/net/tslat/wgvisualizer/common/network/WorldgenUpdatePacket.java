package net.tslat.wgvisualizer.common.network;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tslat.wgvisualizer.Operations;

import java.util.function.Supplier;

public class WorldgenUpdatePacket {
	private final CompoundNBT dataNBT;

	public WorldgenUpdatePacket(JsonObject worldgenData) {
		this.dataNBT = Operations.jsonToNBT(worldgenData);
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeCompoundTag(dataNBT);
	}

	public static WorldgenUpdatePacket decode(PacketBuffer buffer) {
		return new WorldgenUpdatePacket(Operations.nbtToJson(buffer.readCompoundTag()));
	}

	public void receiveMessage(Supplier<NetworkEvent.Context> context) {
		Operations.handleSettingsSync(Operations.nbtToJson(dataNBT), context.get());

		context.get().setPacketHandled(true);
	}
}
