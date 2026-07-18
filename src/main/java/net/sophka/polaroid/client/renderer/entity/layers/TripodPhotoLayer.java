package net.sophka.polaroid.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.client.model.entity.CameraTripodModel;
import net.sophka.polaroid.client.renderer.PhotoCache;
import net.sophka.polaroid.client.renderer.PhotoRenderer;
import net.sophka.polaroid.client.renderer.entity.state.CameraTripodState;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.utils.Utils;
import net.sophka.polaroid.world.item.FilmFormat;

public class TripodPhotoLayer<S extends CameraTripodState, M extends CameraTripodModel> extends RenderLayer<S, M> {
    private final PhotoRenderer photoRenderer;
    public TripodPhotoLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
        this.photoRenderer = new PhotoRenderer(Minecraft.getInstance());
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float yRot, float xRot) {
        ItemStack photoStack = state.photoStack;
        if(photoStack.isEmpty()){
            return;
        }
        PhotoCache.getInstance().get(photoStack.getOrDefault(ModDataComponents.PHOTO,"")).ifPresent(data ->
        {

            poseStack.pushPose();
            this.getParentModel().translateToHead(state, poseStack);
            //poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            long deltaTime = Minecraft.getInstance().level.getGameTime() - photoStack.getOrDefault(ModDataComponents.CREATED_TIME, 0L);
            float zOffset = -0.0625f * 2 * Utils.clampUnit(deltaTime / 20f);
            float width = (10.752f / 100f);

            FilmFormat format = photoStack.getOrDefault(ModDataComponents.FILM_FORMAT, data.format());
            PhotoRenderer.Frame frame = PhotoRenderer.frame(format);
            float sizeRatio = frame.frameWidth() / (float) PhotoRenderer._600.frameWidth();
            //sizeRatio = 1;
            poseStack.translate(sizeRatio * (2/3f) * (-width), 7.85 * 0.0625f, zOffset);
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            photoRenderer.renderPhoto(poseStack, submitNodeCollector, state.lightCoords, photoStack, (1 / 16f) * width * (1 / 3f));
            poseStack.popPose();
            poseStack.popPose();
        });
    }
}
