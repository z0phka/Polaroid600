package net.sophka.polaroid.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.world.item.component.FilmContent;

public class CartridgeItem extends Item {
    public CartridgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem) {
        if (self.getCount() != 1) return false;
        if (clickAction == ClickAction.PRIMARY && other.isEmpty()) {
            return false;
        } else {
            FilmContent initialContents = self.get(ModDataComponents.FILM_CONTENT.get());
            if (initialContents == null) {
                return false;
            } else {
                FilmContent.Mutable contents = new FilmContent.Mutable(initialContents);
                if (clickAction == ClickAction.PRIMARY && !other.isEmpty()) {
                    contents.tryInsert(other);
                    self.set(ModDataComponents.FILM_CONTENT.get(), contents.toImmutable());
                    this.broadcastChangesOnContainerMenu(player);
                    return true;
                }
                return false;
            }
        }
    }

    private void broadcastChangesOnContainerMenu(Player player) {
        AbstractContainerMenu containerMenu = player.containerMenu;
        if (containerMenu != null) {
            containerMenu.slotsChanged(player.getInventory());
        }
    }

}
