package net.sophka.polaroid.compatiblity;

import net.irisshaders.iris.api.v0.IrisApi;

public class IrisHelper {
    public static boolean isShaderPackInUse(){
        return IrisApi.getInstance().isShaderPackInUse();
    }
}
