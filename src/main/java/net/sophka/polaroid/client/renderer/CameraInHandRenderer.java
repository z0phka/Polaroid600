package net.sophka.polaroid.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.world.item.CameraItem;
import net.sophka.polaroid.world.item.ModItemDisplayContexts;

public class CameraInHandRenderer {

    private final ItemInHandRenderer itemInHandRenderer;
    private final Minecraft minecraft;
    private final EntityRenderDispatcher entityRenderDispatcher;

    public CameraInHandRenderer(ItemInHandRenderer itemInHandRenderer){
        this.itemInHandRenderer = itemInHandRenderer;
        this.minecraft = itemInHandRenderer.minecraft;
        this.entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
    }


    /*public void renderOneHandedPhoto(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, float inverseArmHeight, HumanoidArm arm, float attackValue, ItemStack photo) {
        float invert = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        if (!this.minecraft.player.isInvisible()) {
            poseStack.pushPose();
            poseStack.translate(invert * 0.125F, -0.125F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(invert * 10.0F));
            itemInHandRenderer.renderPlayerArm(poseStack, submitNodeCollector, lightCoords, inverseArmHeight, attackValue, arm);
            poseStack.popPose();
        }

        poseStack.pushPose();
        poseStack.translate(invert * 0.625F, -0.14F + inverseArmHeight * -1.2F, -0.95F);
        if(arm == HumanoidArm.LEFT){
            poseStack.translate(-10.752f/100f * 2.275,0,0);
        }
        float sqrtAttackValue = Mth.sqrt(attackValue);
        float xSwing = Mth.sin((double)(sqrtAttackValue * (float)Math.PI));
        float xSwingPosition = -0.5F * xSwing;
        float ySwingPosition = 0.4F * Mth.sin((double)(sqrtAttackValue * ((float)Math.PI * 2F)));
        float zSwingPosition = -0.3F * Mth.sin((double)(attackValue * (float)Math.PI));
        poseStack.translate(invert * xSwingPosition, ySwingPosition - 0.3F * xSwing, zSwingPosition);
        poseStack.mulPose(Axis.XP.rotationDegrees(xSwing * -45.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(invert * xSwing * -30.0F));
        photoRenderer.renderPhoto(poseStack, submitNodeCollector, lightCoords, photo);
        poseStack.popPose();
    }*/

    public float calculateTilt(float xRot) {
       return 0;
    }

    public void renderHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, HumanoidArm arm) {
        AvatarRenderer<AbstractClientPlayer> avatarRenderer = this.entityRenderDispatcher.getPlayerRenderer(this.minecraft.player);
        poseStack.pushPose();
        float invert = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        poseStack.translate(0, 0.5,invert * 0.05F);
        poseStack.mulPose(Axis.YP.rotationDegrees(92.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(70.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(invert * -20.0F));
        poseStack.translate(invert * 0.3F, -1F, 0.45F);
        Identifier skinTexture = this.minecraft.player.getSkin().body().texturePath();
        if (arm == HumanoidArm.RIGHT) {
            avatarRenderer.renderRightHand(
                    poseStack, submitNodeCollector, lightCoords, skinTexture, this.minecraft.player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE), this.minecraft.player
            );
        } else {
            avatarRenderer.renderLeftHand(
                    poseStack, submitNodeCollector, lightCoords, skinTexture, this.minecraft.player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE), this.minecraft.player
            );
        }

        poseStack.popPose();
    }

    public void renderTwoHandedCamera(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, float xRot, float inverseArmHeight, float attackValue, ItemStack camera) {
        float sqrtAttackValue = Mth.sqrt(attackValue);
        float ySwingPosition = -0.2F * Mth.sin((double)(attackValue * (float)Math.PI));
        float zSwingPosition = -0.4F * Mth.sin((double)(sqrtAttackValue * (float)Math.PI));
        poseStack.translate(0.0F, -ySwingPosition / 2.0F, zSwingPosition);
        float mapTilt = calculateTilt(xRot);
        poseStack.translate(0.0F, -0.2F + inverseArmHeight * -1.2F + mapTilt * -0.5F, -0.95F);
        poseStack.mulPose(Axis.XP.rotationDegrees(mapTilt * -85.0F));
        if (!this.minecraft.player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            renderHand(poseStack, submitNodeCollector, lightCoords, HumanoidArm.RIGHT);
            renderHand(poseStack, submitNodeCollector, lightCoords, HumanoidArm.LEFT);
            poseStack.popPose();
        }

        float xzSwingRotation = Mth.sin((double)(sqrtAttackValue * (float)Math.PI));
        poseStack.mulPose(Axis.XP.rotationDegrees(xzSwingRotation * 20.0F));
        poseStack.translate(0,0.3,-0.025);
        itemInHandRenderer.renderItem(this.minecraft.player, camera, ModItemDisplayContexts.selfieModeFirstPersonBoth(), poseStack, submitNodeCollector, lightCoords);
    }

    public void renderOneHandedCamera(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, float inverseArmHeight, HumanoidArm arm, float attack, ItemStack itemStack) {
        float invert = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        LocalPlayer player = this.minecraft.player;
        poseStack.pushPose();
        float sqrtAttackValue = Mth.sqrt(attack);
        float ySwingPosition = -0.2F * Mth.sin((double)(attack * (float)Math.PI));
        float zSwingPosition = -0.4F * Mth.sin((double)(sqrtAttackValue * (float)Math.PI));
        poseStack.translate(0.0F, -ySwingPosition / 2.0F, zSwingPosition);
        poseStack.translate(0, -inverseArmHeight, 0);
        poseStack.translate(invert * 0.2,0.35,-0.225);
        if (!this.minecraft.player.isInvisible()) {
            poseStack.pushPose();
            poseStack.translate(-0.18 * invert,0,0.25);
            //poseStack.mulPose(Axis.XP.rotationDegrees(-invert * 5));
            //poseStack.translate(0.3,-0,0);
            //poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            //poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            //itemInHandRenderer.renderPlayerArm(poseStack, submitNodeCollector, lightCoords, inverseArmHeight, attack, arm);
            /*AvatarRenderer<AbstractClientPlayer> avatarRenderer = this.entityRenderDispatcher.getPlayerRenderer(player);
            Identifier skinTexture = player.getSkin().body().texturePath();
            if (arm == HumanoidArm.RIGHT) {
                avatarRenderer.renderRightHand(poseStack, submitNodeCollector, lightCoords, skinTexture, player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE), this.minecraft.player);
            } else {
                avatarRenderer.renderLeftHand(poseStack, submitNodeCollector, lightCoords, skinTexture, player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE), this.minecraft.player);
            }*/
            itemInHandRenderer.renderPlayerArm(poseStack, submitNodeCollector, lightCoords, 0, 0, arm);
            poseStack.popPose();
        }
        //poseStack.mulPose(Axis.YP.rotationDegrees(-45.0F));
        //poseStack.translate(invert * xSwingPosition, ySwingPosition - 0.3F * xSwing, zSwingPosition);
        //poseStack.mulPose(Axis.XP.rotationDegrees(xSwing * -45.0F));
        //poseStack.mulPose(Axis.YP.rotationDegrees(invert * xSwing * -30.0F));

        poseStack.translate(invert * 0.4F, -0.14F, -1F);
        itemInHandRenderer.renderItem(this.minecraft.player, itemStack, ModItemDisplayContexts.selfieModeFirstPerson(arm), poseStack, submitNodeCollector, lightCoords);
        poseStack.popPose();
        poseStack.popPose();
    }
}
