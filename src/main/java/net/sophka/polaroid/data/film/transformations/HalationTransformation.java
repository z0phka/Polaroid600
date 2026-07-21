package net.sophka.polaroid.data.film.transformations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.data.film.ConvolutionTransformation;
import net.sophka.polaroid.data.film.FilmTransformationType;
import net.sophka.polaroid.data.film.Image;
import net.sophka.polaroid.data.film.TransformableImage;
import net.sophka.polaroid.init.ModFilmTransformations;
import net.sophka.polaroid.utils.Utils;

public record HalationTransformation(double red, double green, double blue) implements ConvolutionTransformation {
    public static final MapCodec<HalationTransformation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.DOUBLE.optionalFieldOf("red",0.5).forGetter(HalationTransformation::red),
                            Codec.DOUBLE.optionalFieldOf("green",0.22).forGetter(HalationTransformation::green),
                            Codec.DOUBLE.optionalFieldOf("blue",0.08).forGetter(HalationTransformation::blue)
                    ).apply(instance, HalationTransformation::new));

    @Override
    public FilmTransformationType<?> type() {
        return ModFilmTransformations.HALATION.get();
    }

    private static double[][] createGaussianKernel(int n) {
        double sigma = (n - 1) / 6.0;
        return createGaussianKernel(n, sigma);
    }

    private static double[][] createGaussianKernel(int n, double sigma) {
        double[][] kernel = new double[n][n];
        double sum = 0;
        int axis = (n - 1)/2;

        for(int x = -axis; x <= axis; x++){
            for(int y = -axis; y <= axis; y++){
                double value = gaussianCoefficient(x,y,sigma,0);
                kernel[x + axis][y+axis] = value;
                sum += value;
            }
        }

        for(int x = -axis; x <= axis; x++){
            for(int y = -axis; y <= axis; y++){
                kernel[x + axis][y+axis] = kernel[x + axis][y+axis]/sum;
            }
        }

        return kernel;
    }

    private static double gaussianCoefficient(int x, int y, double sigma, double mu){
        return 1/(Mth.TWO_PI * sigma * sigma) * Math.exp(-Math.pow(Math.sqrt(x * x + y * y) - mu,2)/(2 * sigma * sigma));
    }

    @Override
    public double[][] kernel() {
        return createGaussianKernel(7);
    }


    @Override
    public void transform(Image oldImage, TransformableImage image){
        for(int y = 0; y < image.height(); y++){
            for(int x = 0; x < image.width(); x++){
                Vec3 color = oldImage.getPixel(x,y).add(image.getPixel(x,y).multiply(red(), green(), blue()));
                image.setPixel(x,y,new Vec3(Utils.clampUnit(color.x()), Utils.clampUnit(color.y()), Utils.clampUnit(color.z())));
            }
        }
    }
}
