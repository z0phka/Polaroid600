package net.sophka.polaroid.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.world.item.FilmFormat;

public class PhotoInHandRenderer {

    private final ItemInHandRenderer itemInHandRenderer;
    private final Minecraft minecraft;
    private final PhotoRenderer photoRenderer;
    private final EntityRenderDispatcher entityRenderDispatcher;

    public PhotoInHandRenderer(ItemInHandRenderer itemInHandRenderer){
        this.itemInHandRenderer = itemInHandRenderer;
        this.minecraft = itemInHandRenderer.minecraft;
        this.photoRenderer = new PhotoRenderer(minecraft);
        this.entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
    }


    public void renderOneHandedPhoto(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, float inverseArmHeight, HumanoidArm arm, float attackValue, ItemStack photo) {
        float invert = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        if (!this.minecraft.player.isInvisible()) {
            poseStack.pushPose();
            poseStack.translate(invert * 0.125F, -0.125F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(invert * 10.0F));
            itemInHandRenderer.renderPlayerArm(poseStack, submitNodeCollector, lightCoords, inverseArmHeight, attackValue, arm);
            poseStack.popPose();
        }

        poseStack.pushPose();
        PhotoCache.getInstance().get(photo.getOrDefault(ModDataComponents.PHOTO,"")).ifPresent(data ->
        {

            FilmFormat format = photo.getOrDefault(ModDataComponents.FILM_FORMAT, data.format());
            PhotoRenderer.Frame frame = PhotoRenderer.frame(format);
            PhotoRenderer.Frame _600 = PhotoRenderer._600;

            float scale = 0.00390625f;

            poseStack.translate(-scale * frame.frameWidth()/2f,0,0);
            poseStack.translate(invert * 0.625F, -0.14F + inverseArmHeight * -1.2F, -0.95F);
            float sqrtAttackValue = Mth.sqrt(attackValue);
            float xSwing = Mth.sin(sqrtAttackValue * (float)Math.PI);
            float xSwingPosition = -0.5F * xSwing;
            float ySwingPosition = 0.4F * Mth.sin(sqrtAttackValue * ((float)Math.PI * 2F));
            float zSwingPosition = -0.3F * Mth.sin(attackValue * (float)Math.PI);

            poseStack.translate(invert * xSwingPosition, ySwingPosition - 0.3F * xSwing, zSwingPosition);
            poseStack.mulPose(Axis.XP.rotationDegrees(xSwing * -45.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(invert * xSwing * -30.0F));
            poseStack.translate(invert * scale * frame.frameWidth() * (_600.frameWidth() / (float)frame.frameWidth()), 1.5 * 0.1875F * (frame.frameHeight() - _600.frameHeight()) / (float)_600.frameHeight() , -0.15f);

            photoRenderer.renderPhoto(poseStack, submitNodeCollector, lightCoords, photo);
        });
        poseStack.popPose();
    }

    public float calculateTilt(float xRot) {
        return 0;
    }

    public void renderHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, HumanoidArm arm) {
        AvatarRenderer<AbstractClientPlayer> avatarRenderer = this.entityRenderDispatcher.getPlayerRenderer(this.minecraft.player);
        poseStack.pushPose();
        float invert = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        poseStack.translate(0, 0,-invert * 0.1F);
        poseStack.mulPose(Axis.YP.rotationDegrees(92.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(invert * -50.0F));
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

    public void renderTwoHandedPhoto(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, float xRot, float inverseArmHeight, float attackValue, ItemStack photo) {
        float sqrtAttackValue = Mth.sqrt(attackValue);
        float ySwingPosition = -0.2F * Mth.sin(attackValue * (float)Math.PI);
        float zSwingPosition = -0.4F * Mth.sin(sqrtAttackValue * (float)Math.PI);
        poseStack.translate(0.0F, -ySwingPosition / 2.0F, zSwingPosition);
        float mapTilt = calculateTilt(xRot);
        poseStack.translate(0.0F, 0.04F + inverseArmHeight * -1.2F + mapTilt * -0.5F, -0.75F);
        poseStack.mulPose(Axis.XP.rotationDegrees(mapTilt * -85.0F));
        if (!this.minecraft.player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            renderHand(poseStack, submitNodeCollector, lightCoords, HumanoidArm.RIGHT);
            renderHand(poseStack, submitNodeCollector, lightCoords, HumanoidArm.LEFT);
            poseStack.popPose();
        }

        PhotoCache.getInstance().get(photo.getOrDefault(ModDataComponents.PHOTO,"")).ifPresent(data ->
        {
            FilmFormat format = photo.getOrDefault(ModDataComponents.FILM_FORMAT, data.format());
            PhotoRenderer.Frame frame = PhotoRenderer.frame(format);

            float xzSwingRotation = Mth.sin(sqrtAttackValue * (float) Math.PI);
            poseStack.mulPose(Axis.XP.rotationDegrees(xzSwingRotation * 20.0F));
            PhotoRenderer.Frame _600 = PhotoRenderer._600;
            poseStack.translate(-0.1875F * (frame.frameWidth() / (float)_600.frameWidth()), 2 * 0.1875F * (frame.frameHeight() - _600.frameHeight()) / (float)_600.frameHeight() , -0.15f);
            poseStack.scale(1.5F, 1.5F, 1.5F);

            photoRenderer.renderPhoto(poseStack, submitNodeCollector, lightCoords, photo);
        });
    }
}
