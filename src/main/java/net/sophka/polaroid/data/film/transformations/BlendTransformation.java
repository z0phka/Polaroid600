package net.sophka.polaroid.data.film.transformations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.data.film.*;
import net.sophka.polaroid.init.ModFilmTransformations;

import java.util.List;
import java.util.stream.IntStream;

public record BlendTransformation(List<SubImage> subImages) implements CompositeTransformation {
    public static final MapCodec<BlendTransformation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            SubImage.CODEC.codec().listOf().fieldOf("transformations").forGetter(BlendTransformation::subImages)
                    ).apply(instance, BlendTransformation::new));

    private record SubImage(List<FilmTransformation> transformations, double weight){
        public static final MapCodec<SubImage> CODEC =
                RecordCodecBuilder.mapCodec(instance ->
                        instance.group(
                                FilmTransformation.dispatchCodec().listOf().fieldOf("transformations").forGetter(SubImage::transformations),
                                Codec.DOUBLE.fieldOf("weight").forGetter(SubImage::weight)
                        ).apply(instance, SubImage::new));
    }

    @Override
    public FilmTransformationType<?> type() {
        return ModFilmTransformations.BLEND.get();
    }


    @Override
    public List<Transformation> transformations() {
        return this.subImages().stream().map(subImage ->
                subImage.transformations.stream()
                        .map(ft -> (Transformation)ft)
                        .reduce(Transformation::then)
                        .orElse(new LinearColorTransformation(1,1,1))).toList();
    }

    @Override
    public TransformableImage composite (List<TransformableImage> subimages){
        TransformableImage result = subimages.getFirst();
        for(int y = 0; y < result.height(); y++){
            for(int x = 0; x < result.width(); x++){
                final int xx = x;
                final int yy = y;
                result.setPixel(x,y,
                        IntStream.range(0, subimages.size())
                                .mapToObj(i -> subimages.get(i)
                                        .getPixel(xx,yy)
                                        .scale(this.subImages().get(i).weight()))
                                .reduce(Vec3::add)
                                .orElse(Vec3.ZERO));
            }
        }

        return result;
    }
}
