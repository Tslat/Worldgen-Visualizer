package net.tslat.wgvisualizer.event;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.tslat.wgvisualizer.Operations;
import net.tslat.wgvisualizer.WorldGenVisualizer;
import net.tslat.wgvisualizer.client.ClientOperations;
import net.tslat.wgvisualizer.common.network.PacketHandling;
import net.tslat.wgvisualizer.common.network.WorldgenSyncPacket;

import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = WorldGenVisualizer.MOD_ID)
public class Events {
	@SubscribeEvent
	public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent ev) {
		if (!ev.getPlayer().world.isRemote()) {
			Operations.scheduler.schedule(() -> {
				ServerPlayerEntity player = (ServerPlayerEntity)ev.getPlayer();
				JsonObject data = Operations.getWorldgenData(player);

				PacketHandling.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new WorldgenSyncPacket(data));
			}, 500, TimeUnit.MILLISECONDS);
		}
		else {
			ClientOperations.handleWorldJoin();
		}
	}
}
