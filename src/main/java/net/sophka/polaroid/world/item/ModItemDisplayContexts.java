package net.sophka.polaroid.world.item;

import net.minecraft.world.item.ItemDisplayContext;

public class ModItemDisplayContexts {
    public static ItemDisplayContext selfieModeThirdPerson(){
        return ItemDisplayContext.valueOf("POLAROID600_SELFIE_MODE_THIRD_PERSON");
    }
    public static ItemDisplayContext selfieModeFirstPerson(){
        return ItemDisplayContext.valueOf("POLAROID600_SELFIE_MODE_FIRST_PERSON");
    }
}
