package net.sophka.polaroid.data.darkslide;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record DarkslideSeries(String seriesName, List<Darkslide> darkslides) {

    public static final Codec<DarkslideSeries> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(DarkslideSeries::seriesName),
                    Darkslide.CODEC.listOf().fieldOf("darkslides").forGetter(DarkslideSeries::darkslides)
            ).apply(instance, DarkslideSeries::new));

    public static final StreamCodec<ByteBuf, DarkslideSeries> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            DarkslideSeries::seriesName,
            Darkslide.STREAM_CODEC.apply(ByteBufCodecs.list()),
            DarkslideSeries::darkslides,
            DarkslideSeries::new);


    public int indexOf(Darkslide darkslide){
        return darkslides.indexOf(darkslide);
    }

    public int size(){
        return darkslides.size();
    }
}
