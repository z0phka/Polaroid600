package net.sophka.polaroid.world.item.component;

import com.google.common.base.Strings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Objects;
import java.util.Optional;

public class DoubleExposure {
    public static final DoubleExposure OFF = new DoubleExposure(State.OFF);

    public static final Codec<DoubleExposure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.STRING.optionalFieldOf("partialImage", null).forGetter(DoubleExposure::getPartialImage),
                            StringRepresentable.fromEnum(State::values).fieldOf("format").forGetter(DoubleExposure::getState))
                    .apply(instance, DoubleExposure::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DoubleExposure> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.STRING_UTF8,
                    DoubleExposure::getPartialImage,
                    NeoForgeStreamCodecs.enumCodec(State.class),
                    DoubleExposure::getState,
                    DoubleExposure::new);
    public enum State implements StringRepresentable {
        OFF("off"),
        ON("on"),
        PRIMED("primed");

        public final String name;

        State(String name){
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    private final State state;
    private final String partialImage;

    public DoubleExposure(String partialImage, State state){
        this.partialImage = partialImage;
        this.state = state;
    }

    public DoubleExposure(State state){
        this("", state);
    }

    public State getState() {
        return state;
    }

    public String getPartialImage() {
        return partialImage;
    }

    public boolean isOn(){
        return getState() != State.OFF;
    }

    public boolean isPrimed(){
        return getState() == State.PRIMED;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DoubleExposure that)) return false;
        return getState() == that.getState() && Objects.equals(getPartialImage(), that.getPartialImage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getState(), getPartialImage());
    }

    public static class Mutable{
        private State state;
        private String partialImage;

        public Mutable(DoubleExposure doubleExposure){
            this.state = doubleExposure.state;
            this.partialImage = doubleExposure.partialImage;
        }

        public void storePartialImage(String partialImage){
            if(this.state != State.ON){
                return;
            }
            this.state = State.PRIMED;
            this.partialImage = partialImage;
        }

        public void turnOn(){
            if(this.state != State.OFF){
                return;
            }
            this.state = State.ON;
        }

        public Optional<String> turnOff(){
            this.state = State.OFF;
            if(!Strings.isNullOrEmpty(this.partialImage)){
                String ret = this.partialImage;
                this.partialImage = "";
                return Optional.of(ret);
            }
            return Optional.empty();
        }

        public DoubleExposure toImmutable(){
            return new DoubleExposure(this.partialImage, this.state);
        }
    }
}
