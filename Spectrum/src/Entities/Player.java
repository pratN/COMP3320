package Entities;

import org.lwjglx.util.vector.Vector3f;
import util.KeyboardHandler;
import util.MouseHandler;

import static org.lwjgl.glfw.GLFW.*;

public class Player {

    private Vector3f position = new Vector3f(0,1,0);
    private float pitch;
    private float yaw;
    private float roll;
    private float mouseSensitivity = 0.1f;
    private MouseHandler mouseHandler;
    private float speed = 0.5f;

    public Player(MouseHandler mouseHandler){
        this.mouseHandler=mouseHandler;
    }

    public void move(){

        if(KeyboardHandler.isKeyDown(GLFW_KEY_W)){
            position.x += Math.sin(Math.toRadians(yaw)) * speed;
            position.z -= Math.cos(Math.toRadians(yaw)) * speed;

        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_S)){
            position.x -= Math.sin(Math.toRadians(yaw)) * speed;
            position.z += Math.cos(Math.toRadians(yaw)) * speed;

        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A)){
            position.x += Math.sin(Math.toRadians(yaw - 90)) * speed;
            position.z -= Math.cos(Math.toRadians(yaw - 90)) * speed;

        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_D)){
            position.x += Math.sin(Math.toRadians(yaw + 90)) * speed;
            position.z -= Math.cos(Math.toRadians(yaw + 90)) * speed;

        }if(KeyboardHandler.isKeyDown(GLFW_KEY_SPACE)){
            position.y+=speed;
        }if(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT_CONTROL)){
            position.y-=speed;
        }

        pitch = mouseHandler.getY()*mouseSensitivity;
        yaw = mouseHandler.getX()*mouseSensitivity;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
