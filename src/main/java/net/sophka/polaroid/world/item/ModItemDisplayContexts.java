package net.sophka.polaroid.world.item;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;

public class ModItemDisplayContexts {
    public static ItemDisplayContext selfieModeThirdPerson(){
        return ItemDisplayContext.valueOf("POLAROID600_SELFIE_MODE_THIRD_PERSON");
    }
    public static ItemDisplayContext selfieModeFirstPersonBoth(){
        return ItemDisplayContext.valueOf("POLAROID600_SELFIE_MODE_FIRST_PERSON_BOTH");
    }
    public static ItemDisplayContext selfieModeFirstPersonLeft(){
        return ItemDisplayContext.valueOf("POLAROID600_SELFIE_MODE_FIRST_PERSON_LEFT");
    }
    public static ItemDisplayContext selfieModeFirstPersonRight(){
        return ItemDisplayContext.valueOf("POLAROID600_SELFIE_MODE_FIRST_PERSON_RIGHT");
    }

    public static ItemDisplayContext selfieModeFirstPerson(HumanoidArm arm){
        if(arm == null){
            return selfieModeFirstPersonBoth();
        }
        return arm == HumanoidArm.LEFT ? selfieModeFirstPersonLeft() : selfieModeFirstPersonRight();
    }
}
