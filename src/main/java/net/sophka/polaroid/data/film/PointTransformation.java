package net.sophka.polaroid.data.film;

import net.minecraft.world.phys.Vec3;

public interface PointTransformation extends FilmTransformation{

    @Override
    default TransformableImage apply(TransformableImage image) {
        for(int y = 0; y < image.height(); y++){
            for(int x = 0; x < image.width(); x++){
                image.setPixel(x,y,transform(image.getPixel(x,y)));
            }
        }
        return image;
    }

    default Vec3 transform (Vec3 old){
        return old;
    }
}
