package net.sophka.polaroid.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import net.sophka.polaroid.Polaroid600;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

public record FilmCartridgeSpriteSource(Identifier film, Identifier output) implements SpriteSource {

    private static final Identifier overlay = Identifier.fromNamespaceAndPath(Polaroid600.MODID,"item/film_cartridge_overlay");

    public static final MapCodec<FilmCartridgeSpriteSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("film").forGetter(FilmCartridgeSpriteSource::film),
                    Identifier.CODEC.fieldOf("output").forGetter(FilmCartridgeSpriteSource::output)
            ).apply(instance, FilmCartridgeSpriteSource::new));

    @Override
    public void run(ResourceManager resourceManager, Output out) {
        Identifier filmPath = TEXTURE_ID_CONVERTER.idToFile(film);
        Identifier overlayPath = TEXTURE_ID_CONVERTER.idToFile(overlay);

        Optional<Resource> filmTexture = resourceManager.getResource(filmPath);
        Optional<Resource> overlayTexture = resourceManager.getResource(overlayPath);

        if(filmTexture.isEmpty() || overlayTexture.isEmpty()) return;

        LazyLoadedImage lazyFilmTexture = new LazyLoadedImage(film, filmTexture.get(), 1);
        LazyLoadedImage lazyOverlayTexture = new LazyLoadedImage(overlay, overlayTexture.get(), 1);

        out.add(this.output(), new FilmCartridgeSpriteSupplier(lazyFilmTexture, lazyOverlayTexture, output));
    }

    @Override
    public MapCodec<? extends SpriteSource> codec() {
        return CODEC;
    }

    public record FilmCartridgeSpriteSupplier(LazyLoadedImage film, LazyLoadedImage overlay, Identifier output) implements SpriteSource.DiscardableLoader{

        @Override
        public @Nullable SpriteContents get(SpriteResourceLoader loader) {
            try {
                NativeImage film = this.film.get();
                NativeImage overlay = this.overlay.get();

                int width = film.getWidth();
                int height = film.getHeight();

                NativeImage resultImage = new NativeImage(width, height, false);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int overlayColor = overlay.getPixel(x, y);
                        int alpha = ARGB.alpha(overlayColor);
                        int outputColor = alpha == 0 ? film.getPixel(x,y) : overlayColor;
                        resultImage.setPixel(x, y, outputColor);
                    }
                }
                return new SpriteContents(output, new FrameSize(width, height), resultImage);
            }
            catch (IOException e){
                Polaroid600.LOGGER.error("Could not create a cartridge texture ", e);
            }finally {
                this.film.release();
                this.overlay.release();
            }
            return null;
        }
    }
}
