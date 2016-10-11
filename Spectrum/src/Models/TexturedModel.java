package models;

import textures.ModelTexture;

/**
 * Created by Beau on 7/09/2016.
 */
public class TexturedModel {

    private RawModel model;
    private ModelTexture texture;

    /**
     *
     * @param raw
     * Raw untextured mesh
     * @param tex
     * Processed texture to be applied
     */
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
