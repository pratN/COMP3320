package Models;

import Textures.ModelTexture;

/**
 * Created by Beau on 7/09/2016.
 */
public class TexturedModel {

    private RawModel model;
    private ModelTexture texture;

    public TexturedModel(RawModel raw, ModelTexture tex){
        this.model = raw;
        this.texture = tex;
    }

    public RawModel getModel() {
        return model;
    }

    public ModelTexture getTexture() {
        return texture;
    }
}
