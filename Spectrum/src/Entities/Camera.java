package entities;

import org.lwjglx.util.vector.Vector3f;

public class Camera {

    protected Vector3f position = new Vector3f(0,1,0);
    protected float pitch;
    protected float yaw;
    protected float roll;

    public Camera(){}



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

    public void increasePitch(float pitch){
        this.pitch+=pitch;
    }

    public void increaseYaw(float yaw){
        this.yaw+= yaw;
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
