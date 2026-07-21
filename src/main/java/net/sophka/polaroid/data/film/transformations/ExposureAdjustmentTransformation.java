package net.sophka.polaroid.data.film.transformations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.data.film.FilmTransformationType;
import net.sophka.polaroid.data.film.PointTransformation;
import net.sophka.polaroid.init.ModFilmTransformations;
import net.sophka.polaroid.world.item.CameraItem;

public record ExposureAdjustmentTransformation(float delta) implements PointTransformation {
    public static final MapCodec<ExposureAdjustmentTransformation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.FLOAT.optionalFieldOf("delta",0f).forGetter(ExposureAdjustmentTransformation::delta)
                    ).apply(instance, ExposureAdjustmentTransformation::new));

    @Override
    public FilmTransformationType<?> type() {
        return ModFilmTransformations.EXPOSURE_ADJUSTMENT.get();
    }

    @Override
    public Vec3 transform (Vec3 old){
        return new Vec3(Math.clamp((1 + delta()) * old.x(),0,1), Math.clamp((1 + delta()) * old.y(),0,1), Math.clamp((1 + delta()) * old.z(),0,1));
    }

    public static ExposureAdjustmentTransformation fromCameraAdjustment(int cameraAdjustment){
        return new ExposureAdjustmentTransformation(0.5f * ((float) cameraAdjustment/CameraItem.EXPOSURE_DELTA));
    }
}