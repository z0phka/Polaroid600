package net.sophka.polaroid.client.gui.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.renderer.PhotoRenderer;

public class PhotoScreen extends Screen {
    private final PhotoRenderer photoRenderer;
    private final ItemStack stack;


    public PhotoScreen(ItemStack stack) {
        super(stack.getDisplayName());
        this.stack = stack;
        this.photoRenderer = new PhotoRenderer(getMinecraft());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        photoRenderer.renderPhoto(graphics,stack,width/2, height/2,2.5f);
    }
}
