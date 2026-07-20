package net.sophka.polaroid.client.renderer.level;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.*;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.ClientState;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.world.entity.CameraViewEntity;
import org.joml.Matrix4f;

public class LevelToTargetRenderer {

    private final Minecraft minecraft;

    private GpuTexture vanillaColorTexture;
    private GpuTextureView vanillaColorTextureView;
    private GpuTexture vanillaDepthTexture;
    private GpuTextureView vanillaDepthTextureView;

    public LevelToTargetRenderer(Minecraft minecraft){
        this.minecraft = minecraft;
    }

    public void render(RenderTarget target, Entity entity){
        boolean guiHidden = minecraft.gui.hud.isHidden();
        Entity cameraEntity = minecraft.getCameraEntity();
        CameraType cameraType = minecraft.options.getCameraType();
        CloudStatus cloudStatus = minecraft.options.getCloudStatus();
        int oldHeight = minecraft.gameRenderer.gameRenderState().windowRenderState.height;
        int oldWidth = minecraft.gameRenderer.gameRenderState().windowRenderState.width;
        int guiScale = minecraft.getWindow().getGuiScale();
        RenderTarget mainRenderTarget = minecraft.gameRenderer.mainRenderTarget();
        Camera camera = minecraft.gameRenderer.mainCamera();
        Vec3 oldCameraPos = camera.position();

        float oldRotX = camera.xRot();
        float oldRotY = camera.yRot();
        float oldRoll = camera.getRoll();


        if (entity != null) {
            camera.setEntity(entity);
            camera.eyeHeight = entity.getEyeHeight();
            camera.eyeHeightOld = entity.getEyeHeight();
            camera.setPosition(entity.getX(), entity.getY(), entity.getZ());
        }

        if (!minecraft.gui.hud.isHidden()) {
            minecraft.gui.hud.toggle();
        }
        minecraft.options.setCameraType(CameraType.FIRST_PERSON);
        //TODO: Really figure out how to make clouds stay without having to do this
        minecraft.options.cloudStatus().set(CloudStatus.OFF);

        hijackRenderTarget(target);
        minecraft.getWindow().setWidth(target.width);
        minecraft.getWindow().setHeight(target.height);

        mainRenderTarget.width = target.width;
        mainRenderTarget.height = target.height;

        DeltaTracker deltaTracker = minecraft.getDeltaTracker();

        minecraft.gameRenderer.update(DeltaTracker.ONE);
        minecraft.gameRenderer.extract(DeltaTracker.ONE, true);
        minecraft.gameRenderer.render(deltaTracker, true);
        mainRenderTarget.width = oldWidth;
        mainRenderTarget.height = oldHeight;
        minecraft.getWindow().setWidth(oldWidth);
        minecraft.getWindow().setHeight(oldHeight);
        minecraft.getWindow().setGuiScale(guiScale);

        releaseRenderTarget();

        minecraft.options.cloudStatus().set(cloudStatus);
        minecraft.options.setCameraType(cameraType);

        if (minecraft.gui.hud.isHidden() != guiHidden) {
            minecraft.gui.hud.toggle();
        }


        if (entity != null) {
            camera.setEntity(cameraEntity);
            camera.eyeHeight = cameraEntity.getEyeHeight();
            camera.eyeHeightOld = cameraEntity.getEyeHeight();
            camera.setPosition(oldCameraPos.x, oldCameraPos.y, oldCameraPos.z);
            camera.setRotation(oldRotY, oldRotX, oldRoll);
        }
    }

    private Matrix4f createProjectionMatrixForCulling(Camera camera, int width, int height) {
        float fovForCulling = Math.max(camera.fov, this.minecraft.options.fov().get().intValue());
        Matrix4f projection = new Matrix4f();
        return projection.perspective(
                fovForCulling * (float) (Math.PI / 180.0),
                (float) width / (float) height,
                0.05F,
                camera.depthFar,
                RenderSystem.getDevice().getDeviceInfo().isZZeroToOne()
        );
    }

    public void hijackRenderTarget(RenderTarget renderTarget) {
        RenderTarget mainRenderTarget = minecraft.gameRenderer.mainRenderTarget();
        vanillaColorTexture = mainRenderTarget.colorTexture;
        vanillaColorTextureView = mainRenderTarget.colorTextureView;
        vanillaDepthTexture = mainRenderTarget.depthTexture;
        vanillaDepthTextureView = mainRenderTarget.depthTextureView;

        mainRenderTarget.colorTexture = renderTarget.colorTexture;
        mainRenderTarget.colorTextureView = renderTarget.colorTextureView;
        mainRenderTarget.depthTexture = renderTarget.depthTexture;
        mainRenderTarget.depthTextureView = renderTarget.depthTextureView;
    }

    public void releaseRenderTarget() {
        RenderTarget mainRenderTarget = minecraft.gameRenderer.mainRenderTarget();
        mainRenderTarget.colorTexture = vanillaColorTexture;
        mainRenderTarget.colorTextureView = vanillaColorTextureView;
        mainRenderTarget.depthTexture = vanillaDepthTexture;
        mainRenderTarget.depthTextureView = vanillaDepthTextureView;
    }
}
