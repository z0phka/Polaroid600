package net.sophka.polaroid.data.transformations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.data.*;
import net.sophka.polaroid.init.ModFilmTransformations;

import java.util.Random;

public record AberrationTransformation(int xScaleRed, int yScaleRed, int xScaleGreen, int yScaleGreen, int xScaleBlue, int yScaleBlue, float red, float green, float blue) implements SpatialTransformation {
    public static final MapCodec<AberrationTransformation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.INT.optionalFieldOf("xScaleRed",0).forGetter(AberrationTransformation::xScaleRed),
                            Codec.INT.optionalFieldOf("yScaleRed",0).forGetter(AberrationTransformation::yScaleRed),
                            Codec.INT.optionalFieldOf("xScaleGreen",0).forGetter(AberrationTransformation::xScaleGreen),
                            Codec.INT.optionalFieldOf("yScaleGreen",0).forGetter(AberrationTransformation::yScaleGreen),
                            Codec.INT.optionalFieldOf("xScaleBlue",0).forGetter(AberrationTransformation::xScaleBlue),
                            Codec.INT.optionalFieldOf("yScaleBlue",0).forGetter(AberrationTransformation::yScaleBlue),
                            Codec.FLOAT.optionalFieldOf("red",0f).forGetter(AberrationTransformation::red),
                            Codec.FLOAT.optionalFieldOf("green",0f).forGetter(AberrationTransformation::green),
                            Codec.FLOAT.optionalFieldOf("blue",0f).forGetter(AberrationTransformation::blue)
                    ).apply(instance, AberrationTransformation::new));

    @Override
    public FilmTransformationType<?> type() {
        return ModFilmTransformations.ABERRATION.get();
    }

    @Override
    public void transform(Image oldImage, TransformableImage image) {
        int height = image.height();
        int width = image.width();
        Vec2 center = new Vec2(width/2f, height/2f);
        float maxDst = (float) (1f/(Math.sqrt(Math.pow(height/2f,2) + Math.pow(width/2f,2))));

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                Vec3 old = oldImage.getPixel(x,y);
                Vec2 fromCenter = center.add(new Vec2(-x,-y));
                Vec2 scaled = fromCenter.scale(maxDst);

                Vec3 offsetRed = oldImage.getPixel(Math.round(x + scaled.x * xScaleRed), Math.round(y + scaled.y * yScaleRed));
                Vec3 offsetGreen = oldImage.getPixel(Math.round(x + scaled.x * xScaleGreen), Math.round(y + scaled.y * yScaleGreen));
                Vec3 offsetBlue = oldImage.getPixel(Math.round(x + scaled.x * xScaleBlue), Math.round(y + scaled.y * yScaleBlue));

                old = old.multiply(1 - red, 1 - green, 1 - blue);
                offsetRed = offsetRed.multiply(red, 0, 0);
                offsetGreen = offsetGreen.multiply(0, green, 0);
                offsetBlue = offsetBlue.multiply(0, 0, blue);
                Vec3 result = old.add(offsetRed).add(offsetGreen).add(offsetBlue);
                image.setPixel(x,y,result);
            }
        }
    }
}
