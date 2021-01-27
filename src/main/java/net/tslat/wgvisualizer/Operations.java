package net.tslat.wgvisualizer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.tslat.wgvisualizer.common.network.PacketHandling;
import net.tslat.wgvisualizer.common.network.WorldgenPreSyncPacket;
import net.tslat.wgvisualizer.common.network.WorldgenSyncPacket;
import net.tslat.wgvisualizer.worldgen.ServerChunkProviderHolder;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class Operations {
	public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	public static JsonObject backupWorldgenData = null;
	public static JsonObject currentWorldgenData = null;

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

			if (overworld.getBlockState(player.getPosition()).getMaterial() != Material.AIR) {
				BlockPos pos = player.getPosition();

				player.setPosition(pos.getX(), overworld.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ()), pos.getZ());
			}
		}
	}

	public static boolean isClient() {
		return FMLEnvironment.dist == Dist.CLIENT;
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

	public static void handleSettingsSync(JsonObject data, NetworkEvent.Context packetContext) {
		backupWorldgenData = copyJsonObject(currentWorldgenData);
		ServerWorld world = packetContext.getSender().getServerWorld();
		BlockPos playerPos = packetContext.getSender().getPosition();

		for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
			currentWorldgenData.add(entry.getKey(), entry.getValue());
		}

		try {
			Dimension oldDim = world.getServer().getServerConfiguration().getDimensionGeneratorSettings().func_236224_e_().getOrDefault(world.getDimensionKey().getLocation());
			Biome oldBiome = oldDim.getChunkGenerator().getBiomeProvider().getBiomes(playerPos.getX(), playerPos.getY(), playerPos.getZ(), 1).stream().findFirst().get();
			Dimension newDim = Dimension.CODEC.decode(JsonOps.INSTANCE, currentWorldgenData.get(GenCategory.DIMENSION.toString())).resultOrPartial(msg -> {throw new IllegalArgumentException("Unable to create new Dimension based on submitted data: " + msg);}).get().getFirst();

			oldDim.dimensionTypeSupplier = () -> DimensionType.CODEC.decode(JsonOps.INSTANCE, currentWorldgenData.get(GenCategory.DIMENSION_TYPE.toString())).resultOrPartial(msg -> {throw new IllegalArgumentException("Unable to create new Dimension Type based on submitted data: " + msg);}).get().getFirst();
			oldDim.chunkGenerator = newDim.chunkGenerator;

			JsonObject biomeJson = copyJsonObject(currentWorldgenData.get(GenCategory.BIOME.toString()).getAsJsonObject());
			JsonArray structuresJson = new JsonArray();
			JsonArray featuresJson = new JsonArray();
			JsonObject featuresJsonObject = currentWorldgenData.get(GenCategory.FEATURES.toString()).getAsJsonObject();

			for (Map.Entry<String, JsonElement> entry : currentWorldgenData.get(GenCategory.STRUCTURES.toString()).getAsJsonObject().entrySet()) {
				structuresJson.add(entry.getValue());
			}

			featuresJson.add(featuresJsonObject.get("raw_generation"));
			featuresJson.add(featuresJsonObject.get("lakes"));
			featuresJson.add(featuresJsonObject.get("local_modifications"));
			featuresJson.add(featuresJsonObject.get("underground_structures"));
			featuresJson.add(featuresJsonObject.get("surface_structures"));
			featuresJson.add(featuresJsonObject.get("strongholds"));
			featuresJson.add(featuresJsonObject.get("underground_ores"));
			featuresJson.add(featuresJsonObject.get("underground_decoration"));
			featuresJson.add(featuresJsonObject.get("vegetal_decoration"));
			featuresJson.add(featuresJsonObject.get("top_layer_modification"));

			biomeJson.add("features", featuresJson);
			biomeJson.add("starts", structuresJson);

			Biome newBiome = Biome.CODEC.decode(JsonOps.INSTANCE, biomeJson).result().get().getFirst();

			oldBiome.climate = newBiome.climate;
			oldBiome.biomeGenerationSettings = newBiome.biomeGenerationSettings;
			oldBiome.depth = newBiome.depth;
			oldBiome.scale = newBiome.scale;
			oldBiome.mobSpawnInfo = newBiome.mobSpawnInfo;
			oldBiome.effects = newBiome.effects;

			oldDim.chunkGenerator.biomeProvider = new SingleBiomeProvider(oldBiome);
			oldDim.chunkGenerator.field_235949_c_ = new SingleBiomeProvider(oldBiome);
			ServerChunkProviderHolder.chunkGenerator = oldDim.chunkGenerator;
			BiomeGenerationSettings biomeSettings = oldBiome.biomeGenerationSettings;
			ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder = biomeSettings.surfaceBuilder.get().builder.func_237202_d_().decode(JsonOps.INSTANCE, currentWorldgenData.get(GenCategory.SURFACE_BUILDER.toString())).result().get().getFirst();
			biomeSettings.surfaceBuilder = () -> configuredSurfaceBuilder;
		}
		catch (Exception ex) {
			WorldGenVisualizer.LOGGER.log(Level.ERROR, "Invalid worldgen data update requested. Reverting to previous known configuration.");
			ex.printStackTrace();
			packetContext.getSender().sendMessage(new TranslationTextComponent("message." + WorldGenVisualizer.MOD_ID + ".feedback.invalidData").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);

			currentWorldgenData = backupWorldgenData;
		}

		PacketHandling.INSTANCE.send(PacketDistributor.ALL.noArg(), new WorldgenPreSyncPacket());
		resetWorldState(world.getServer());
		PacketHandling.INSTANCE.send(PacketDistributor.ALL.noArg(), new WorldgenSyncPacket(currentWorldgenData));
	}

	public static JsonObject getWorldgenData(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		Biome biome = world.getBiome(player.getPosition());
		DimensionType dimType = world.getDimensionType();
		Dimension dim = world.getServer().getServerConfiguration().getDimensionGeneratorSettings().func_236224_e_().getOrDefault(world.getDimensionKey().getLocation());
		ConfiguredSurfaceBuilder surfaceBuilder = biome.biomeGenerationSettings.getSurfaceBuilder().get();

		JsonObject biomeJson = Biome.CODEC.encodeStart(JsonOps.INSTANCE, biome).resultOrPartial(msg -> WorldGenVisualizer.LOGGER.log(Level.ERROR, "Unable to find biome for encode: " + msg)).get().getAsJsonObject();
		JsonObject dimensionJson = Dimension.CODEC.encodeStart(JsonOps.INSTANCE, dim).resultOrPartial(msg -> WorldGenVisualizer.LOGGER.log(Level.ERROR, "unable to find dimension for encode: " + msg)).get().getAsJsonObject();
		JsonObject dimensionTypeJson = DimensionType.CODEC.encodeStart(JsonOps.INSTANCE, dimType).resultOrPartial(msg -> WorldGenVisualizer.LOGGER.log(Level.ERROR, "unable to find dimension type for encode: " + msg)).get().getAsJsonObject();
		JsonArray featuresJsonArray = biomeJson.get("features").getAsJsonArray();
		JsonObject featuresJson = new JsonObject();
		JsonArray structuresJsonArray = biomeJson.get("starts").getAsJsonArray();
		JsonObject structuresJson = new JsonObject();
		String surfaceBuilderType = Registry.SURFACE_BUILDER.getKey(surfaceBuilder.builder).toString();
		JsonObject surfaceBuilderJson = ((Codec<ConfiguredSurfaceBuilder<?>>)surfaceBuilder.builder.func_237202_d_()).encodeStart(JsonOps.INSTANCE, surfaceBuilder).result().get().getAsJsonObject();

		surfaceBuilderJson.addProperty("type", surfaceBuilderType);

		featuresJson.add("raw_generation", featuresJsonArray.size() > 0 ? featuresJsonArray.get(0) : new JsonArray());
		featuresJson.add("lakes", featuresJsonArray.size() > 1 ? featuresJsonArray.get(1) : new JsonArray());
		featuresJson.add("local_modifications", featuresJsonArray.size() > 2 ? featuresJsonArray.get(2) : new JsonArray());
		featuresJson.add("underground_structures", featuresJsonArray.size() > 3 ? featuresJsonArray.get(3) : new JsonArray());
		featuresJson.add("surface_structures", featuresJsonArray.size() > 4 ? featuresJsonArray.get(4) : new JsonArray());
		featuresJson.add("strongholds", featuresJsonArray.size() > 5 ? featuresJsonArray.get(5) : new JsonArray());
		featuresJson.add("underground_ores", featuresJsonArray.size() > 6 ? featuresJsonArray.get(6) : new JsonArray());
		featuresJson.add("underground_decoration", featuresJsonArray.size() > 7 ? featuresJsonArray.get(7) : new JsonArray());
		featuresJson.add("vegetal_decoration", featuresJsonArray.size() > 8 ? featuresJsonArray.get(8) : new JsonArray());
		featuresJson.add("top_layer_modification", featuresJsonArray.size() > 9 ? featuresJsonArray.get(9) : new JsonArray());

		for (int i = 0; i < structuresJsonArray.size(); i++) {
			structuresJson.add(String.valueOf(i), structuresJsonArray.get(i));
		}

		JsonObject dataObject = new JsonObject();

		dataObject.add(Operations.GenCategory.BIOME.toString(), biomeJson);
		dataObject.add(Operations.GenCategory.DIMENSION.toString(), dimensionJson);
		dataObject.add(Operations.GenCategory.DIMENSION_TYPE.toString(), dimensionTypeJson);
		dataObject.add(Operations.GenCategory.FEATURES.toString(), featuresJson);
		dataObject.add(Operations.GenCategory.STRUCTURES.toString(), structuresJson);
		dataObject.add(Operations.GenCategory.SURFACE_BUILDER.toString(), surfaceBuilderJson);

		currentWorldgenData = dataObject;
		backupWorldgenData = dataObject;

		return currentWorldgenData;
	}

	public static JsonObject nbtToJson(CompoundNBT data) {
		try {
			return (JsonObject)NBTDynamicOps.INSTANCE.convertTo(JsonOps.INSTANCE, data);
		}
		catch (ClassCastException ex) {
			WorldGenVisualizer.LOGGER.log(Level.ERROR, "Invalidly formatted worldgen data, unable to complete update operation.");
			ex.printStackTrace();
		}

		return new JsonObject();
	}

	public static CompoundNBT jsonToNBT(JsonObject data) {
		try {
			return (CompoundNBT)JsonOps.INSTANCE.convertTo(NBTDynamicOps.INSTANCE, data);
		}
		catch (ClassCastException ex) {
			WorldGenVisualizer.LOGGER.log(Level.ERROR, "Invalidly formatted worldgen data, unable to complete update operation.");
			ex.printStackTrace();
		}

		return new CompoundNBT();
	}

	public static JsonObject copyJsonObject(JsonObject element) {
		JsonObject newObject = new JsonObject();

		for (Map.Entry<String, JsonElement> entry : element.entrySet()) {
			newObject.add(entry.getKey(), entry.getValue());
		}

		return newObject;
	}

	public enum GenCategory {
		BIOME,
		DIMENSION,
		DIMENSION_TYPE,
		FEATURES,
		STRUCTURES,
		SURFACE_BUILDER
	}
}
