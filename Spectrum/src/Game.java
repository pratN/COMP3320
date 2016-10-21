import engine.MasterRenderHandler;
import engine.ModelLoadHandler;
import engine.WindowHandler;
import entities.Entity;
import entities.Light;
import entities.Player;
import particles.ParticleSystem;
import terrain.Terrain;
import util.KeyboardHandler;
import water.Water;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/**
 * Created by Beau on 14/09/2016.
 */
public class Game{

    private Player player;
    private Terrain terrain;
    private List<Entity> entities;
    private List<Light> lights;
    private List<ParticleSystem> particleSystems;
    private List<Entity> normalMappedEntities;
    private List<Water> waters;
    private MasterRenderHandler renderer;
    private ModelLoadHandler loader;


    public static void main(String[] args){
        Game game = new Game();
        game.init();
        game.run();
        game.close();
    }


    public void init(){
        //Create window

        //Create renderHandler

        //Create world
    }

    public void loop(){
        //Render loop
        //Game logic
    }

    public void createWorld(){

    }

    public void close(){
        //Clean up

        System.exit(0);
    }

    public void run(){
        createWorld();
        while(!KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE) && !WindowHandler.close()) {
            loop();
        }
    }


}
