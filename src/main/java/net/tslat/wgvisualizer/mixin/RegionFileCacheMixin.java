package net.tslat.wgvisualizer.mixin;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.RegionFileCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(RegionFileCache.class)
public abstract class RegionFileCacheMixin {
	@Inject(at = @At("HEAD"), method = "writeChunk(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/CompoundNBT;)V", cancellable = true)
	private void writeChunk(ChunkPos pos, CompoundNBT compound, CallbackInfo callback) {
		callback.cancel();
	}

	@Inject(at = @At("HEAD"), method = "func_235987_a_()V", cancellable = true)
	private void func_235987_a_(CallbackInfo callback) throws IOException {
		callback.cancel();
	}
}
