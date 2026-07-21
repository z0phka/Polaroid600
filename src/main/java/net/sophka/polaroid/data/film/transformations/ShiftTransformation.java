package net.sophka.polaroid.data.film.transformations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.data.film.FilmTransformationType;
import net.sophka.polaroid.data.film.Image;
import net.sophka.polaroid.data.film.SpatialTransformation;
import net.sophka.polaroid.data.film.TransformableImage;
import net.sophka.polaroid.init.ModFilmTransformations;

public record ShiftTransformation(int size, double strength) implements SpatialTransformation {
    public static final MapCodec<ShiftTransformation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.INT.optionalFieldOf("size",2).forGetter(ShiftTransformation::size),
                            Codec.DOUBLE.optionalFieldOf("strength",0.5).forGetter(ShiftTransformation::strength)
                    ).apply(instance, ShiftTransformation::new));

    @Override
    public FilmTransformationType<?> type() {
        return ModFilmTransformations.SHIFT.get();
    }

    @Override
    public void transform(Image oldImage, TransformableImage image) {
        for(int y = 0; y < image.height(); y++){
            for(int x = 0; x < image.width(); x++){
                Vec3 result = oldImage.getPixel(x,y);
                Vec3 added = Vec3.ZERO;

                for(int i = -size; i <= size; i++){
                    for(int j = -size; j <= size; j++){
                        if(i == 0 && j == 0){
                            continue;
                        }
                        added = result.add(oldImage.getPixel(x + i,y + j));
                    }
                }
                result = result.scale(1 - strength).add(added.normalize().scale(strength));
                image.setPixel(x,y,result);
            }
        }
    }
}
