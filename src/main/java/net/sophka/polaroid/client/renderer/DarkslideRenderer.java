package net.sophka.polaroid.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.config.ClientConfig;
import net.sophka.polaroid.data.darkslide.Darkslide;
import net.sophka.polaroid.data.darkslide.DarkslideManager;
import net.sophka.polaroid.data.darkslide.DarkslideSeries;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.utils.Utils;
import net.sophka.polaroid.world.item.FilmFormat;
import net.sophka.polaroid.world.item.PhotoItem;

import java.util.Optional;

public class DarkslideRenderer {
    public record Frame(Identifier identifier, RenderType frameRenderType, int frameWidth, int frameHeight) {
        public static Frame create(Identifier identifier, int frameWidth, int frameHeight) {
            return new Frame(identifier, RenderTypes.entityCutout(identifier), frameWidth, frameHeight);
        }
    }

    public static final Frame FRAME = Frame.create(
            Identifier.fromNamespaceAndPath(Polaroid600.MODID, "textures/photo/darkslide/darkslide.png"),
            64, 78
    );

    private final Identifier ARROW = Identifier.fromNamespaceAndPath(Polaroid600.MODID, "textures/photo/darkslide/arrow.png");
    private final Identifier MISSING = Identifier.fromNamespaceAndPath(Polaroid600.MODID, "missing");

    private final Minecraft minecraft;

    public DarkslideRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }


    public void renderDarkslide(GuiGraphicsExtractor graphics, ItemStack itemStack, int x, int y, float scale) {
        Frame frame = FRAME;
        float frameTextureWidth = frame.frameWidth() * scale;
        float frameTextureHeight = frame.frameHeight() * scale;
        graphics.blit(RenderPipelines.GUI_TEXTURED, frame.identifier, Math.round(x - frameTextureWidth / 2f), Math.round(y - frameTextureHeight / 2f), 0, 0, (int) frameTextureWidth, (int) frameTextureHeight, (int) frameTextureWidth, (int) frameTextureHeight);
        Font font = minecraft.font;

        DarkslideManager.CLIENT_INSTANCE.get(itemStack.getOrDefault(ModDataComponents.DARKSLIDE, MISSING)).ifPresent(
                darkslide -> {
                    float frameWidth = frame.frameWidth() * scale;
                    float frameHeight = frame.frameHeight() * scale;

                    float deltaY = 3 * scale;

                    int topLeftX = Math.round(x - (frameWidth/2f));
                    int topLeftY = Math.round(y - (frameHeight/2f) + deltaY);

                    frameHeight -= deltaY;

                    int lineHeight = minecraft.font.lineHeight;

                    int arrowSize = Math.round(2 * lineHeight * 0.45f);
                    graphics.blit(RenderPipelines.GUI_TEXTURED, ARROW, Math.round(topLeftX + frameWidth/2f), topLeftY + lineHeight, 0, 0,  arrowSize, arrowSize, arrowSize,arrowSize);


                    paragraph(graphics, "Instant film this side up.\nDo not remove this darkslide.", topLeftX + frameWidth/2f + arrowSize + 2, topLeftY + lineHeight,0xffffffff, 0.45f);
                    paragraph(graphics, darkslide.text(), topLeftX + lineHeight, topLeftY + Math.round(frameHeight/3f),0xffffffff, 1.5f);
                    paragraph(graphics, "polaroid", topLeftX + lineHeight, topLeftY + frameHeight - 2.5f * lineHeight ,0xffffffff, 1);

                    String ordinal = String.format("%02d/%02d", darkslide.ordinal(), darkslide.series().size());
                    paragraph(graphics, ordinal,topLeftX + frameWidth * 0.5f,topLeftY + frameHeight - 2.5f * lineHeight,0xffffffff, 0.5f);
                    paragraph(graphics, darkslide.series().seriesName() + "\npolaroid.com",topLeftX + frameWidth * 0.5f + width(ordinal, 0.5f) + lineHeight/2f,topLeftY + frameHeight - 2.5f * lineHeight,0xffffffff, 0.5f);
                });
    }

    public float width(String text, float scale){
        return minecraft.font.width(text) * scale;
    }

    public void paragraph(GuiGraphicsExtractor graphics, String text, Number x, Number y, int color, float scale){
        paragraph(graphics, text, Math.round(x.floatValue()), Math.round(y.floatValue()), color, scale);
    }

    public void paragraph(GuiGraphicsExtractor graphics, String text, int x, int y, int color, float scale){
        int n = 0;
        graphics.pose().pushMatrix();
        graphics.pose().translate(x,y);
        graphics.pose().scale(scale);
        for(String line : text.split("\n")){
            graphics.text(minecraft.font, line, 0, 0 + minecraft.font.lineHeight * n, color, false);
            n++;
        }
        graphics.pose().popMatrix();
    }
}
