package net.sophka.polaroid.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.sophka.polaroid.world.item.FilmItem;
import org.apache.commons.lang3.math.Fraction;

import javax.annotation.Nullable;
import java.util.Objects;

import static net.sophka.polaroid.world.item.CameraItem.MAX_SLIDES;

public class FilmContent {
    public static final FilmContent EMPTY = new FilmContent(null);

    @Nullable
    private final ItemStackTemplate film;

    public static final Codec<FilmContent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ItemStackTemplate.CODEC.fieldOf("film").forGetter(FilmContent::getFilm)).apply(instance, FilmContent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FilmContent> STREAM_CODEC = StreamCodec.composite(ItemStackTemplate.STREAM_CODEC, FilmContent::getFilm, FilmContent::new);
    public FilmContent(@Nullable ItemStackTemplate film){
        this.film = film;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FilmContent that)) return false;
        return Objects.equals(film, that.film);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(film);
    }

    public ItemStackTemplate getFilm(){
        return this.film;
    }

    public ItemStack getFilmStack(){
        return count() <= 0 ? ItemStack.EMPTY : this.film.create();
    }

    public int count() {
        return this.film == null ? 0 : this.film.count();
    }

    public Fraction fraction(){
        return Fraction.getFraction(count(), MAX_SLIDES);
    }

    public static class Mutable {
        private ItemStack film;

        public Mutable(FilmContent filmContent) {
            this.film = filmContent.getFilmStack();
        }

        public Mutable clear() {
            this.film = ItemStack.EMPTY;
            return this;
        }

        public ItemStack getFilm(){
            return this.film;
        }

        public int tryInsert(ItemStack stack){
            if(stack.getItem() instanceof FilmItem && (film.isEmpty() || ItemStack.isSameItemSameComponents(stack, film))){
                if(film.isEmpty()){
                    this.film = stack.copy();
                    this.film.setCount(0);
                }
                int countToInsert = Math.min(MAX_SLIDES - film.count(), stack.count());
                film.grow(countToInsert);
                stack.shrink(countToInsert);
                return countToInsert;
            }
            return 0;
        }

        public FilmContent toImmutable(){
            return new FilmContent(this.film == null || this.film.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(this.film));
        }
    }
}
