package net.sophka.polaroid.client.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.world.item.FilmItem;
import net.sophka.polaroid.world.item.FilmType;
import net.sophka.polaroid.world.item.component.FilmContent;
import org.jspecify.annotations.Nullable;

public record FilmContentProperty() implements SelectItemModelProperty<FilmType> {
    public static final Type<FilmContentProperty, FilmType> TYPE = Type.create(
            MapCodec.unit(new FilmContentProperty()),
            FilmType.CODEC
    );

    @Nullable
    @Override
    public FilmType get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        ItemStack filmStack = stack.getOrDefault(ModDataComponents.FILM_CONTENT, FilmContent.EMPTY).getFilmStack();
        if(filmStack.getItem() instanceof FilmItem filmItem){
            return filmItem.filmType;
        }
        return FilmType.MISSING;
    }

    @Override
    public Codec<FilmType> valueCodec() {
        return FilmType.CODEC;
    }

    @Override
    public Type<FilmContentProperty, FilmType> type() {
        return TYPE;
    }
}
