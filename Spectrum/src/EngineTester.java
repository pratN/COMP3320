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

        TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("Wood_Test_Texture")));
        Entity entity = new Entity(texturedModel, new Vector3f(0,0,-25),0,0,0,1);
        Light light = new Light(new Vector3f(0,0,-20),new Vector3f(1,1,1));

        Camera camera = new Camera();

        while(!WindowHandler.close()) {
            entity.increaseRotation(0,1,0);
            camera.move();
            renderer.prepare();
            shader.start();
            shader.loadLight(light);
            shader.loadViewMatrix(camera);
            renderer.render(entity,shader);
            shader.stop();
            WindowHandler.updateWindow();
        }
        shader.cleanUp();
        loader.cleanUp();
    }
}
