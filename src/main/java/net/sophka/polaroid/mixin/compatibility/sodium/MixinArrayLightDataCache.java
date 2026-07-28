package net.sophka.polaroid.mixin.compatibility.sodium;

import net.minecraft.core.BlockPos;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import net.sophka.polaroid.config.ClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//TODO: Stop rawdogging Sodium and idk, add it as a proper optional depependency and maybe use API, if there even is one
@Pseudo
@Mixin(remap = false, targets = "net.caffeinemc.mods.sodium.client.model.light.data.ArrayLightDataCache")
public class MixinArrayLightDataCache {
    @Inject(method = "get(III)I", at = @At("RETURN"), cancellable = true)
    private void get(int x, int y, int z, CallbackInfoReturnable<Integer> ci) {
        if(ClientConfig.FLASH_ENABLED.isFalse()){
            return;
        }
        int returned = ci.getReturnValueI();
        int blockLight = returned & 15;
        int flashIntensity =  ClientPhotoTaker.instance().flashIntensityAt(new BlockPos(x, y, z));

        if(flashIntensity > 0){
            ci.setReturnValue(((returned >> 4) << 4) | (Math.max(blockLight, flashIntensity)));
        }
    }
}
