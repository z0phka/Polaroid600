package net.sophka.polaroid.event;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.init.ModEntityTypes;
import net.sophka.polaroid.world.item.CameraItem;

@EventBusSubscriber(modid = Polaroid600.MODID)
public class ModEventHandler {
    @SubscribeEvent
    public static void createDefaultAttributes(EntityAttributeCreationEvent event) {
        event.put(
                ModEntityTypes.CAMERA_TRIPOD.get(),
                LivingEntity.createLivingAttributes()
                        .add(Attributes.STEP_HEIGHT, 0.0)
                        .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                        .build()
        );
    }

    @SubscribeEvent
    public static void onInteractEntity(PlayerInteractEvent.EntityInteract event){
        ItemStack stack = event.getEntity().getActiveItem();
        if(stack.getItem() instanceof CameraItem && !CameraItem.permitInteraction(stack, event.getHand(), event.getTarget())){
            event.setCancellationResult(InteractionResult.PASS);
            event.setCanceled(true);
        }
    }
}
