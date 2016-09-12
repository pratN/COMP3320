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

        RawModel model = OBJLoader.loadObjModel("dragon", loader);
        TexturedModel texturedModel1 = new TexturedModel(model, new ModelTexture(loader.loadTexture("red")));
        ModelTexture texture1 = texturedModel1.getTexture();
        texture1.setShineDamper(10);
        texture1.setReflectivity(0.25f);
        Entity entity = new Entity(texturedModel1, new Vector3f(0,-1,-10),0,0,0, 0.25f);
        Light light = new Light(new Vector3f(3000,2000,20),new Vector3f(1,1,1));

        Terrain terrain = new Terrain(0,-1,loader, new ModelTexture(loader.loadTexture("ice_ground")));
        Terrain terrain2 = new Terrain(-1,-1,loader, new ModelTexture(loader.loadTexture("ice_ground")));
        Camera camera = new Camera(mouseCallback);
        MasterRenderHandler renderer = new MasterRenderHandler();


        while(!KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE)  && !WindowHandler.close()) {
            camera.move();
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            renderer.render(light,camera);
            renderer.processEntity(entity);
            WindowHandler.updateWindow();
        }
        renderer.cleanUp();
        loader.cleanUp();
    }
}
