package net.sophka.polaroid.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.client.ClientState;
import net.sophka.polaroid.client.renderer.CameraInHandRenderer;
import net.sophka.polaroid.client.renderer.PhotoInHandRenderer;
import net.sophka.polaroid.world.item.CameraItem;
import net.sophka.polaroid.world.item.PhotoItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// There now seems to be an event for this now, but I guess keeping it here might make potential Fabric port easier
@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {

    private PhotoInHandRenderer photoInHandRenderer;
    private CameraInHandRenderer cameraInHandRenderer;

    @Shadow
    private ItemStack offHandItem;
    @Shadow
    private ItemStack mainHandItem;

    @Inject(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    public void submitArmWithItem(AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci){
        if(photoInHandRenderer == null){
            Object self = this;
            photoInHandRenderer = new PhotoInHandRenderer((ItemInHandRenderer)self);
        }
        if(cameraInHandRenderer == null){
            Object self = this;
            cameraInHandRenderer = new CameraInHandRenderer((ItemInHandRenderer)self);
        }
        boolean isMainHand = hand == InteractionHand.MAIN_HAND;
        HumanoidArm arm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
        if (itemStack.getItem() instanceof PhotoItem) {
            if (isMainHand && this.offHandItem.isEmpty()) {
                photoInHandRenderer.renderTwoHandedPhoto(poseStack, submitNodeCollector, lightCoords, xRot, inverseArmHeight, attack, mainHandItem);
            } else {
                photoInHandRenderer.renderOneHandedPhoto(poseStack, submitNodeCollector, lightCoords, inverseArmHeight, arm, attack, itemStack);
            }
            poseStack.pushPose();
            ci.cancel();
        }
        else if (itemStack.getItem() instanceof CameraItem && ClientState.selfieMode){
            if (isMainHand && this.offHandItem.isEmpty()) {
                cameraInHandRenderer.renderTwoHandedCamera(poseStack, submitNodeCollector, lightCoords, xRot, inverseArmHeight, attack, mainHandItem);
            }
            poseStack.pushPose();
            ci.cancel();
        }
    }
}
