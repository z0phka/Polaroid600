package net.sophka.polaroid.data.film;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public interface ConvolutionTransformation extends SpatialTransformation{
    double[][] kernel();

    default TransformableImage applyKernel(double[][] kernel, Image copy, TransformableImage image){

        int kernelHeight = kernel.length;
        int kernelWidth = kernel[0].length;
        for(int i = 0; i < copy.height(); i++){
            for(int j = 0; j < copy.width(); j++){
                Vec3 color = Vec3.ZERO;
                for(int x = 0; x < kernelHeight; x++){
                    for (int y = 0; y < kernelWidth; y++){
                        double weight = kernel[x][y];
                        int yy = Mth.clamp(i - y + Mth.ceil(kernelWidth/2d),0, copy.height() - 1);
                        int xx = Mth.clamp(j - x + Mth.ceil(kernelHeight/2d),0, copy.width() - 1);

                        Vec3 vec = copy.getPixel(xx,yy);
                        color = color.add(vec.scale(weight));
                    }
                }
                image.setPixel(j,i,color);
            }
        }
        return image;
    }

    default int stride(){
        return 1;
    }

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
        TransformableImage filtered = applyKernel(kernel(),oldImage, image);
        transform(oldImage,filtered);
        return filtered;
    }
}
