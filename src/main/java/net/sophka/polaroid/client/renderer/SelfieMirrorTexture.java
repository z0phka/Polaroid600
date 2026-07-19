package net.sophka.polaroid.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.renderer.texture.AbstractTexture;

public class SelfieMirrorTexture extends AbstractTexture {
    private final RenderTarget renderTarget;
    public SelfieMirrorTexture(RenderTarget renderTarget){
        this.renderTarget = renderTarget;
        this.texture = renderTarget.getColorTexture();
        this.textureView = renderTarget.getColorTextureView();
        this.sampler = RenderSystem.getSamplerCache()
                .getSampler(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.NEAREST, FilterMode.NEAREST, false);
    }
}
