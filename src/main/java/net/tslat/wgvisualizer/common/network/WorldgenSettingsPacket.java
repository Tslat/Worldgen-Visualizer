package net.tslat.wgvisualizer.common.network;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tslat.wgvisualizer.Operations;
import net.tslat.wgvisualizer.WorldGenVisualizer;
import net.tslat.wgvisualizer.client.ClientOperations;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public class WorldgenSettingsPacket {
	private CompoundNBT dataNBT;

	public WorldgenSettingsPacket(JsonObject worldgenData) {
		dataNBT = jsonToNBT(worldgenData);
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeCompoundTag(dataNBT);
	}

	public static WorldgenSettingsPacket decode(PacketBuffer buffer) {
		return new WorldgenSettingsPacket(nbtToJson(buffer.readCompoundTag()));
	}

	public void receiveMessage(Supplier<NetworkEvent.Context> context) {
		if (Operations.isClient()) {
			ClientOperations.handleSettingsSync(nbtToJson(dataNBT));
		}
		else {
			Operations.handleSettingsSync(nbtToJson(dataNBT));
		}

		context.get().setPacketHandled(true);
	}

	private static JsonObject nbtToJson(CompoundNBT data) {
		try {
			return (JsonObject)NBTDynamicOps.INSTANCE.convertTo(JsonOps.INSTANCE, data);
		}
		catch (ClassCastException ex) {
			WorldGenVisualizer.LOGGER.log(Level.ERROR, "Invalidly formatted worldgen data, unable to complete update operation.");
			ex.printStackTrace();
		}

		return new JsonObject();
	}

	private static CompoundNBT jsonToNBT(JsonObject data) {
		try {
			return (CompoundNBT)JsonOps.INSTANCE.convertTo(NBTDynamicOps.INSTANCE, data);
		}
		catch (ClassCastException ex) {
			WorldGenVisualizer.LOGGER.log(Level.ERROR, "Invalidly formatted worldgen data, unable to complete update operation.");
			ex.printStackTrace();
		}

		return new CompoundNBT();
	}
}
