package net.tslat.wgvisualizer.mixin;

import net.minecraft.world.chunk.storage.RegionFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(RegionFile.class)
public class RegionFileMixin {
	@Inject(at = @At("HEAD"), method = "func_227143_c_()V", cancellable = true)
	private void func_227143_c_(CallbackInfo callback) throws IOException {
		callback.cancel();
	}
}
