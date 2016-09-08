import Entities.Entity;
import Models.RawModel;
import Models.TexturedModel;
import Shaders.StaticShader;
import Textures.ModelTexture;
import org.lwjgl.*;

//import World.World;

import Engine.*;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector3f;

public class EngineTester {

    private static int WIDTH = 1280;
    private static int HEIGHT = 720;
    private static String title = "Spectrum";


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
    }

    //Main Loop
    private static void loop() {

        ModelLoadHandler loader = new ModelLoadHandler();
        StaticShader shader = new StaticShader();
        RenderHandler renderer = new RenderHandler(shader,WIDTH,HEIGHT);

        float[] vertices = {-0.5f, 0.5f, 0f,    //V0
                -0.5f, -0.5f, 0f,   //V1
                0.5f, -0.5f, 0f,    //V2
                0.5f, 0.5f, 0f,     //V3
        };
        int[] indices = {0, 1, 3, 3, 1, 2};

        float[] texCoords = {0, 0,    //V0
                0, 1,    //V1
                1, 1,    //V2
                1, 0     //V3
        };

        RawModel model = loader.loadToVAO(vertices, texCoords, indices);
        ModelTexture texture = new ModelTexture(loader.loadTexture("Wood_Test_Texture"));
        TexturedModel texturedModel = new TexturedModel(model, texture);
        Entity entity = new Entity(texturedModel, new Vector3f(0,0,0),0,0,0,1);
        while(!WindowHandler.close()) {
            renderer.prepare();

            entity.increasePosition(0,0,-0.1f);
            entity.increaseRotation(0,0,0);

            shader.start();
            renderer.render(entity,shader);
            shader.stop();
            WindowHandler.updateWindow();
        }
        shader.cleanUp();
        loader.cleanUp();
    }
}
