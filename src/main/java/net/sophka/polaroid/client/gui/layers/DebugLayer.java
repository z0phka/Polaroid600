package net.sophka.polaroid.client.gui.layers;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.sophka.polaroid.client.ClientState;

public class DebugLayer implements GuiLayer {
    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        //graphics.blit(ClientState.selfieMirrorTexture().getTextureView(), ClientState.selfieMirrorTexture().getSampler(), 0,0,128,128,0,1,1,0);
    }
}
