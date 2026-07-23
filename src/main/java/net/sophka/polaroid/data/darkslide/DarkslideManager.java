package net.sophka.polaroid.data.darkslide;


import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.sophka.polaroid.Polaroid600;

import java.util.*;
import java.util.stream.Collectors;

public class DarkslideManager extends SimpleJsonResourceReloadListener<DarkslideSeries> {
    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(Polaroid600.MODID, "darkslides");
    public static final DarkslideManager INSTANCE = new DarkslideManager();
    public static final DarkslideManager CLIENT_INSTANCE = new DarkslideManager();

    private Map<Identifier, DarkslideSeries> darkslideSeries = Collections.emptyMap();
    private Map<Identifier, Darkslide> darkslides = Collections.emptyMap();

    protected DarkslideManager() {
        super(DarkslideSeries.CODEC, FileToIdConverter.json( "darkslides"));
    }

    @Override
    protected void apply(Map<Identifier, DarkslideSeries> preparations, ResourceManager manager, ProfilerFiller profiler) {
        load(preparations);
    }

    public void load(Map<Identifier, DarkslideSeries> seriesMap){
        darkslideSeries = seriesMap;
        darkslides = darkslideSeries.entrySet().stream().flatMap(
                        entry -> entry.getValue().darkslides().stream().map(
                                darkslide -> {
                                    darkslide.setSeries(entry.getValue());
                                    return Pair.of(entry.getKey().withSuffix("/" + darkslide.identifier()), darkslide);
                                }))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    public Optional<Darkslide> get(Identifier identifier){
        return Optional.ofNullable(darkslides.get(identifier));
    }

    public Map<Identifier, DarkslideSeries> darkslideSeries(){
        return Collections.unmodifiableMap(darkslideSeries);
    }

    public List<Identifier> darkslideIdentifiers(){
        return darkslides.keySet().stream().toList();
    }
}
