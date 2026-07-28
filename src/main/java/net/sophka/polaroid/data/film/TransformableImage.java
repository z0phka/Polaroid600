package net.sophka.polaroid.data.film;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class TransformableImage implements Image{

    private final NativeImage nativeImage;
    private final Vec3[] pixels;
    private final int width;
    private final int height;

    public TransformableImage(NativeImage image){
        this.nativeImage = image;
        this.pixels = Arrays.stream(image.getPixelsABGR()).mapToObj(TransformableImage::intToVec).toArray(Vec3[]::new);
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public Vec3[] pixels(){
        return this.pixels;
    }

    public int width(){
        return this.width;
    }

    public int height(){
        return this.height;
    }

    public void setPixel(int x, int y, Vec3 vec3){
        this.pixels[y * width() + x] = vec3;
    }

    private static Vec3 intToVec(int color){
        color = ARGB.fromABGR(color);
        double r = ARGB.redFloat(color);
        double g = ARGB.greenFloat(color);
        double b = ARGB.blueFloat(color);

        return new Vec3(r,g,b);
    }

    private static int vecToInt(Vec3 vec){
        return ARGB.toABGR(ARGB.color(vec));
    }

    public NativeImage nativeImage(){
        return this.nativeImage;
    }

    public void updateImage(){
        for(int y = 0; y < height(); y++){
            for(int x = 0; x < width(); x++){
                this.nativeImage.setPixelABGR(x,y,vecToInt(getPixel(x,y)));
            }
        }
    }
}
