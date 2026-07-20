package net.sophka.polaroid.mixin;

import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.ListenerTransform;
import net.sophka.polaroid.client.ClientState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Listener.class)
public class MixinListener {
    @Inject(method = "setTransform", at = @At(value = "INVOKE"), cancellable = true)
    public void setTransform(ListenerTransform transform, CallbackInfo ci) {
        if(ClientState.isSelfieCameraPass()){
            ci.cancel();
        }
    }
}


