package net.tslat.wgvisualizer.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.tslat.wgvisualizer.Operations;
import net.tslat.wgvisualizer.client.ClientOperations;

import javax.annotation.Nullable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class WorldgenHandshakePacket {
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	public static boolean handshakeRequestReceived = false;
	public static String operatingPlayer = null;

	@Nullable
	private String collidedPlayer = null;

	public WorldgenHandshakePacket() {}

	public WorldgenHandshakePacket(String collidedPlayer) {
		this.collidedPlayer = collidedPlayer;
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeBoolean(collidedPlayer != null);

		if (collidedPlayer != null)
			buffer.writeString(collidedPlayer);
	}

	public static WorldgenHandshakePacket decode(PacketBuffer buffer) {
		return new WorldgenHandshakePacket(buffer.readBoolean() ? buffer.readString() : null);
	}

	public void receiveMessage(Supplier<NetworkEvent.Context> context) {
		if (Operations.isClient()) {
			ClientOperations.receiveHandshakeResponse(collidedPlayer);
		}
		else {
			if (operatingPlayer != null && operatingPlayer.equals(collidedPlayer))
				operatingPlayer = null;

			PacketHandling.INSTANCE.send(PacketDistributor.PLAYER.with(() -> context.get().getSender()), new WorldgenHandshakePacket(operatingPlayer));

			if (!handshakeRequestReceived) {
				handshakeRequestReceived = true;
				operatingPlayer = ((StringTextComponent)context.get().getSender().getName()).getText();

				scheduler.schedule(() -> {
					handshakeRequestReceived = false;
					operatingPlayer = null;
				}, 2, TimeUnit.SECONDS);
			}
		}

		context.get().setPacketHandled(true);
	}
}
