package net.sophka.polaroid.data.transformations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.data.FilmTransformationType;
import net.sophka.polaroid.data.PointTransformation;
import net.sophka.polaroid.init.ModFilmTransformations;

public record LinearCombinationTransformation(float red, float green, float blue) implements PointTransformation {
    public static final MapCodec<LinearCombinationTransformation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.FLOAT.optionalFieldOf("red",1f).forGetter(LinearCombinationTransformation::red),
                            Codec.FLOAT.optionalFieldOf("green",1f).forGetter(LinearCombinationTransformation::green),
                            Codec.FLOAT.optionalFieldOf("blue",1f).forGetter(LinearCombinationTransformation::blue)
                    ).apply(instance, LinearCombinationTransformation::new));

    @Override
    public FilmTransformationType<?> type() {
        return ModFilmTransformations.LINEAR_COMBINATION_TRANSFORMATION.get();
    }

    @Override
    public Vec3 transform (Vec3 old){
        double value = Math.clamp(red() * old.x() + green() * old.y() + blue() * old.z(),0,1);
        return new Vec3(value, value, value);
    }
}
