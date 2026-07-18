package net.sophka.polaroid.data;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public interface Image {
    Vec3[] pixels();

    int width();

    int height();

    default Vec3 getPixel(int x, int y) {
        x = Mth.clamp(x,0, width() - 1);
        y = Mth.clamp(y,0, height() - 1);
        return pixels()[y * width() + x];
    }
}
