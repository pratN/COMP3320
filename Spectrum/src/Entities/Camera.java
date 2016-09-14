package Entities;

import org.lwjglx.util.vector.Vector3f;
import util.KeyboardHandler;
import util.MouseHandler;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    protected Vector3f position = new Vector3f(0,1,0);
    protected float pitch;
    protected float yaw;
    protected float roll;
    private float speed = 0.5f;


    public Camera(){}



    public void move(){

    }

    public void setPosition(float x, float y, float z){
        position.x=x;
        position.y=y;
        position.z=z;
    }
    public void increasePosition(float x, float y, float z){
        position.x+=x;
        position.y+=y;
        position.z+=z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
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
