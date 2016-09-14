package Entities;

import Engine.WindowHandler;
import org.lwjgl.glfw.GLFW;
import util.KeyboardHandler;
import util.MouseHandler;

import static org.lwjgl.glfw.GLFW.*;


/**
 * Created by Beau on 14/09/2016.
 */
public class Player extends Camera {
    private float RUN_SPEED = 20;
    private float currentSpeed = 0;
    private float strafeSpeed = 0;
    protected float mouseSensitivity = 0.1f;
    private MouseHandler mouseHandler;
    private float dx=0;
    private float dz=0;
    private float dy=0;
    private static final float GRAVITY = -30;
    private static final float JUMP_POWER = 15;
    private float upwardsSpeed = 0;
    private static final float TERRAIN_HEIGHT = 2;
    private boolean airborne = false;
    public Player(MouseHandler mouseHandler) {
        this.mouseHandler = mouseHandler;
    }

    public void move() {
        checkInputs();
        super.setPitch(pitch);
        super.setYaw(yaw);
        position.x=dx;
        position.z=dz;
        upwardsSpeed +=GRAVITY*WindowHandler.getFrameTimeSeconds();
        dy=upwardsSpeed*WindowHandler.getFrameTimeSeconds();
        increasePosition(0,dy,0);

        if (getPosition().y<TERRAIN_HEIGHT){
            upwardsSpeed=0;
            airborne=false;
            getPosition().y=TERRAIN_HEIGHT;
        }



    }
    private void jump(){
        if(!airborne) {
            this.upwardsSpeed = JUMP_POWER;
            airborne = true;
        }
    }

    private void checkInputs() {
        if(KeyboardHandler.isKeyDown(GLFW_KEY_W)) {
            currentSpeed = RUN_SPEED;
            dx += Math.sin(Math.toRadians(yaw)) * (currentSpeed * WindowHandler.getFrameTimeSeconds());
            dz -= Math.cos(Math.toRadians(yaw)) * (currentSpeed * WindowHandler.getFrameTimeSeconds());

        } else if(KeyboardHandler.isKeyDown(GLFW_KEY_S)) {
            currentSpeed = RUN_SPEED;
            dx -= Math.sin(Math.toRadians(yaw)) * (currentSpeed * WindowHandler.getFrameTimeSeconds());
            dz += Math.cos(Math.toRadians(yaw)) * (currentSpeed * WindowHandler.getFrameTimeSeconds());

        } else currentSpeed = 0;
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A)) {
            strafeSpeed = RUN_SPEED;
            dx += Math.sin(Math.toRadians(yaw - 90)) * (strafeSpeed * WindowHandler.getFrameTimeSeconds());
            dz -= Math.cos(Math.toRadians(yaw - 90)) * (strafeSpeed * WindowHandler.getFrameTimeSeconds());

        } else if(KeyboardHandler.isKeyDown(GLFW_KEY_D)) {
            strafeSpeed = RUN_SPEED;
            dx += Math.sin(Math.toRadians(yaw + 90)) * (strafeSpeed * WindowHandler.getFrameTimeSeconds());
            dz -= Math.cos(Math.toRadians(yaw + 90)) * (strafeSpeed * WindowHandler.getFrameTimeSeconds());

        } else {
            strafeSpeed = 0;
        }
        pitch = mouseHandler.getY() * mouseSensitivity;
        yaw = mouseHandler.getX() * mouseSensitivity;

        if(KeyboardHandler.isKeyDown(GLFW_KEY_SPACE)) {
            jump();
        }

    }
}
