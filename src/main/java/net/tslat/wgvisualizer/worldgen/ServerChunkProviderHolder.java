package net.tslat.wgvisualizer.worldgen;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.SaveFormat;

import java.util.List;
import java.util.concurrent.Executor;

public class ServerChunkProviderHolder {
	private static MinecraftServer server;
	private static Executor executor;
	private static SaveFormat.LevelSave save;
	private static IServerWorldInfo worldInfo;
	private static RegistryKey<World> world;
	private static DimensionType dimType;
	private static IChunkStatusListener chunkListener;
	public static ChunkGenerator chunkGenerator;
	private static boolean isDebug;
	private static long seed;
	private static List<ISpecialSpawner> spawners;
	private static boolean isOverworld;

	public static void takeServerWorld(MinecraftServer server, Executor executor, SaveFormat.LevelSave save, IServerWorldInfo worldInfo, RegistryKey<World> world, DimensionType dimType, IChunkStatusListener chunkListener, ChunkGenerator chunkGenerator, boolean isDebug, long seed, List<ISpecialSpawner> spawners, boolean isOverworld) {
		ServerChunkProviderHolder.server = server;
		ServerChunkProviderHolder.executor = executor;
		ServerChunkProviderHolder.save = save;
		ServerChunkProviderHolder.worldInfo = worldInfo;
		ServerChunkProviderHolder.world = world;
		ServerChunkProviderHolder.dimType = dimType;
		ServerChunkProviderHolder.chunkListener = chunkListener;
		ServerChunkProviderHolder.chunkGenerator = chunkGenerator;
		ServerChunkProviderHolder.isDebug = isDebug;
		ServerChunkProviderHolder.seed = seed;
		ServerChunkProviderHolder.spawners = spawners;
		ServerChunkProviderHolder.isOverworld = isOverworld;
	}

	public static ServerChunkProvider retrieveServerChunkProvider(ServerWorld world) {
		return new ServerChunkProvider(
				world,
				ServerChunkProviderHolder.save,
				ServerChunkProviderHolder.server.getDataFixer(),
				ServerChunkProviderHolder.server.getTemplateManager(),
				ServerChunkProviderHolder.executor,
				ServerChunkProviderHolder.chunkGenerator,
				ServerChunkProviderHolder.server.getPlayerList().getViewDistance(),
				ServerChunkProviderHolder.server.func_230540_aS_(),
				ServerChunkProviderHolder.chunkListener,
				() -> ServerChunkProviderHolder.server.func_241755_D_().getSavedData());
	}
}
