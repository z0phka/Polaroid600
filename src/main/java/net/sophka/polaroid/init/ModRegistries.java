package net.sophka.polaroid.init;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.data.FilmTransformationType;

public class ModRegistries {
    public static final ResourceKey<Registry<FilmTransformationType<?>>> FILM_TRANSFORMATION_TYPE_KEY =
            ResourceKey.createRegistryKey(
                    Identifier.fromNamespaceAndPath(
                            Polaroid600.MODID,
                            "film_transformation_type"
                    )
            );
    public static final Registry<FilmTransformationType<?>> FILM_TRANSFORMATION_TYPE = new RegistryBuilder<>(FILM_TRANSFORMATION_TYPE_KEY).create();

}
