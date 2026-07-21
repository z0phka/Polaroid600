package net.sophka.polaroid.data.film.transformations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.data.film.FilmTransformationType;
import net.sophka.polaroid.data.film.PointTransformation;
import net.sophka.polaroid.init.ModFilmTransformations;

public record PowerColorTransformation(float red, float green, float blue) implements PointTransformation {
    public static final MapCodec<PowerColorTransformation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.FLOAT.optionalFieldOf("red",1f).forGetter(PowerColorTransformation::red),
                            Codec.FLOAT.optionalFieldOf("green",1f).forGetter(PowerColorTransformation::green),
                            Codec.FLOAT.optionalFieldOf("blue",1f).forGetter(PowerColorTransformation::blue)
                    ).apply(instance, PowerColorTransformation::new));

    @Override
    public FilmTransformationType<?> type() {
        return ModFilmTransformations.POWER_COLOR_TRANSFORMATION.get();
    }

    @Override
    public Vec3 transform (Vec3 old){
        return new Vec3(Math.clamp(Math.pow(old.x(),red()),0,1), Math.clamp(Math.pow(old.y(),green()),0,1), Math.clamp(Math.pow(old.z(),blue()),0,1));
    }
}
