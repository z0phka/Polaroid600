package net.sophka.polaroid.mixin.compatibility.iris;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import net.sophka.polaroid.config.ClientConfig;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderState.class)
public class MixinBlockEntityRenderState {
    @Inject(method = "extractBase", at = @At("RETURN"))
    private static void extractBase(BlockEntity blockEntity, BlockEntityRenderState state, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, CallbackInfo ci) {
        if(ClientConfig.FLASH_ENABLED.isFalse()){
            return;
        }
        int returned = state.lightCoords;
        int flashIntensity =  ClientPhotoTaker.instance().flashIntensityAt(state.blockPos);
        state.lightCoords = LightCoordsUtil.max(returned, LightCoordsUtil.pack(flashIntensity,0));
    }
}
