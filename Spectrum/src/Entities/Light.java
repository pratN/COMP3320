package entities;

import engine.WindowHandler;
import org.lwjglx.Sys;
import org.lwjglx.util.vector.Vector3f;
import ui.GUITexture;
import util.KeyboardHandler;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Auscav_Steve on 9/10/2016.
 */
public class Light {
    private Vector3f position;
    private Vector3f colour;
    private Vector3f attenuation = new Vector3f(1, 0, 0);
    private Vector3f offAttenuation = new Vector3f(1000, 1000, 1000);
    private Vector3f startingAttenuation;
    private int col = 1;
    private boolean keyPressed = false;
    private boolean lightOffKeyPressed = false;

    public Light(Vector3f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;

    }

    public Light(Vector3f position, Vector3f colour, Vector3f attenuation) {
        this.position = position;
        this.colour = colour;
        this.attenuation = offAttenuation;
        this.startingAttenuation = attenuation;

    }

    /*********************
     * for testing purposes
     ********************************/
    public void changeColour(List<GUITexture> guis, GUITexture red, GUITexture blue, GUITexture green, GUITexture off) {
        if(KeyboardHandler.isKeyDown(GLFW_KEY_TAB)) {
            System.out.println(lightOffKeyPressed);
            if(!lightOffKeyPressed) {
                if(attenuation == startingAttenuation) {
                    attenuation = offAttenuation;
                    guis.remove(guis.size() - 1);
                    guis.add(off);

                } else {
                    attenuation = startingAttenuation;
                    guis.remove(guis.size() - 1);
                    if(col == 1) guis.add(red);
                    if(col == 2) guis.add(green);
                    if(col == 3) guis.add(blue);
                }
                lightOffKeyPressed = true;

            }


        } else {
            lightOffKeyPressed = false;
        }

        if(KeyboardHandler.isKeyDown(GLFW_KEY_F)) {
            if(!keyPressed) {
                col++;
                if(col > 3) {
                    col = 1;
                }
                if(col == 1) {
                    this.colour.x = 1;
                    this.colour.y = 0;
                    this.colour.z = 0;
                    if(attenuation == startingAttenuation) {
                        guis.remove(guis.size() - 1);
                        guis.add(red);
                    }
                }
                if(col == 2) {
                    this.colour.x = 0;
                    this.colour.y = 1;
                    this.colour.z = 0;
                    if(attenuation == startingAttenuation) {
                        guis.remove(guis.size() - 1);
                        guis.add(green);
                    }
                }
                if(col == 3) {
                    this.colour.x = 0;
                    this.colour.y = 0;
                    this.colour.z = 1;
                    if(attenuation == startingAttenuation) {
                        guis.remove(guis.size() - 1);
                        guis.add(blue);
                    }
                }
            }
            keyPressed = true;

        } else {
            keyPressed = false;


        }
    }

    public void move() {
        if(KeyboardHandler.isKeyDown(GLFW_KEY_UP)) {
            this.position.x++;
        } else if(KeyboardHandler.isKeyDown(GLFW_KEY_DOWN)) {
            this.position.x--;
        } else if(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT)) {
            this.position.z--;
        } else if(KeyboardHandler.isKeyDown(GLFW_KEY_RIGHT)) {
            this.position.z++;
        }
    }

    /**************************************************************************/

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
