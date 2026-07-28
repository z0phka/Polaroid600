package net.sophka.polaroid.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.world.block.entity.PhotoBlockEntity;

import java.util.function.Supplier;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Polaroid600.MODID);
    public static final Supplier<BlockEntityType<PhotoBlockEntity>> PHOTO_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "photo",
            () -> new BlockEntityType<>(
                    PhotoBlockEntity::new,
                    false,
                    ModBlocks.PHOTO.get()
            ));
}
