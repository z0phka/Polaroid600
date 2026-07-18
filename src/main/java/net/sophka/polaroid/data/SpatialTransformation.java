package net.sophka.polaroid.data;

import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public interface SpatialTransformation extends FilmTransformation{

    @Override
    default TransformableImage apply(TransformableImage image) {

        Vec3[] oldPixels = Arrays.stream(image.pixels()).map(vec3 -> new Vec3(vec3.x, vec3.y, vec3.z)).toArray(Vec3[]::new);
        Image oldImage = new Image() {
            @Override
            public Vec3[] pixels() {
                return oldPixels;
            }

            @Override
            public int width() {
                return image.width();
            }

            @Override
            public int height() {
                return image.height();
            }
        };

        transform(oldImage, image);
        return image;
    }

    void transform(Image oldImage, TransformableImage image);
}
