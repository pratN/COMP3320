import Entities.Entity;
import Models.RawModel;
import Models.TexturedModel;
import Shaders.StaticShader;
import Textures.ModelTexture;
import org.lwjgl.*;

import World.World;

import Engine.*;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjglx.util.vector.Vector3f;
import util.KeyboardHandler;

import static org.lwjgl.glfw.GLFW.*;

public class EngineTester {

    private static int WIDTH = 1280;
    private static int HEIGHT = 720;
    private static String title = "Spectrum";
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

        WindowHandler.createWindow(WIDTH, HEIGHT, title);
        glfwSetKeyCallback(WindowHandler.getWindow(), keyCallback = new KeyboardHandler());
    }

    //Main Loop
    private static void loop() {

        ModelLoadHandler loader = new ModelLoadHandler();
        StaticShader shader = new StaticShader();
        RenderHandler renderer = new RenderHandler(shader,WIDTH,HEIGHT);


        RawModel model = OBJLoader.loadObjModel("stall",loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("stallTexture"));
        TexturedModel texturedModel = new TexturedModel(model, texture);
        Entity entity = new Entity(texturedModel, new Vector3f(0,0,-50),0,0,0,1);

        Camera camera = new Camera();

        while(!WindowHandler.close()) {
            renderer.prepare();
            entity.increaseRotation(0,1,0);
            camera.move();
            shader.start();
            shader.loadViewMatrix(camera);
            renderer.render(entity,shader);
            shader.stop();
            WindowHandler.updateWindow();
        }
        shader.cleanUp();
        loader.cleanUp();
    }
}
