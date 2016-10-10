package Entities;

import Engine.WindowHandler;
import org.lwjglx.util.vector.Vector3f;
import util.KeyboardHandler;
import static org.lwjgl.glfw.GLFW.*;

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
    /*********************for testing purposes********************************/
    public void changeColour(){
        if(KeyboardHandler.isKeyDown(GLFW_KEY_KP_1)){
            this.colour.z -= 0.5f* WindowHandler.getFrameTimeSeconds();
        }else if(KeyboardHandler.isKeyDown(GLFW_KEY_KP_3)){
            this.colour.z+= 0.5f* WindowHandler.getFrameTimeSeconds();
        }else if(KeyboardHandler.isKeyDown(GLFW_KEY_KP_4)){
            this.colour.x-= 0.5f* WindowHandler.getFrameTimeSeconds();
        }else if(KeyboardHandler.isKeyDown(GLFW_KEY_KP_6)){
            this.colour.x+= 0.5f* WindowHandler.getFrameTimeSeconds();
        }else if(KeyboardHandler.isKeyDown(GLFW_KEY_KP_7)){
            this.colour.y-= 0.5f* WindowHandler.getFrameTimeSeconds();
        }else if(KeyboardHandler.isKeyDown(GLFW_KEY_KP_9)){
            this.colour.y+= 0.5f* WindowHandler.getFrameTimeSeconds();
        }

    }
    public void move(){
        if(KeyboardHandler.isKeyDown(GLFW_KEY_UP)){
            this.position.x++;
        }else if(KeyboardHandler.isKeyDown(GLFW_KEY_DOWN)){
            this.position.x--;
        }else if(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT)){
            this.position.z--;
        }else if(KeyboardHandler.isKeyDown(GLFW_KEY_RIGHT)){
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
