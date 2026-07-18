package net.sophka.polaroid.world.item;

import net.minecraft.world.item.Item;

public class FilmItem extends Item {
    public final FilmType filmType;
    public FilmItem(FilmType filmType, Properties properties) {
        super(properties);
        this.filmType = filmType;
    }
}
