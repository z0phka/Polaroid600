package net.sophka.polaroid.client.model;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.init.ModItems;
import net.sophka.polaroid.world.item.FilmType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, Polaroid600.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.itemModelOutput.accept(
                ModItems.PHOTO.get(),
                new SelectItemModel.Unbaked(
                        Optional.empty(),
                        new SelectItemModel.UnbakedSwitch<>(
                                new FilmTypeProperty(),
                                List.of(
                                        new SelectItemModel.SwitchCase<>(
                                                List.of(FilmType.COLOR),
                                                new CuboidItemModelWrapper.Unbaked(
                                                        Identifier.fromNamespaceAndPath(Polaroid600.MODID, "item/photo_600_color"),
                                                        Optional.empty(),
                                                        Collections.emptyList()
                                                )
                                        ),
                                        new SelectItemModel.SwitchCase<>(
                                                List.of(FilmType.BW),
                                                new CuboidItemModelWrapper.Unbaked(
                                                        Identifier.fromNamespaceAndPath(Polaroid600.MODID, "item/photo_600_bw"),
                                                        Optional.empty(),
                                                        Collections.emptyList()
                                                )
                                        ),
                                        new SelectItemModel.SwitchCase<>(
                                                List.of(FilmType.BLUE),
                                                new CuboidItemModelWrapper.Unbaked(
                                                        Identifier.fromNamespaceAndPath(Polaroid600.MODID, "item/photo_600_blue"),
                                                        Optional.empty(),
                                                        Collections.emptyList()
                                                )
                                        ),
                                        new SelectItemModel.SwitchCase<>(
                                                List.of(FilmType.GREEN),
                                                new CuboidItemModelWrapper.Unbaked(
                                                        Identifier.fromNamespaceAndPath(Polaroid600.MODID, "item/photo_600_green"),
                                                        Optional.empty(),
                                                        Collections.emptyList()
                                                )
                                        ),
                                        new SelectItemModel.SwitchCase<>(
                                                List.of(FilmType.PURPLE),
                                                new CuboidItemModelWrapper.Unbaked(
                                                        Identifier.fromNamespaceAndPath(Polaroid600.MODID, "item/photo_600_purple"),
                                                        Optional.empty(),
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                        ),
                        Optional.of(
                                new CuboidItemModelWrapper.Unbaked(
                                        Identifier.fromNamespaceAndPath(Polaroid600.MODID, "item/photo"),
                                        Optional.empty(),
                                        Collections.emptyList()
                                )
                        )
                )
        );
    }

    @Override
    protected java.util.stream.Stream<? extends net.minecraft.core.Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected java.util.stream.Stream<? extends net.minecraft.core.Holder<Item>> getKnownItems() {
        return Stream.of(ModItems.PHOTO);
    }
}
