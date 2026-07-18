package net.sophka.polaroid.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.client.model.entity.CameraTripodModel;
import net.sophka.polaroid.client.renderer.entity.state.CameraTripodState;

public class TripodCameraLayer<S extends CameraTripodState, M extends CameraTripodModel> extends RenderLayer<S, M> {
    public TripodCameraLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float yRot, float xRot) {
        ItemStack cameraStack = state.cameraStack;
        if(cameraStack.isEmpty()){
            return;
        }
        poseStack.pushPose();
        this.getParentModel().translateToHead(state, poseStack);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.translate(0,-0.21875f - 1.7625 * 0.0625f,0.0625f);
        poseStack.scale(0.625f,0.625f,0.625f);
        state.cameraStackState.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }
}
