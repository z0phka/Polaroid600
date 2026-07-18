package net.sophka.polaroid.utils;

import net.minecraft.world.level.storage.LevelResource;
import net.sophka.polaroid.Polaroid600;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Utils {


    public static final LevelResource PHOTOS = new LevelResource("photos");

    public static byte[] compressInts(int[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos);
             DataOutputStream dos = new DataOutputStream(gzip)) {
            for (int value : data) {
                dos.writeInt(value);
            }
        }
        Polaroid600.LOGGER.debug("{}/{} B", data.length, baos.toByteArray().length);
        return baos.toByteArray();
    }

    public static int[] decompressInts(byte[] compressed, int length) throws IOException {
        int[] result = new int[length];

        try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed));
             DataInputStream dis = new DataInputStream(gzip)) {

            for (int i = 0; i < length; i++) {
                result[i] = dis.readInt();
            }
        }

        return result;
    }

    public static double clampUnit(double value){
        return Math.clamp(value,0,1);
    }
    public static float clampUnit(float value){
        return Math.clamp(value,0,1);
    }
}
