package Engine;

import org.lwjglx.util.vector.Vector3f;

import util.KeyboardHandler;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Beau on 9/09/2016.
 */
public class Camera {
    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch;
    private float yaw;
    private float roll;

    public Camera() {

    }

    public void move() {
        if(KeyboardHandler.isKeyDown(GLFW_KEY_W)) {
            position.z -= 0.02;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_D)) {
            position.x += 0.02f;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A)) {
            position.x -= 0.02f;
        }
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
