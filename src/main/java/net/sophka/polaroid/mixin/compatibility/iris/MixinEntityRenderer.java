package net.sophka.polaroid.mixin.compatibility.iris;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import net.sophka.polaroid.config.ClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity> {

    //No more mr nice gaius, we are done here, just take the light value please
    @Inject(method = "getBlockLightLevel", at = @At("RETURN"), cancellable = true)
    private void get(T entity, BlockPos blockPos, CallbackInfoReturnable<Integer> ci) {
        if(ClientConfig.FLASH_ENABLED.isFalse()){
            return;
        }
        int returned = ci.getReturnValueI();
        int flashIntensity =  ClientPhotoTaker.instance().flashIntensityAt(blockPos);
        ci.setReturnValue(Math.max(returned, flashIntensity));
    }
}
