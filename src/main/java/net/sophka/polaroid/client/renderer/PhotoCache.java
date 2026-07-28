package net.sophka.polaroid.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.network.PhotoRequestPayload;
import net.sophka.polaroid.world.item.FilmFormat;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PhotoCache {
    private static PhotoCache instance;

    public record PhotoData(FilmFormat format, Identifier texture){}

    private final ConcurrentHashMap<String, PhotoData> cachedData = new ConcurrentHashMap<>();
    private final HashSet<String> requested = new HashSet<>();

    public void clearCache(){
        cachedData.values().forEach(entry -> Minecraft.getInstance().getTextureManager().release(entry.texture()));
        requested.clear();
        cachedData.clear();
    }

    private void request(String id){
        if(requested.contains(id)){
            return;
        }
        requested.add(id);
        ClientPacketDistributor.sendToServer(new PhotoRequestPayload(id));
    }

    public Optional<PhotoData> get(String id){
        if(id == null){
            return Optional.of(blankShot());
        }
        if(!cachedData.containsKey(id)){
            request(id);
        }
        return Optional.ofNullable(cachedData.getOrDefault(id, blankShot()));
    }

    public PhotoData save(FilmFormat format, String id, NativeImage nativeImage){
        Identifier identifier = Identifier.fromNamespaceAndPath(Polaroid600.MODID,"user_photo/" + id);
        DynamicTexture texture = new DynamicTexture(() -> id, nativeImage);
        Minecraft.getInstance().getTextureManager().register(identifier,texture);
        PhotoData data = new PhotoData(format, identifier);
        cachedData.put(id, data);
        return data;
    }

    public PhotoData blankShot(){
        if(!cachedData.containsKey("debug")){
            FilmFormat format = FilmFormat._600;
            NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, format.width, format.height, true);

            for(int x = 0; x < format.width; x++){
                for(int y = 0; y < format.height; y++){
                    nativeImage.setPixelABGR(x, y, ARGB.toABGR(ARGB.color(255,0)));
                }
            }
            return save(format, "debug", nativeImage);
        }
        return cachedData.get("debug");
    }

    public static PhotoCache getInstance(){
        if(instance == null){
            instance = new PhotoCache();
        }
        return instance;
    }
}
