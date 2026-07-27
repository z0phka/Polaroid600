package net.sophka.polaroid.server.photo;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PhotoTokenManager {
    public record Entry(UUID id, Supplier<ItemStack> camera, Consumer<ItemStack> post, boolean consumeFilm){}

    private static PhotoTokenManager instance;
    private int nextToken = 0;

    private final Map<Integer, Entry> tokens = new ConcurrentHashMap<>();

    private PhotoTokenManager(){
        instance = this;
    }

    public static PhotoTokenManager getInstance(){
        return instance == null ? new PhotoTokenManager() : instance;
    }

    public int acquireTokenFor(Player player, Supplier<ItemStack> camera, Consumer<ItemStack> post, boolean consumeFilm){
        int token = nextToken++;
        tokens.put(token,new Entry(player.getUUID(), camera, post, consumeFilm));
        return token;
    }

    public int acquireTokenFor(Player player, Supplier<ItemStack> camera, Consumer<ItemStack> post){
        return acquireTokenFor(player, camera, post, true);
    }

    public boolean verify(int token, Player player){
        if(tokens.containsKey(token)){
            return tokens.get(token).id.equals(player.getUUID());
        }
        return false;
    }

    public Optional<Entry> get(int token){
        return Optional.ofNullable(tokens.get(token));
    }

    public void invalidateToken(int token){
        tokens.remove(token);
    }
}
