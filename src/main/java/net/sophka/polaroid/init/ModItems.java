package net.sophka.polaroid.init;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.world.item.*;
import net.sophka.polaroid.world.item.component.CameraFilm;
import net.sophka.polaroid.world.item.component.DoubleExposure;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Polaroid600.MODID);

    public static final DeferredItem<Item> CAMERA_600 = ITEMS.registerItem("camera_600", p -> new CameraItem(p, new CameraItem.CameraProperties()), p -> p.stacksTo(1).component(ModDataComponents.CAMERA_FILM, CameraFilm.EMPTY).component(ModDataComponents.EXPOSURE,0));
    public static final DeferredItem<Item> CAMERA_600_AF = ITEMS.registerItem("camera_600_af", p -> new CameraItem(p,new CameraItem.CameraProperties().withAF()),p -> p.stacksTo(1).component(ModDataComponents.CAMERA_FILM, CameraFilm.EMPTY).component(ModDataComponents.EXPOSURE,0));
    public static final DeferredItem<Item> CAMERA_NOW = ITEMS.registerItem("camera_now", p -> new CameraItem(p,new CameraItem.CameraProperties().withDoubleExposure().withAF().withTimer()),p -> p.stacksTo(1).component(ModDataComponents.CAMERA_FILM, CameraFilm.EMPTY).component(ModDataComponents.EXPOSURE,0).component(ModDataComponents.DOUBLE_EXPOSURE, DoubleExposure.OFF).component(ModDataComponents.AF,false));
    public static final DeferredItem<Item> CAMERA_GO = ITEMS.registerItem("camera_go", p -> new CameraItem(p,new CameraItem.CameraProperties().withDoubleExposure().withAF().withFilmFormat(FilmFormat._GO).withTimer().withSelfieMirror()), p -> p.stacksTo(1).component(ModDataComponents.CAMERA_FILM, CameraFilm.EMPTY).component(ModDataComponents.EXPOSURE,0).component(ModDataComponents.DOUBLE_EXPOSURE, DoubleExposure.OFF).component(ModDataComponents.AF,false));
    public static final DeferredItem<Item> CAMERA_SPECTRA = ITEMS.registerItem("camera_spectra", p -> new CameraItem(p,new CameraItem.CameraProperties().withAF().withFilmFormat(FilmFormat._1200).withTimer()), p -> p.stacksTo(1).component(ModDataComponents.CAMERA_FILM, CameraFilm.EMPTY).component(ModDataComponents.EXPOSURE,0).component(ModDataComponents.AF,false));
    public static final DeferredItem<Item> CAMERA_SX_70 = ITEMS.registerItem("camera_sx_70", p -> new CameraItem(p,new CameraItem.CameraProperties().withFilmFormat(FilmFormat._SX_70).withAF()),p -> p.stacksTo(1).component(ModDataComponents.CAMERA_FILM, CameraFilm.EMPTY).component(ModDataComponents.EXPOSURE,0).component(ModDataComponents.AF,false));

    public static final DeferredItem<PhotoItem> PHOTO = ITEMS.registerItem("photo", PhotoItem::new, p -> p.stacksTo(1).component(ModDataComponents.CREATED_TIME,0L));

    public static final DeferredItem<FilmItem> FILM_600 = ITEMS.registerItem("film_600", p -> new FilmItem(FilmType.COLOR, p), p -> p.stacksTo(10));
    public static final DeferredItem<FilmItem> FILM_600_BW = ITEMS.registerItem("film_600_bw", p -> new FilmItem(FilmType.BW, p), p -> p.stacksTo(10));
    public static final DeferredItem<FilmItem> FILM_600_BLUE = ITEMS.registerItem("film_600_blue", p -> new FilmItem(FilmType.BLUE, p), p -> p.stacksTo(10));
    public static final DeferredItem<FilmItem> FILM_600_GREEN = ITEMS.registerItem("film_600_green", p -> new FilmItem(FilmType.GREEN, p), p -> p.stacksTo(10));
    public static final DeferredItem<FilmItem> FILM_600_PURPLE = ITEMS.registerItem("film_600_purple", p -> new FilmItem(FilmType.PURPLE, p), p -> p.stacksTo(10));
    public static final DeferredItem<FilmItem> FILM_600_DEBUG_DIGITAL = ITEMS.registerItem("film_600_debug_digital", p -> new FilmItem(FilmType.COLOR, p), p -> p.stacksTo(10));
    public static final DeferredItem<FilmItem> FILM_600_DEBUG_DEPTH = ITEMS.registerItem("film_600_debug_depth", p -> new FilmItem(FilmType.COLOR, p), p -> p.stacksTo(10));

    public static final DeferredItem<Item> BLUE_LIGHT_SENSITIVE_LAYER = ITEMS.registerSimpleItem("blue_light_sensitive_layer", p -> p.stacksTo(64));
    public static final DeferredItem<Item> GREEN_LIGHT_SENSITIVE_LAYER = ITEMS.registerSimpleItem("green_light_sensitive_layer", p -> p.stacksTo(64));
    public static final DeferredItem<Item> RED_LIGHT_SENSITIVE_LAYER = ITEMS.registerSimpleItem("red_light_sensitive_layer", p -> p.stacksTo(64));
    public static final DeferredItem<Item> LIGHT_SENSITIVE_LAYER = ITEMS.registerSimpleItem("light_sensitive_layer", p -> p.stacksTo(64));
    public static final DeferredItem<Item> CYAN_DEVELOPER = ITEMS.registerSimpleItem("cyan_developer", p -> p.stacksTo(64));
    public static final DeferredItem<Item> MAGENTA_DEVELOPER = ITEMS.registerSimpleItem("magenta_developer", p -> p.stacksTo(64));
    public static final DeferredItem<Item> YELLOW_DEVELOPER = ITEMS.registerSimpleItem("yellow_developer", p -> p.stacksTo(64));
    public static final DeferredItem<Item> BW_REAGENT = ITEMS.registerSimpleItem("bw_reagent", p -> p.stacksTo(64));
    public static final DeferredItem<Item> COLOR_REAGENT = ITEMS.registerSimpleItem("color_reagent", p -> p.stacksTo(64));
    public static final DeferredItem<Item> BW_NEGATIVE = ITEMS.registerSimpleItem("bw_negative", p -> p.stacksTo(64));
    public static final DeferredItem<Item> COLOR_NEGATIVE = ITEMS.registerSimpleItem("color_negative", p -> p.stacksTo(64));

    public static final DeferredItem<CameraTripodItem> CAMERA_TRIPOD = ITEMS.registerItem("camera_tripod", CameraTripodItem::new, p -> p.stacksTo(16));


}
