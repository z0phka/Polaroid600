package net.sophka.polaroid.data.film.transformations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.data.film.FilmTransformationType;
import net.sophka.polaroid.data.film.Image;
import net.sophka.polaroid.data.film.SpatialTransformation;
import net.sophka.polaroid.data.film.TransformableImage;
import net.sophka.polaroid.init.ModFilmTransformations;
import net.sophka.polaroid.utils.Utils;

public record VignetteTransformation(double strength, double threshold) implements SpatialTransformation {
    public static final MapCodec<VignetteTransformation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.DOUBLE.optionalFieldOf("strength",0.5).forGetter(VignetteTransformation::strength),
                            Codec.DOUBLE.optionalFieldOf("threshold",0.75).forGetter(VignetteTransformation::threshold)
                    ).apply(instance, VignetteTransformation::new));

    @Override
    public FilmTransformationType<?> type() {
        return ModFilmTransformations.VIGNETTE.get();
    }

    @Override
    public void transform(Image oldImage, TransformableImage image) {
        for(int y = 0; y < image.height(); y++){
            for(int x = 0; x < image.width(); x++){
                Vec3 old = oldImage.getPixel(x,y);
                double distanceSq = Mth.square(y - image.height()/2) + Mth.square(x - image.width()/2);
                double dim = Math.min(image.height(), image.width())/2d;
                double threshold = Mth.square(dim * threshold());

                double v = strength * (Math.max(0, distanceSq - threshold) / threshold);

                image.setPixel(x,y,old.scale(Utils.clampUnit(1 - v)));
            }
        }
    }
}
