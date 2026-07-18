package net.sophka.polaroid.data;

import com.mojang.serialization.Codec;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.loot.LootModifierManager;
import net.sophka.polaroid.Polaroid600;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FilmDataManager extends SimpleJsonResourceReloadListener<FilmData> {
    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(Polaroid600.MODID, "film");
    public static final FilmDataManager INSTANCE = new FilmDataManager();

    private Map<Identifier, FilmData> map = Collections.emptyMap();

    protected FilmDataManager() {
        super(FilmData.CODEC, FileToIdConverter.json( "film"));
    }

    @Override
    protected void apply(Map<Identifier, FilmData> preparations, ResourceManager manager, ProfilerFiller profiler) {
        map = preparations;
    }

    public Optional<FilmData> get(Identifier identifier){
        return Optional.ofNullable(map.getOrDefault(identifier, null));
    }
}
