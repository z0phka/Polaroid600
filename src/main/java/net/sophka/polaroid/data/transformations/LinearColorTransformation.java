package net.sophka.polaroid.data.transformations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.data.FilmTransformationType;
import net.sophka.polaroid.data.PointTransformation;
import net.sophka.polaroid.init.ModFilmTransformations;

public record LinearColorTransformation(float red, float green, float blue) implements PointTransformation {
    public static final MapCodec<LinearColorTransformation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.FLOAT.optionalFieldOf("red",1f).forGetter(LinearColorTransformation::red),
                            Codec.FLOAT.optionalFieldOf("green",1f).forGetter(LinearColorTransformation::green),
                            Codec.FLOAT.optionalFieldOf("blue",1f).forGetter(LinearColorTransformation::blue)
                    ).apply(instance, LinearColorTransformation::new));

    @Override
    public FilmTransformationType<?> type() {
        return ModFilmTransformations.LINEAR_COLOR_TRANSFORMATION.get();
    }

    @Override
    public Vec3 transform (Vec3 old){
        return new Vec3(Math.clamp(red() * old.x(),0,1), Math.clamp(green() * old.y(),0,1), Math.clamp(blue() * old.z(),0,1));
    }
}
