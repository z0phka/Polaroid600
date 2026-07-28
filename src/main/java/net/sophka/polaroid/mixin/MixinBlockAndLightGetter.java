package net.sophka.polaroid.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.LightLayer;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import net.sophka.polaroid.config.ClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockAndLightGetter.class)
public interface MixinBlockAndLightGetter extends BlockAndLightGetter{

    @Inject(method = "getBrightness", at = @At("RETURN"), cancellable = true)
    private void getBrightness(LightLayer layer, BlockPos pos, CallbackInfoReturnable<Integer> ci) {
        if(ClientConfig.FLASH_ENABLED.isFalse()){
            return;
        }
        if (layer == LightLayer.BLOCK) {
            ci.setReturnValue(Math.max(ci.getReturnValueI(), ClientPhotoTaker.instance().flashIntensityAt(pos)));
        }
    }
}
