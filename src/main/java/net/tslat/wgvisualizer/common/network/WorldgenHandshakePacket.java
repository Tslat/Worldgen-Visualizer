package net.tslat.wgvisualizer.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.tslat.wgvisualizer.Operations;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class WorldgenHandshakePacket {
	public static boolean handshakeRequestReceived = false;
	public static String operatingPlayer = null;

	public WorldgenHandshakePacket() {}

	public void encode(PacketBuffer buffer) {}

	public static WorldgenHandshakePacket decode(PacketBuffer buffer) {
		return new WorldgenHandshakePacket();
	}

	public void receiveMessage(Supplier<NetworkEvent.Context> context) {
		String sendingPlayer = ((StringTextComponent)context.get().getSender().getName()).getText();

		if (operatingPlayer != null && operatingPlayer.equals(sendingPlayer))
			operatingPlayer = null;

		PacketHandling.INSTANCE.send(PacketDistributor.PLAYER.with(() -> context.get().getSender()), new WorldgenResponsePacket(operatingPlayer));

		if (!handshakeRequestReceived) {
			handshakeRequestReceived = true;
			operatingPlayer = sendingPlayer;

			Operations.scheduler.schedule(() -> {
				handshakeRequestReceived = false;
				operatingPlayer = null;
			}, 2, TimeUnit.SECONDS);
		}

		context.get().setPacketHandled(true);
	}
}
