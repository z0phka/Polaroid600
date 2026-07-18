package net.sophka.polaroid.data.transformations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.data.FilmTransformationType;
import net.sophka.polaroid.data.PointTransformation;
import net.sophka.polaroid.init.ModFilmTransformations;

public record QuantizationTransformation(int red, int green, int blue) implements PointTransformation {
    public static final MapCodec<QuantizationTransformation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.INT.optionalFieldOf("red",255).forGetter(QuantizationTransformation::red),
                            Codec.INT.optionalFieldOf("green",255).forGetter(QuantizationTransformation::green),
                            Codec.INT.optionalFieldOf("blue",255).forGetter(QuantizationTransformation::blue)
                    ).apply(instance, QuantizationTransformation::new));

    @Override
    public FilmTransformationType<?> type() {
        return ModFilmTransformations.QUANTIZATION.get();
    }

    @Override
    public Vec3 transform (Vec3 old){
        double r = Math.floor(old.x * red) / red;
        double g = Math.floor(old.y * green) / green;
        double b = Math.floor(old.z * blue) / blue;
        return new Vec3(Math.clamp(r,0,1), Math.clamp(g,0,1), Math.clamp(b,0,1));
    }
}
