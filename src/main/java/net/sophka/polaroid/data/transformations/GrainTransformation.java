package net.sophka.polaroid.data.transformations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.data.*;
import net.sophka.polaroid.init.ModFilmTransformations;
import net.sophka.polaroid.utils.Utils;

import java.util.Random;

public record GrainTransformation(int size, double strength) implements PointTransformation {
    public static final MapCodec<GrainTransformation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.INT.optionalFieldOf("size",2).forGetter(GrainTransformation::size),
                            Codec.DOUBLE.optionalFieldOf("strength",0.03125).forGetter(GrainTransformation::strength)
                    ).apply(instance, GrainTransformation::new));

    @Override
    public FilmTransformationType<?> type() {
        return ModFilmTransformations.GRAIN.get();
    }

    private static final Random random = new Random();
    @Override
    public Vec3 transform (Vec3 old){
        return new Vec3(
                Utils.clampUnit(old.x() + random.nextGaussian() * strength),
                Utils.clampUnit(old.y() + random.nextGaussian() * strength),
                Utils.clampUnit(old.z() + random.nextGaussian() * strength));
    }
}
