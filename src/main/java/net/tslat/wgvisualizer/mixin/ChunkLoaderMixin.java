package net.tslat.wgvisualizer.mixin;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.ChunkLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(ChunkLoader.class)
public class ChunkLoaderMixin {
	@Inject(at = @At("HEAD"), method = "readChunk(Lnet/minecraft/util/math/ChunkPos;)Lnet/minecraft/nbt/CompoundNBT;", cancellable = true)
	private void readChunk(ChunkPos p_227078_1_, CallbackInfoReturnable<CompoundNBT> callback) throws IOException {
		callback.setReturnValue(null);
	}
}
