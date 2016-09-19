package Entities;

import org.lwjglx.util.vector.Vector3f;

/**
 * Created by Auscav_Steve on 9/10/2016.
 */
public class Light {
    private Vector3f position;
    private Vector3f colour;
    private Vector3f attenuation = new  Vector3f(1,0,0);

    public Light(Vector3f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;

    }   public Light(Vector3f position, Vector3f colour, Vector3f attenuation) {
        this.position = position;
        this.colour = colour;
        this.attenuation=attenuation;

    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
}
