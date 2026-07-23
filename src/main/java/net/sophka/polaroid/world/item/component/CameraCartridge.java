package net.sophka.polaroid.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.world.item.CartridgeItem;
import org.apache.commons.lang3.math.Fraction;

import javax.annotation.Nullable;
import java.util.Objects;

public class CameraCartridge {
    public static final CameraCartridge EMPTY = new CameraCartridge(null);

    @Nullable
    private final ItemStackTemplate cartridge;

    public static final Codec<CameraCartridge> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ItemStackTemplate.CODEC.fieldOf("cartridge").forGetter(CameraCartridge::getCartridge)).apply(instance, CameraCartridge::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CameraCartridge> STREAM_CODEC = StreamCodec.composite(ItemStackTemplate.STREAM_CODEC, CameraCartridge::getCartridge, CameraCartridge::new);
    public CameraCartridge(@Nullable ItemStackTemplate cartridge){
        this.cartridge = cartridge;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CameraCartridge that)) return false;
        return Objects.equals(cartridge, that.cartridge);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cartridge);
    }

    public ItemStackTemplate getCartridge(){
        return this.cartridge;
    }

    public ItemStack getCartridgeStack(){
        return count() <= 0 ? ItemStack.EMPTY : this.cartridge.create();
    }

    public int count() {
        return this.cartridge == null ? 0 : this.cartridge.count();
    }

    public int filmCount(){
        return filmContent().count();
    }

    public Fraction filmFraction(){
        return filmContent().fraction();
    }

    public FilmContent filmContent(){
        return this.cartridge == null ? FilmContent.EMPTY : cartridge.getOrDefault(ModDataComponents.FILM_CONTENT,FilmContent.EMPTY);
    }


    public static class Mutable {
        private ItemStack cartridgeStack;

        public Mutable(CameraCartridge cameraCartridge) {
            this.cartridgeStack = cameraCartridge.getCartridgeStack();
        }

        public Mutable clear() {
            this.cartridgeStack = ItemStack.EMPTY;
            return this;
        }

        public ItemStack getCartridgeStack(){
            return this.cartridgeStack;
        }

        public int tryInsert(ItemStack stack){
            if(stack.getItem() instanceof CartridgeItem && stack.has(ModDataComponents.FILM_CONTENT)){
                if(cartridgeStack.isEmpty()){
                    this.cartridgeStack = stack.copy();
                    this.cartridgeStack.setCount(0);
                }
                cartridgeStack.grow(1);
                stack.shrink(1);
                return 1;
            }
            return 0;
        }

        public int filmCount(){
            return cartridgeStack.getOrDefault(ModDataComponents.FILM_CONTENT,FilmContent.EMPTY).count();
        }

        public Fraction filmFraction(){
            return cartridgeStack.getOrDefault(ModDataComponents.FILM_CONTENT,FilmContent.EMPTY).fraction();
        }

        public CameraCartridge toImmutable(){
            return new CameraCartridge(this.cartridgeStack == null || this.cartridgeStack.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(this.cartridgeStack));
        }
    }
}
