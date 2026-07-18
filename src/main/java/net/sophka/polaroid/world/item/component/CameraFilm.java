package net.sophka.polaroid.world.item.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.sophka.polaroid.world.item.CameraItem;
import net.sophka.polaroid.world.item.FilmItem;
import org.apache.commons.lang3.math.Fraction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.sophka.polaroid.world.item.CameraItem.MAX_SLIDES;

public class CameraFilm {
    public static final CameraFilm EMPTY = new CameraFilm(null);

    @Nullable
    private final ItemStackTemplate film;

    public static final Codec<CameraFilm> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ItemStackTemplate.CODEC.fieldOf("film").forGetter(CameraFilm::getFilm)).apply(instance, CameraFilm::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CameraFilm> STREAM_CODEC = StreamCodec.composite(ItemStackTemplate.STREAM_CODEC, CameraFilm::getFilm,CameraFilm::new);
    public CameraFilm(@Nullable ItemStackTemplate film){
        this.film = film;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CameraFilm that)) return false;
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

        public Mutable(CameraFilm cameraFilm) {
            this.film = cameraFilm.getFilmStack();
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

        public CameraFilm toImmutable(){
            return new CameraFilm(this.film == null || this.film.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(this.film));
        }
    }
}
