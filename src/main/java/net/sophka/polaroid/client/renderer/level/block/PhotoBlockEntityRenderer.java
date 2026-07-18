package net.sophka.polaroid.client.renderer.level.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.ShelfRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.renderer.PhotoRenderer;
import net.sophka.polaroid.world.block.entity.PhotoBlockEntity;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

public class PhotoBlockEntityRenderer implements BlockEntityRenderer<PhotoBlockEntity, PhotoBlockEntityRenderState> {

    private final PhotoRenderer photoRenderer;

    public PhotoBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.photoRenderer = new PhotoRenderer(Minecraft.getInstance());
    }

    @Override
    public PhotoBlockEntityRenderState createRenderState() {
        return new PhotoBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(PhotoBlockEntity blockEntity, PhotoBlockEntityRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(blockEntity, renderState, crumblingOverlay);
        renderState.items = blockEntity.getItems();
        renderState.facing = blockEntity.getBlockState().getValue(ShelfBlock.FACING);
    }
    @Override
    public void submit(PhotoBlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {

        float yRot = renderState.facing.getAxis().isHorizontal() ? -renderState.facing.toYRot() : 180.0F;
        for (int slot = 0; slot < renderState.items.size(); slot++) {
            ItemStack itemStack = renderState.items.get(slot);
            if (!itemStack.isEmpty()) {
                this.submitItem(renderState, itemStack, poseStack, collector, slot, yRot);
            }
        }
    }
    private void submitItem(
            PhotoBlockEntityRenderState state, ItemStack stack, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int slot, float yRot
    ) {
        float width = (10.752f/100f);
        float height = (13.104f/100f);

        float freeWidth = 1 - (width * PhotoBlockEntity.COLUMNS);
        float freeHeight = 1 - (height * PhotoBlockEntity.ROWS);

        float gapX = freeWidth / (PhotoBlockEntity.COLUMNS);
        float gapY = freeHeight / (PhotoBlockEntity.ROWS);

        int y = Mth.floorDiv(slot, PhotoBlockEntity.COLUMNS);
        int x = slot % PhotoBlockEntity.COLUMNS;
        Vec3 itemOffset = new Vec3(x * (width + gapX) + gapX/2f - 0.5, -y*(height + gapY) - gapY/2f + 0.5 , -0.495);
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.translate(itemOffset);
        poseStack.scale(0.25F, 0.25F, 0.25F);
        photoRenderer.renderPhoto(poseStack,submitNodeCollector,state.lightCoords, stack,(1/16f)*width);
        poseStack.popPose();
    }
}
