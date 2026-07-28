package net.sophka.polaroid.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.ShaderManager;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Shadow
    private ShaderManager shaderManager;

    //TODO: Figure out how to do this properly, so shaders work and uniforms can be passed

    @Inject(method = "getTransparencyChain", at = @At("RETURN"), cancellable = true)
    private void extractArmedEntityRenderState(CallbackInfoReturnable<PostChain> ci) {
        ClientPhotoTaker photoTaker = ClientPhotoTaker.instance();
        if (photoTaker.getState() == ClientPhotoTaker.State.TAKING_PHOTO) {
            ci.cancel();
            ci.setReturnValue(shaderManager.getPostChain(photoTaker.getAutofocus() ? ClientPhotoTaker.dofAutofocusEffect : ClientPhotoTaker.dofEffect, LevelTargetBundle.SORTING_TARGETS));
        }
    }

}
