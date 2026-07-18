package net.sophka.polaroid.data;

import java.util.List;

public interface CompositeTransformation extends FilmTransformation{

    List<Transformation> transformations();

    default List<TransformableImage> subImages(TransformableImage image){
        return transformations().stream().map(transformation -> transformation.apply(image)).toList();
    }

    @Override
    default TransformableImage apply(TransformableImage image) {
        return composite(subImages(image));
    }

    TransformableImage composite (List<TransformableImage> subimages);
}
