package net.sophka.polaroid.client.gui.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.client.renderer.DarkslideRenderer;

public class DarkslideScreen extends Screen {
    private final DarkslideRenderer darkslideRenderer;
    private final ItemStack stack;


    public DarkslideScreen(ItemStack stack) {
        super(stack.getDisplayName());
        this.stack = stack;
        this.darkslideRenderer = new DarkslideRenderer(getMinecraft());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        darkslideRenderer.renderDarkslide(graphics,stack,width/2, height/2,2.5f);
    }
}
