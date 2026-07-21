package net.sophka.polaroid.client.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.world.item.FilmType;
import org.jspecify.annotations.Nullable;

public record FilmTypeProperty() implements SelectItemModelProperty<FilmType> {
    public static final SelectItemModelProperty.Type<FilmTypeProperty, FilmType> TYPE = SelectItemModelProperty.Type.create(
            MapCodec.unit(new FilmTypeProperty()),
            FilmType.CODEC
    );

    @Nullable
    @Override
    public FilmType get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return stack.getOrDefault(ModDataComponents.FILM_TYPE, FilmType.MISSING);
    }

    @Override
    public Codec<FilmType> valueCodec() {
        return FilmType.CODEC;
    }

    @Override
    public SelectItemModelProperty.Type<FilmTypeProperty, FilmType> type() {
        return TYPE;
    }
}
