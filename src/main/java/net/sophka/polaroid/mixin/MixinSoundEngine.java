package net.sophka.polaroid.mixin;

import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.ListenerTransform;
import net.minecraft.client.Camera;
import net.minecraft.client.sounds.SoundEngine;
import net.sophka.polaroid.client.ClientState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {
    @Inject(method = "updateSource", at = @At(value = "INVOKE"), cancellable = true)
    public void updateSource(Camera camera, CallbackInfo ci) {
        if(camera.entity() == ClientState.selfieViewEntity()){
            ci.cancel();
        }
    }
}


