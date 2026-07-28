package net.sophka.polaroid.client.renderer.level.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.client.ClientState;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record SelfieMirrorRenderer(Vec3 topLeft, Vec3 bottomRight) implements SpecialModelRenderer<Void> {
    @Override
    public void submit(@Nullable Void argument, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if(!ClientState.selfieMode || !ClientState.isSelfieTextureReady() || Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON){
            return;
        }

        poseStack.pushPose();

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(ClientState.selfieMirrorTextureIdentifier), (pose, buffer) -> {
            buffer.addVertex(pose, (float) topLeft.x/16f, (float) bottomRight.y/16f, (float) topLeft.z/16f).setColor(-1).setUv(0.0F, 0.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
            buffer.addVertex(pose, (float) topLeft.x/16f, (float) topLeft.y/16f, (float) topLeft.z/16f).setColor(-1).setUv(0.0F, 1.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
            buffer.addVertex(pose, (float) bottomRight.x/16f, (float) topLeft.y/16f, (float) bottomRight.z/16f).setColor(-1).setUv(1F, 1.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
            buffer.addVertex(pose, (float) bottomRight.x/16f, (float) bottomRight.y/16f, (float) bottomRight.z/16f).setColor(-1).setUv(1.0F, 0.0F).setUv1(OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V).setNormal(pose, 0, 0, -1).setLight(lightCoords);
        });
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {

    }

    @Override
    public @Nullable Void extractArgument(ItemStack stack) {
        return null;
    }

    public record Unbaked(Vec3 topLeft, Vec3 bottomRight) implements SpecialModelRenderer.Unbaked<Void> {

        public static final MapCodec<SelfieMirrorRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Vec3.CODEC.fieldOf("top_left").forGetter(SelfieMirrorRenderer.Unbaked::topLeft),
                        Vec3.CODEC.fieldOf("bottom_right").forGetter(SelfieMirrorRenderer.Unbaked::bottomRight)
                ).apply(instance, SelfieMirrorRenderer.Unbaked::new)
        );

        @Override
        public MapCodec<SelfieMirrorRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<Void> bake(SpecialModelRenderer.BakingContext ctx) {
            return new SelfieMirrorRenderer(topLeft, bottomRight);
        }
    }
}
