package ui;

import org.lwjglx.util.vector.Vector2f;

/**
 * Created by Beau on 17/09/2016.
 */
public class GUITexture {
    private int texture;
    private Vector2f position;
    private Vector2f scale;

    public GUITexture(int texture, Vector2f position, Vector2f scale) {
        this.texture = texture;
        this.position = position;
        this.scale = scale;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public int getTexture() {
        return texture;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }
}
