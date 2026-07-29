package net.sophka.polaroid.client.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.world.item.FilmFormat;
import org.jspecify.annotations.Nullable;

public record FilmFormatProperty() implements SelectItemModelProperty<FilmFormat> {
    public static final Type<FilmFormatProperty, FilmFormat> TYPE = Type.create(
            MapCodec.unit(new FilmFormatProperty()),
            FilmFormat.CODEC
    );

    @Nullable
    @Override
    public FilmFormat get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return stack.getOrDefault(ModDataComponents.FILM_FORMAT, FilmFormat._600);
    }

    @Override
    public Codec<FilmFormat> valueCodec() {
        return FilmFormat.CODEC;
    }

    @Override
    public Type<FilmFormatProperty, FilmFormat> type() {
        return TYPE;
    }
}
