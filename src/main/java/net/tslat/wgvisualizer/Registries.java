package net.tslat.wgvisualizer;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tslat.wgvisualizer.worldgen.TweakableNoiseChunkGenerator;

import javax.annotation.Nullable;

public final class Registries {
	protected static final DeferredRegister<ForgeWorldType> WORLD_TYPES = DeferredRegister.create(ForgeRegistries.WORLD_TYPES, WorldGenVisualizer.MOD_ID);

	public static final RegistryObject<ForgeWorldType> WORLD_GEN_TWEAKING = WORLD_TYPES.register("world_gen_tweaking", () -> new ForgeWorldType((biomeRegistry, dimensionSettingsRegistry, seed) -> new NoiseChunkGenerator(new SingleBiomeProvider(biomeRegistry.getOrThrow(Biomes.PLAINS)), seed, () -> {return dimensionSettingsRegistry.getOrThrow(DimensionSettings.field_242734_c);})));

	public static void handleDynamicRegistrations() {
		Registry.register(Registry.CHUNK_GENERATOR_CODEC, new ResourceLocation(WorldGenVisualizer.MOD_ID, "tweakable"), TweakableNoiseChunkGenerator.CODEC);
	}

	@Nullable
	public static Biome getTweakableBiome(Registry<Biome> registry) {
		RegistryKey<Biome> biomeKey = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation(WorldGenVisualizer.MOD_ID, "tweakable_biome"));

		return registry.getValueForKey(biomeKey);
	}
}
