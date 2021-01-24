package net.tslat.wgvisualizer;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.tslat.wgvisualizer.worldgen.ServerChunkProviderHolder;

import java.io.IOException;

public final class Operations {
	public static void resetWorldState(MinecraftServer server) {
		ServerWorld overworld = server.getWorld(World.OVERWORLD);
		ChunkManager chunkManager = overworld.getChunkProvider().chunkManager;
		int renderDistance = chunkManager.viewDistance;

		for (ServerPlayerEntity player : overworld.getPlayers()) {
			SectionPos sectionPos = player.getManagedSectionPos();
			int x = sectionPos.getSectionX();
			int z = sectionPos.getSectionZ();

			for (int chunkPosX = x - renderDistance; chunkPosX <= x + renderDistance; chunkPosX++) {
				for (int chunkPosZ = z - renderDistance; chunkPosZ <= z + renderDistance; chunkPosZ++) {
					chunkManager.setChunkLoadedAtClient(player, new ChunkPos(chunkPosX, chunkPosZ), new IPacket[2], true, false);
				}
			}
		}

		try {
			overworld.getChunkProvider().close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

		overworld.field_241102_C_ = ServerChunkProviderHolder.retrieveServerChunkProvider(overworld);
		chunkManager = overworld.getChunkProvider().chunkManager;
		renderDistance = chunkManager.viewDistance;

		for (ServerPlayerEntity player : overworld.getPlayers()) {
			SectionPos sectionPos = player.getManagedSectionPos();
			int x = sectionPos.getSectionX();
			int z = sectionPos.getSectionZ();

			chunkManager.setPlayerTracking(player, true);

			for (int chunkPosX = x - renderDistance; chunkPosX <= x + renderDistance; chunkPosX++) {
				for (int chunkPosZ = z - renderDistance; chunkPosZ <= z + renderDistance; chunkPosZ++) {
					chunkManager.setChunkLoadedAtClient(player, new ChunkPos(chunkPosX, chunkPosZ), new IPacket[2], false, true);
				}
			}
		}
	}

	public static boolean isClient() {
		return LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
	}

	public static void handleSettingsSync(JsonObject data) {

	}

	public static String toTitleCase(String str) {
		str = str.toLowerCase();
		int size = str.length();
		StringBuilder buffer = new StringBuilder(size);
		boolean space = true;

		for (int i = 0; i < size; i++) {
			char ch = str.charAt(i);

			if (Character.isWhitespace(ch) || ch == '_') {
				buffer.append(' ');
				space = true;
			}
			else if (ch == ':') {
				buffer.append(':');
				space = true;
			}
			else if (space) {
				buffer.append(Character.toTitleCase(ch));
				space = false;
			}
			else {
				buffer.append(ch);
			}
		}

		return buffer.toString();
	}
}
