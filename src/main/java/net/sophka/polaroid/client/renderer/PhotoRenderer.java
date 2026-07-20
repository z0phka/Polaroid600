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
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.config.ClientConfig;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.utils.Utils;
import net.sophka.polaroid.world.item.FilmFormat;
import net.sophka.polaroid.world.item.PhotoItem;

import java.util.Optional;

public class PhotoRenderer {
    public record Frame(Identifier identifier, RenderType frameRenderType, int frameWidth, int frameHeight,
                        int leftMargin, int rightMargin, int topMargin, int bottomMargin) {
        public static Frame create(Identifier identifier, int frameWidth, int frameHeight, int leftMargin, int rightMargin, int topMargin, int bottomMargin) {
            return new Frame(identifier, RenderTypes.entityCutout(identifier), frameWidth, frameHeight, leftMargin, rightMargin, topMargin, bottomMargin);
        }
    }

    public static final Frame _600 = Frame.create(
            Identifier.fromNamespaceAndPath(Polaroid600.MODID, "textures/photo/600_frame.png"),
            64, 78, 4, 4, 4, 18
    );

    public static final Frame _1200 = Frame.create(
                    Identifier.fromNamespaceAndPath(Polaroid600.MODID, "textures/photo/1200_frame.png"),
            74, 75, 4, 4, 6, 17
    );

    public static final Frame GO = Frame.create(
            Identifier.fromNamespaceAndPath(Polaroid600.MODID, "textures/photo/go_frame.png"),
            39, 47, 3, 3, 3, 11
    );

    public static final Style FONT = Style.EMPTY.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath(Polaroid600.MODID, "marker")));

    private static final Identifier _1x1_RES = Identifier.fromNamespaceAndPath(Polaroid600.MODID, "textures/utils/1x1.png");
    private static final RenderType _1x1_TYPE = RenderTypes.entityTranslucent(_1x1_RES);
    private final Minecraft minecraft;

    public PhotoRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public static Frame frame(FilmFormat format) {
        return switch (format) {
            case _GO -> GO;
            case _600 -> _600;
            case _1200 -> _1200;
            default -> _600;
        };
    }

    public void renderPhoto(GuiGraphicsExtractor graphics, ItemStack itemStack, int x, int y, float scale) {


        PhotoCache photoCache = PhotoCache.getInstance();
        Optional<PhotoCache.PhotoData> optData = photoCache.get(itemStack.get(ModDataComponents.PHOTO));
        optData.ifPresent(photoData -> {
            FilmFormat format = itemStack.getOrDefault(ModDataComponents.FILM_FORMAT, photoData.format());
            Frame frame = frame(format);
            float frameWidth = frame.frameWidth() * scale;
            float frameHeight = frame.frameHeight() * scale;
            float leftMargin = frame.leftMargin() * scale;
            float rightMargin = frame.rightMargin() * scale;
            float topMargin = frame.topMargin() * scale;
            float bottomMargin = frame.bottomMargin() * scale;

            graphics.blit(RenderPipelines.GUI_TEXTURED, frame.identifier, Math.round(x - frameWidth / 2f), Math.round(y - frameHeight / 2f), 0, 0, (int) frameWidth, (int) frameHeight, (int) frameWidth, (int) frameHeight);
            PhotoCache.PhotoData data = optData.get();
            double progress = PhotoItem.developmentProgress(minecraft.level, itemStack);
            double sunDamage = PhotoItem.sunDamage(minecraft.level, itemStack);
            int value = (int) Math.round(progress * 255);
            int imageColor = ARGB.color(255, value, value, value);

            graphics.blit(RenderPipelines.GUI_TEXTURED, data.texture(), Math.round(x + leftMargin - frameWidth / 2f), Math.round(y + topMargin - frameHeight / 2f), 0, 0, (int) (frameWidth - leftMargin - rightMargin), (int) (frameHeight - topMargin - bottomMargin), (int) (frameWidth - leftMargin - rightMargin), (int) (frameHeight - topMargin - bottomMargin), imageColor);

            if (sunDamage > 0) {
                int damageColor = ARGB.color((int) Math.round(255 * sunDamage), 0xffffff);
                graphics.fill((int) (x + leftMargin - frameWidth / 2f), Math.round(y + topMargin - frameHeight / 2f), Math.round(x + frameWidth / 2f - rightMargin), (int) (y + frameHeight / 2f - bottomMargin), damageColor);
            }
            if (progress < 1) {
                int opacifierColor = ARGB.color((int) Math.round(255 * Utils.clampUnit(1 - progress * 1.25)), 84, 169, 229);
                graphics.fill((int) (x + leftMargin - frameWidth / 2f), Math.round(y + topMargin - frameHeight / 2f), Math.round(x + frameWidth / 2f - rightMargin), (int) (y + frameHeight / 2f - bottomMargin), opacifierColor);
            }

            if (itemStack.getCustomName() != null) {
                MutableComponent component = Component.literal(itemStack.getCustomName().tryCollapseToString());
                if(ClientConfig.CUSTOM_MARKER_FONT.get()){
                    component.setStyle(FONT);
                }
                int fontX = x - minecraft.font.width(component) / 2;
                int fontY = Math.round(y - minecraft.font.lineHeight/2f + frameHeight/2f - bottomMargin/2f);
                graphics.text(minecraft.font, component, fontX, fontY, 0xFF2D2D2D, false);
            }
        });
    }

    public void renderPhoto(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, ItemStack itemStack) {
        renderPhoto(poseStack, submitNodeCollector, lightCoords, itemStack, 0.00390625f);
    }

    public void renderPhoto(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, ItemStack itemStack, float scale) {
        PhotoCache photoCache = PhotoCache.getInstance();
        Optional<PhotoCache.PhotoData> optData = photoCache.get(itemStack.get(ModDataComponents.PHOTO));
        optData.ifPresent(photoData -> {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.scale(scale, scale, scale);
            poseStack.translate(-0.5F, -0.5F, 0.0F);

            FilmFormat format = itemStack.getOrDefault(ModDataComponents.FILM_FORMAT, photoData.format());
            Frame frame = frame(format);

            RenderType renderType = frame.frameRenderType();

            float frameWidth = frame.frameWidth();
            float frameHeight = frame.frameHeight();
            float leftMargin = frame.leftMargin();
            float rightMargin = frame.rightMargin();
            float topMargin = frame.topMargin();
            float bottomMargin = frame.bottomMargin();

            submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                buffer.addVertex(pose, 0, frameHeight, 0.0F).setColor(-1).setUv(0.0F, 1.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                buffer.addVertex(pose, frameWidth, frameHeight, 0.0F).setColor(-1).setUv(1.0F, 1.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                buffer.addVertex(pose, frameWidth, 0, 0.0F).setColor(-1).setUv(1.0F, 0.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                buffer.addVertex(pose, 0, 0, 0.0F).setColor(-1).setUv(0.0F, 0.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
            });


            double progress = PhotoItem.developmentProgress(minecraft.level, itemStack);
            int value = (int) Math.round(progress * 255);
            int imageColor = ARGB.color(255, value, value, value);

            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(photoData.texture()), (pose, buffer) -> {
                buffer.addVertex(pose, leftMargin, frameHeight - bottomMargin, -0.01F).setColor(imageColor).setUv(0.0F, 1.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                buffer.addVertex(pose, frameWidth - rightMargin, frameHeight - bottomMargin, -0.01F).setColor(imageColor).setUv(1.0F, 1.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                buffer.addVertex(pose, frameWidth - rightMargin, topMargin, -0.01F).setColor(imageColor).setUv(1.0F, 0.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                buffer.addVertex(pose, leftMargin, topMargin, -0.01F).setColor(imageColor).setUv(0.0F, 0.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
            });

            double sunDamage = PhotoItem.sunDamage(minecraft.level, itemStack);
            if (sunDamage > 0) {
                //TODO: Replace with actual image editing for sun damage
                int damageColor = ARGB.color((int) Math.round(255 * sunDamage), 0xffffff);
                submitNodeCollector.submitCustomGeometry(poseStack, _1x1_TYPE, (pose, buffer) -> {
                    buffer.addVertex(pose, leftMargin, frameHeight - bottomMargin, -0.012F).setColor(damageColor).setUv(0.0F, 1.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                    buffer.addVertex(pose, frameWidth - rightMargin, frameHeight - bottomMargin, -0.0125F).setColor(damageColor).setUv(1.0F, 1.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                    buffer.addVertex(pose, frameWidth - rightMargin, topMargin, -0.012F).setColor(damageColor).setUv(1.0F, 0.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                    buffer.addVertex(pose, leftMargin, topMargin, -0.012F).setColor(damageColor).setUv(0.0F, 0.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                });
            }

            if (progress < 1) {
                int opacifierColor = ARGB.color((int) Math.round(255 * Utils.clampUnit(1 - progress * 1.25)), 84, 169, 229);
                submitNodeCollector.submitCustomGeometry(poseStack, _1x1_TYPE, (pose, buffer) -> {
                    buffer.addVertex(pose, leftMargin, frameHeight - bottomMargin, -0.0125F).setColor(opacifierColor).setUv(0.0F, 1.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                    buffer.addVertex(pose, frameWidth - rightMargin, frameHeight - bottomMargin, -0.0125F).setColor(opacifierColor).setUv(1.0F, 1.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                    buffer.addVertex(pose, frameWidth - rightMargin, topMargin, -0.0125F).setColor(opacifierColor).setUv(1.0F, 0.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                    buffer.addVertex(pose, leftMargin, topMargin, -0.0125F).setColor(opacifierColor).setUv(0.0F, 0.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
                });
            }
            if (itemStack.getCustomName() != null) {
                poseStack.translate(0, 0, -0.1F);
                poseStack.scale(0.4f, 0.4f, 0.4f);
                MutableComponent component = Component.literal(itemStack.getCustomName().tryCollapseToString());
                if(ClientConfig.CUSTOM_MARKER_FONT.get()){
                    component.setStyle(FONT);
                }

                int fontX = Math.round(frameWidth * (10/4f) / 2f - minecraft.font.width(component)/2f);
                int fontY = Math.round(frameHeight * (10/4f) - minecraft.font.lineHeight/2f - bottomMargin * (10/4f) /2f);
                submitNodeCollector.submitText(poseStack, fontX, fontY, component.getVisualOrderText(),false, Font.DisplayMode.NORMAL, lightCoords, 0xFF2D2D2D, 0, 0);
            }
        });
    }
}
