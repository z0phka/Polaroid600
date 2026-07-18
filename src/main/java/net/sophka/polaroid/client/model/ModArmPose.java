package net.sophka.polaroid.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class ModArmPose {

    private static final IArmPoseTransformer SELFIE_BOTH_ARM_POSE_TRANSFOMER =
            (IArmPoseTransformer)(model, state, arm) -> {
                ModelPart mainArm = model.getArm(arm);
                ModelPart offhandArm = model.getArm(arm.getOpposite());

                mainArm.xRot = model.getHead().xRot - Mth.HALF_PI;
                offhandArm.xRot = model.getHead().xRot - Mth.HALF_PI;
                mainArm.yRot = -11.5f * Mth.PI/180;
                offhandArm.yRot = 11.5f * Mth.PI/180;

                offhandArm.zRot = mainArm.zRot;
    };

    private static final IArmPoseTransformer SELFIE_LEFT_ARM_POSE_TRANSFOMER =
            (IArmPoseTransformer)(model, state, arm) -> {
                ModelPart mainArm = model.getArm(arm);
                ModelPart offhandArm = model.getArm(arm.getOpposite());

                mainArm.xRot = -Mth.HALF_PI;
                offhandArm.xRot = -Mth.HALF_PI;
                mainArm.yRot = -11.5f * Mth.PI/180;
                offhandArm.yRot = 11.5f * Mth.PI/180;

                offhandArm.zRot = mainArm.zRot;
            };

    private static final IArmPoseTransformer SELFIE_RIGHT_ARM_POSE_TRANSFOMER =
            (IArmPoseTransformer)(model, state, arm) -> {
                ModelPart mainArm = model.getArm(arm);
                ModelPart offhandArm = model.getArm(arm.getOpposite());

                mainArm.xRot = -Mth.HALF_PI;
                offhandArm.xRot = -Mth.HALF_PI;
                mainArm.yRot = -11.5f * Mth.PI/180;
                offhandArm.yRot = 11.5f * Mth.PI/180;

                offhandArm.zRot = mainArm.zRot;
            };

    public static final EnumProxy<HumanoidModel.ArmPose> SELFIE_BOTH_PROXY = new EnumProxy<>(
            HumanoidModel.ArmPose.class,
            true,
            true,
            SELFIE_BOTH_ARM_POSE_TRANSFOMER);

    public static final EnumProxy<HumanoidModel.ArmPose> SELFIE_LEFT_PROXY = new EnumProxy<>(
            HumanoidModel.ArmPose.class,
            true,
            true,
            SELFIE_LEFT_ARM_POSE_TRANSFOMER);

    public static final EnumProxy<HumanoidModel.ArmPose> SELFIE_RIGHT_PROXY = new EnumProxy<>(
            HumanoidModel.ArmPose.class,
            true,
            true,
            SELFIE_RIGHT_ARM_POSE_TRANSFOMER);

    public static HumanoidModel.ArmPose selfieBothArmPose() {
        return HumanoidModel.ArmPose.valueOf("POLAROID600_SELFIE_BOTH_POSE");
    }

    public static HumanoidModel.ArmPose selfieLeftArmPose() {
        return HumanoidModel.ArmPose.valueOf("POLAROID600_SELFIE_BOTH_POSE");
    }

    public static HumanoidModel.ArmPose selfieRightArmPose() {
        return HumanoidModel.ArmPose.valueOf("POLAROID600_SELFIE_BOTH_POSE");
    }
}
