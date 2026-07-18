package net.sophka.polaroid.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.renderer.entity.state.CameraTripodState;

public class CameraTripodModel extends EntityModel<CameraTripodState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Polaroid600.MODID, "camera_tripod"), "main");
    private final ModelPart body;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart head;
    private final ModelPart handle;

    public CameraTripodModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.leg1 = this.body.getChild("leg1");
        this.leg2 = this.body.getChild("leg2");
        this.leg3 = this.body.getChild("leg3");
        this.head = this.body.getChild("head");
        this.handle = this.head.getChild("handle");
    }

    public static LayerDefinition createCameraTripodModel() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(23, 19).addBox(-1.0F, 8.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 21).addBox(-0.5F, -5.4F, -0.5F, 1.0F, 14.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.4F, 0.0F));

        PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.6F, 0.0F));

        PartDefinition cube_r1 = leg1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(5, 21).addBox(-0.5F, 7.0F, -0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 15.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition leg2 = body.addOrReplaceChild("leg2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.6F, 0.0F));

        PartDefinition cube_r2 = leg2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(14, 21).addBox(-0.5F, 7.0F, -0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(14, 0).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 15.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3491F, 2.0944F, 0.0F));

        PartDefinition leg3 = body.addOrReplaceChild("leg3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.6F, 0.0F));

        PartDefinition cube_r3 = leg3.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(7, 0).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 15.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(21, 10).addBox(-0.5F, 7.0F, -0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3491F, -2.0944F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(21, 15).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(21, 0).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(23, 23).addBox(1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(5, 26).addBox(-2.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.4F, 0.0F));

        PartDefinition handle = head.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(21, 4).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.5F, -0.5F, -0.4363F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void setupAnim(CameraTripodState state) {
        super.setupAnim(state);
        this.head.yRot = state.headRot * Mth.DEG_TO_RAD;
    }

    public void translateToHead(CameraTripodState state, PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.head.translateAndRotate(poseStack);
    }
}