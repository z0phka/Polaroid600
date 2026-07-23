package net.sophka.polaroid.client.renderer;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.ClientState;
import net.sophka.polaroid.data.film.*;
import net.sophka.polaroid.data.film.transformations.ExposureAdjustmentTransformation;
import net.sophka.polaroid.data.film.transformations.LinearColorTransformation;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.network.PhotoDataPayload;
import net.sophka.polaroid.utils.Utils;
import net.sophka.polaroid.world.entity.CameraViewEntity;
import net.sophka.polaroid.world.item.CameraItem;
import net.sophka.polaroid.world.item.FilmFormat;
import net.sophka.polaroid.world.item.FilmItem;
import net.sophka.polaroid.world.item.component.FilmContent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ClientPhotoTaker {
    private record ScheduledPhoto(ItemStack cameraStack, FilmItem filmItem, int exposureAdjustment,
                                  @Nullable CameraViewEntity cameraViewEntity, int token) {
    }

    public enum State {
        IDLE,
        TAKING_PHOTO
    }

    private final Minecraft minecraft;
    private static ClientPhotoTaker instance;

    private float fov;
    private State state = State.IDLE;
    private boolean autofocus;
    private ConcurrentLinkedQueue<ScheduledPhoto> scheduledPhotos = new ConcurrentLinkedQueue<>();

    public static final Identifier dofEffect = Identifier.fromNamespaceAndPath(Polaroid600.MODID, "dof");
    public static final Identifier dofAutofocusEffect = Identifier.fromNamespaceAndPath(Polaroid600.MODID, "dof_af");

    private RenderTarget cameraRenderTarget;
    private GpuTexture vanillaColorTexture;
    private GpuTextureView vanillaColorTextureView;
    private GpuTexture vanillaDepthTexture;
    private GpuTextureView vanillaDepthTextureView;


    private ClientPhotoTaker(Minecraft minecraft) {
        this.minecraft = minecraft;
        instance = this;
    }

    public static ClientPhotoTaker instance() {
        return instance == null ? new ClientPhotoTaker(Minecraft.getInstance()) : instance;
    }

    private void schedulePhoto(ScheduledPhoto scheduledPhoto) {
        this.scheduledPhotos.add(scheduledPhoto);
    }

    public void takePhoto(ItemStack cameraStack, @Nullable CameraViewEntity view, int token) {
        if (!(cameraStack.getItem() instanceof CameraItem cameraItem)) {
            return;
        }
        FilmContent filmContent = CameraItem.filmContent(cameraStack);
        if (filmContent == null) {
            return;
        }
        ItemStack filmStack = filmContent.getFilmStack();
        if (!(filmStack.getItem() instanceof FilmItem filmItem)) {
            return;
        }
        int exposureAdjustment = cameraStack.getOrDefault(ModDataComponents.EXPOSURE, 0);
        schedulePhoto(new ScheduledPhoto(cameraStack, filmItem, exposureAdjustment, view, token));
    }

    public void process() {
        ScheduledPhoto scheduledPhoto = scheduledPhotos.poll();
        if (scheduledPhoto == null) {
            return;
        }
        processScheduledPhoto(scheduledPhoto);
    }

    private void processRender(RenderTarget renderTarget, ScheduledPhoto scheduledPhoto) {
        Polaroid600.LOGGER.debug("processRender");
        Screenshot.takeScreenshot(renderTarget,
                screenshot -> {
                    Polaroid600.LOGGER.debug("takeScreenshot");
                    Util.ioPool().execute(() -> {
                        Polaroid600.LOGGER.debug("ioPool");
                        Polaroid600.LOGGER.debug("queueFencedTask");
                        CameraItem cameraItem = (CameraItem) scheduledPhoto.cameraStack().getItem();
                        FilmItem filmItem = scheduledPhoto.filmItem();
                        int exposureAdjustment = scheduledPhoto.exposureAdjustment();

                        FilmFormat format = cameraItem.cameraProperties.getFilmFormat();

                        try (NativeImage scaled = new NativeImage(format.width, format.height, false)) {
                            //screenshot.resizeSubRectTo(x, y, width, height, scaled);
                            cropAndResize(screenshot, scaled);
                            TransformableImage transformableImage = new TransformableImage(scaled);
                            Optional<FilmData> filmData = FilmDataManager.INSTANCE.get(BuiltInRegistries.ITEM.getKey(filmItem));
                            if (filmData.isPresent()) {
                                List<FilmTransformation> transformations = filmData.get().transformations();
                                Transformation transformation =
                                        ExposureAdjustmentTransformation.fromCameraAdjustment(exposureAdjustment)
                                                .then(transformations.stream()
                                                        .map(ft -> (Transformation) ft)
                                                        .reduce(Transformation::then)
                                                        .orElse(new LinearColorTransformation(1, 1, 1)));
                                transformation.apply(transformableImage);
                                transformableImage.updateImage();
                                ClientPacketDistributor.sendToServer(new PhotoDataPayload(Utils.compressInts(scaled.getPixels()), cameraItem.cameraProperties.getFilmFormat(), "", scheduledPhoto.token()));
                            }
                        } catch (IOException e) {
                            Polaroid600.LOGGER.warn("Couldn't take photo", e);
                        } finally {
                            screenshot.close();
                        }
                    });
                });
    }

    private NativeImage cropAndResize(NativeImage src, NativeImage dst) {
        double targetAspectRatio = dst.getWidth() / (double) dst.getHeight();
        double srcAspectAspectRatio = src.getWidth() / (double) src.getHeight();

        int width = 0;
        int height = 0;
        int x = 0;
        int y = 0;

        if (targetAspectRatio > srcAspectAspectRatio) {
            width = src.getWidth();
            height = (int)Math.round(src.getWidth() / targetAspectRatio);
            y = (src.getHeight() - height) / 2;
        } else {
            height = src.getHeight();
            width = (int)Math.round(src.getHeight() * targetAspectRatio);
            x = (src.getWidth() - width) / 2;
        }
        src.resizeSubRectTo(x, y, width, height, dst);
        return dst;
    }

    //TODO: Fix LevelToTargetRenderer and switch to using it
    private void processScheduledPhoto(ScheduledPhoto scheduledPhoto) {
        if (this.minecraft.levelExtractor.countRenderedSections() > 10 && this.minecraft.levelRenderer.hasRenderedAllSections()) {
            ItemStack cameraStack = scheduledPhoto.cameraStack;
            CameraItem cameraItem = (CameraItem)cameraStack.getItem();

            this.fov = cameraItem.getFov() * (ClientState.selfieMode ? 2 : 1);
            this.autofocus = cameraItem.cameraProperties.hasAF() && cameraStack.getOrDefault(ModDataComponents.AF,false);
            boolean selfie = ClientState.selfieMode;
            boolean guiHidden = this.minecraft.gui.hud.isHidden();
            Entity cameraEntity = minecraft.getCameraEntity();
            CameraType cameraType = minecraft.options.getCameraType();
            Camera camera = minecraft.gameRenderer.mainCamera();
            Vec3 oldCameraPos = camera.position();
            float oldRotX = camera.xRot();
            float oldRotY = camera.yRot();
            float oldRoll = camera.getRoll();

            CameraViewEntity cameraViewEntity = null;
            if (scheduledPhoto.cameraViewEntity != null) {
                cameraViewEntity = scheduledPhoto.cameraViewEntity;
            } else if (selfie) {
                cameraViewEntity = ClientState.selfieViewEntity();
            }

            if (cameraViewEntity != null) {
                camera.setEntity(cameraViewEntity);
                camera.eyeHeight = cameraViewEntity.getEyeHeight();
                camera.eyeHeightOld = cameraViewEntity.getEyeHeight();
                camera.setPosition(cameraViewEntity.getX(), cameraViewEntity.getY(), cameraViewEntity.getZ());
            }

            if (!this.minecraft.gui.hud.isHidden()) {
                this.minecraft.gui.hud.toggle();
            }
            minecraft.options.setCameraType(CameraType.FIRST_PERSON);
            hijackRenderTarget();
            state = State.TAKING_PHOTO;
            minecraft.gameRenderer.update(DeltaTracker.ONE);
            minecraft.gameRenderer.extract(DeltaTracker.ONE, true);
            minecraft.gameRenderer.render(DeltaTracker.ONE, true);
            state = State.IDLE;
            minecraft.options.setCameraType(cameraType);
            if (this.minecraft.gui.hud.isHidden() != guiHidden) {
                this.minecraft.gui.hud.toggle();
            }


            if (cameraViewEntity != null) {
                camera.setEntity(cameraEntity);
                camera.eyeHeight = cameraEntity.getEyeHeight();
                camera.eyeHeightOld = cameraEntity.getEyeHeight();
                camera.setPosition(oldCameraPos.x, oldCameraPos.y, oldCameraPos.z);
                camera.setRotation(oldRotY, oldRotX, oldRoll);
            }

            processRender(cameraRenderTarget, scheduledPhoto);
        }
    }

    public void hijackRenderTarget() {
        RenderTarget mainRenderTarget = minecraft.gameRenderer.mainRenderTarget();
        cameraRenderTarget = new MainTarget(mainRenderTarget.width, mainRenderTarget.height);
        vanillaColorTexture = mainRenderTarget.colorTexture;
        vanillaColorTextureView = mainRenderTarget.colorTextureView;
        vanillaDepthTexture = mainRenderTarget.depthTexture;
        vanillaDepthTextureView = mainRenderTarget.depthTextureView;

        mainRenderTarget.colorTexture = cameraRenderTarget.colorTexture;
        mainRenderTarget.colorTextureView = cameraRenderTarget.colorTextureView;
        mainRenderTarget.depthTexture = cameraRenderTarget.depthTexture;
        mainRenderTarget.depthTextureView = cameraRenderTarget.depthTextureView;
    }

    public void releaseRenderTarget() {
        RenderTarget mainRenderTarget = minecraft.gameRenderer.mainRenderTarget();
        mainRenderTarget.colorTexture = vanillaColorTexture;
        mainRenderTarget.colorTextureView = vanillaColorTextureView;
        mainRenderTarget.depthTexture = vanillaDepthTexture;
        mainRenderTarget.depthTextureView = vanillaDepthTextureView;
    }

    public State getState() {
        return this.state;
    }

    public float getFov() {
        return this.fov;
    }

    public boolean getAutofocus(){
        return this.autofocus;
    }
}