package net.sophka.polaroid.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.model.entity.CameraTripodModel;
import net.sophka.polaroid.client.renderer.entity.layers.TripodCameraLayer;
import net.sophka.polaroid.client.renderer.entity.layers.TripodPhotoLayer;
import net.sophka.polaroid.client.renderer.entity.state.CameraTripodState;
import net.sophka.polaroid.world.entity.CameraTripodEntity;

public class CameraTripodRenderer extends LivingEntityRenderer<CameraTripodEntity, CameraTripodState, CameraTripodModel> {
    private final Identifier texture = Identifier.fromNamespaceAndPath(Polaroid600.MODID,"textures/entity/tripod/tripod.png");

    public CameraTripodRenderer(EntityRendererProvider.Context context) {
        super(context, new CameraTripodModel(context.bakeLayer(CameraTripodModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new TripodCameraLayer<>(this));
        this.addLayer(new TripodPhotoLayer<>(this));
    }

    @Override
    public CameraTripodState createRenderState() {
        return new CameraTripodState();
    }

    @Override
    public void submit(CameraTripodState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public Identifier getTextureLocation(CameraTripodState state) {
        return this.texture;
    }

    @Override
    protected boolean shouldShowName(CameraTripodEntity entity, double distanceToCameraSq) {
        return entity.isCustomNameVisible();
    }

    @Override
    public void extractRenderState(CameraTripodEntity entity, CameraTripodState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        float headRot = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.getYHeadRot());
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        state.cameraStack = entity.getCamera();
        state.photoStack = entity.getPhoto();
        state.headRot = headRot - bodyYaw;
        itemModelResolver.updateForLiving(state.cameraStackState, state.cameraStack, ItemDisplayContext.FIXED, entity);
    }
}
