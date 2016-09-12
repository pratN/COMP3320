import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Models.RawModel;
import Models.TexturedModel;
import Shaders.StaticShader;
import Textures.ModelTexture;
import org.lwjgl.*;

//import World.World;

import Engine.*;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjglx.util.vector.Vector3f;
import util.KeyboardHandler;

import static org.lwjgl.glfw.GLFW.*;

public class EngineTester {

    private static int WIDTH = 1280;
    private static int HEIGHT = 720;
    private static GLFWKeyCallback keyCallback;


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
    }

    //Main Loop
    private static void loop() {

        ModelLoadHandler loader = new ModelLoadHandler();
        StaticShader shader = new StaticShader();
        RenderHandler renderer = new RenderHandler(shader,WIDTH,HEIGHT);


        RawModel model = OBJLoader.loadObjModel("dragon", loader);

        TexturedModel texturedModel1 = new TexturedModel(model, new ModelTexture(loader.loadTexture("red")));
        ModelTexture texture1 = texturedModel1.getTexture();
        texture1.setShineDamper(10);
        texture1.setReflectivity(1);
        TexturedModel texturedModel2 = new TexturedModel(model, new ModelTexture(loader.loadTexture("green")));
        ModelTexture texture2 = texturedModel2.getTexture();
        texture2.setShineDamper(10);
        texture2.setReflectivity(1);
        TexturedModel texturedModel3 = new TexturedModel(model, new ModelTexture(loader.loadTexture("blue")));
        ModelTexture texture3 = texturedModel3.getTexture();
        texture3.setShineDamper(10);
        texture3.setReflectivity(1);
        Entity entity1 = new Entity(texturedModel1, new Vector3f(0,0,-10),0,0,0, 0.25f);
        Entity entity2 = new Entity(texturedModel2, new Vector3f(5,0,-10),0,0,0, 0.25f);
        Entity entity3 = new Entity(texturedModel3, new Vector3f(-5,0,-10),0,0,0, 0.25f);
        Light light = new Light(new Vector3f(0,0,-1),new Vector3f(1,1,1));

        Camera camera = new Camera();

        while(!WindowHandler.close()) {
            entity1.increaseRotation(0,0,0);
            entity2.increaseRotation(0,0,0);
            entity3.increaseRotation(0,0,0);
            camera.move();
            renderer.prepare();
            shader.start();
            shader.loadLight(light);
            shader.loadViewMatrix(camera);
            renderer.render(entity1,shader);
            renderer.render(entity2,shader);
            renderer.render(entity3,shader);
            shader.stop();
            WindowHandler.updateWindow();
        }
        shader.cleanUp();
        loader.cleanUp();
    }
}
