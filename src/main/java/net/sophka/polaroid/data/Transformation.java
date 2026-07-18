package net.sophka.polaroid.data;


@FunctionalInterface
public interface Transformation {
    TransformableImage apply(TransformableImage image);

    default Transformation then(Transformation transformation){
        return image -> transformation.apply(apply(image));
    }
}
