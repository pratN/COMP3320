package Entities;

import Engine.WindowHandler;
import org.lwjglx.util.vector.Vector3f;
import util.KeyboardHandler;
import util.MouseHandler;
import Terrain.Terrain;

import static org.lwjgl.glfw.GLFW.*;


/**
 * Created by Beau on 14/09/2016.
 */
public class Player extends Camera {
    private float RUN_SPEED = 50;
    private float currentSpeed = 0;
    private float strafeSpeed = 0;
    protected float MOUSE_SENSITIVITY = 0.1f;
    private float CROUCH_MODIFIER = 1;
    private float SPRINT_MODIFIER = 1;
    private float playerHeight = 2;
    private MouseHandler mouseHandler;
    private float dx = 0;
    private float dz = 0;
    private float dy = 0;
    private float mouseLastX =0;
    private float mouseLastY =0;
    private float mouseDX =0;
    private float mouseDY =0;
    private static final float GRAVITY = -60;
    private static final float JUMP_POWER = 25;
    private float upwardsSpeed = 0;
    private boolean airborne = false;

    public Player(MouseHandler mouseHandler) {
        this.mouseHandler = mouseHandler;
    }
    public Player(MouseHandler mouseHandler, Vector3f pos) {
        this.mouseHandler = mouseHandler;
        super.position = pos;
        super.setPosition(pos.x,pos.y,pos.z);
    }

    public void move(Terrain terrain) {
        checkInputs();
        super.increasePitch(pitch);
        super.increaseYaw(yaw);
        increasePosition(dx,0,dz);
        upwardsSpeed += GRAVITY * WindowHandler.getFrameTimeSeconds();
        dy = upwardsSpeed * WindowHandler.getFrameTimeSeconds();
        increasePosition(0, dy, 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z) + (playerHeight*CROUCH_MODIFIER);
        if(getPosition().y < terrainHeight){
            upwardsSpeed = 0;
            airborne = false;
            position.y = terrainHeight;
        }
    }

    private void jump() {
        if(!airborne) {
            this.upwardsSpeed = JUMP_POWER;
            airborne = true;
        }
    }

    private void checkInputs() {
        resetValues();
        if(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT_SHIFT)){
            SPRINT_MODIFIER = 2f;
        }
        else
        {
            SPRINT_MODIFIER =1;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT_CONTROL)){
            CROUCH_MODIFIER = 0.5f;
        }
        else
        {
            CROUCH_MODIFIER =1;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_W)) {
            currentSpeed = RUN_SPEED * SPRINT_MODIFIER;
            dx += (float) Math.sin(Math.toRadians(yaw)) * (currentSpeed * WindowHandler.getFrameTimeSeconds());
            dz -= (float)Math.cos(Math.toRadians(yaw)) * (currentSpeed * WindowHandler.getFrameTimeSeconds());

        } else if(KeyboardHandler.isKeyDown(GLFW_KEY_S)) {
            currentSpeed = RUN_SPEED;
            dx -= (float)Math.sin(Math.toRadians(yaw)) * (currentSpeed * WindowHandler.getFrameTimeSeconds());
            dz += (float)Math.cos(Math.toRadians(yaw)) * (currentSpeed * WindowHandler.getFrameTimeSeconds());
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A)) {
            strafeSpeed = RUN_SPEED;
            dx += (float)Math.sin(Math.toRadians(yaw - 90)) * (strafeSpeed * WindowHandler.getFrameTimeSeconds());
            dz -= (float)Math.cos(Math.toRadians(yaw - 90)) * (strafeSpeed * WindowHandler.getFrameTimeSeconds());
        } else if(KeyboardHandler.isKeyDown(GLFW_KEY_D)) {
            strafeSpeed = RUN_SPEED;
            dx += (float)Math.sin(Math.toRadians(yaw + 90)) * (strafeSpeed * WindowHandler.getFrameTimeSeconds());
            dz -= (float)Math.cos(Math.toRadians(yaw + 90)) * (strafeSpeed * WindowHandler.getFrameTimeSeconds());
        }
        mouseDX = (mouseHandler.getX()-mouseLastX) * MOUSE_SENSITIVITY;
        mouseDY = (mouseHandler.getY()-mouseLastY) * MOUSE_SENSITIVITY;

        pitch = mouseDY;
        yaw   = mouseDX;

        if(KeyboardHandler.isKeyDown(GLFW_KEY_SPACE)) {
            jump();
        }

    }

    public void resetValues(){
        dx=0;
        dz=0;
        strafeSpeed = 0;
        currentSpeed = 0;
    }
}
