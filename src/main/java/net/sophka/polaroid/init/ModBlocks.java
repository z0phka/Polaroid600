package net.sophka.polaroid.init;

import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.world.block.PhotoBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Polaroid600.MODID);
    public static final DeferredBlock<PhotoBlock> PHOTO = BLOCKS.registerBlock("photo", p -> new PhotoBlock(p.noTerrainParticles().noLootTable().noCollision().sound(SoundType.CANDLE).instabreak()));
}
