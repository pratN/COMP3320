import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Models.RawModel;
import Models.TexturedModel;
import Shaders.StaticShader;
import Terrain.Terrain;
import Textures.ModelTexture;
import org.lwjgl.*;

//import World.World;

import Engine.*;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjglx.util.vector.Vector3f;
import util.KeyboardHandler;
import util.MouseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class EngineTester {

    private static int WIDTH = 1280;
    private static int HEIGHT = 720;
    private static GLFWKeyCallback keyCallback;
    private static MouseHandler mouseCallback;

    public static void main(String[] args) {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        try {
            init();
            loop();
        } finally {
            WindowHandler.closeWindow();
        }
    }

    //Initialisation
    private static void init() {
        String title = "Spectrum";
        WindowHandler.createWindow(WIDTH, HEIGHT, title);
        glfwSetKeyCallback(WindowHandler.getWindow(), keyCallback = new KeyboardHandler());
        glfwSetCursorPosCallback(WindowHandler.getWindow(), mouseCallback = new MouseHandler());
        glfwSetInputMode(WindowHandler.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    }

    //Main Loop
    private static void loop() {

        ModelLoadHandler loader = new ModelLoadHandler();

        RawModel dragonModel = OBJLoader.loadObjModel("dragon", loader);
        RawModel treeModel = OBJLoader.loadObjModel("tree", loader);

        TexturedModel dragonTexturedModel = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("red")));
        TexturedModel treeTexturedModel = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
        TexturedModel grassTexturedModel = new TexturedModel(OBJLoader.loadObjModel("grassModel",loader), new ModelTexture(loader.loadTexture("grassTexture")));
        grassTexturedModel.getTexture().setHasTransparency(true);
        grassTexturedModel.getTexture().setUseFakeLighting(true);
        TexturedModel fernTexturedModel = new TexturedModel(OBJLoader.loadObjModel("fern",loader),new ModelTexture(loader.loadTexture("fern")));
        fernTexturedModel.getTexture().setHasTransparency(true);

        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random();
        for(int i = 0; i < 500; i++){
            entities.add(new Entity(treeTexturedModel,new Vector3f(random.nextFloat() * 800 - 400, 0,random.nextFloat() * -600),0,0,0,3));
            entities.add(new Entity(grassTexturedModel,new Vector3f(random.nextFloat() * 800 - 400, 0,random.nextFloat() * -600),0,0,0,1));
            entities.add(new Entity(fernTexturedModel,new Vector3f(random.nextFloat() * 800 - 400, 0,random.nextFloat() * -600),0,0,0,0.6f));

        }

        ModelTexture dragonTexture = dragonTexturedModel.getTexture();
        dragonTexture.setShineDamper(5);
        dragonTexture.setReflectivity(0.75f);
        Entity dragonEntity = new Entity(dragonTexturedModel, new Vector3f(0,0,-10),0,0,0, 0.25f);
        Light light = new Light(new Vector3f(3000,2000,20),new Vector3f(1,1,1));

        Terrain terrain = new Terrain(0,-1,loader, new ModelTexture(loader.loadTexture("grass")));
        Terrain terrain2 = new Terrain(-1,-1,loader, new ModelTexture(loader.loadTexture("grass")));
        Camera camera = new Camera(mouseCallback);
        MasterRenderHandler renderer = new MasterRenderHandler();


        while(!KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE)  && !WindowHandler.close()) {
            camera.move();
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            renderer.render(light,camera);
            renderer.processEntity(dragonEntity);
            for(Entity entity:entities){
                renderer.processEntity(entity);
            }
            WindowHandler.updateWindow();
        }
        renderer.cleanUp();
        loader.cleanUp();
    }
}
