import Shaders.StaticShader;
import org.lwjgl.*;

//import World.World;

import Engine.*;
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
    private static void init() {
        WindowHandler.createWindow(WIDTH,HEIGHT,title);
    }

    private static void loop() {

        ModelLoadHandler loader = new ModelLoadHandler();
        RenderHandler renderer = new RenderHandler();
        StaticShader shader= new StaticShader();

        float[] vertices = {
                -0.5f, 0.5f, 0f,    //V0
                -0.5f, -0.5f, 0f,   //V1
                0.5f, -0.5f, 0f,    //V2
                0.5f, 0.5f, 0f,     //V3
        };
        int[] indices= {
                0,1,3,
                3,1,2};

        RawModel model = loader.loadToVAO(vertices, indices);
        while(!WindowHandler.close()){
            renderer.prepare();
            //game logic
            shader.start();
            renderer.render(model);
            shader.stop();
            WindowHandler.updateWindow();
        }
        shader.cleanUp();
        loader.cleanUp();
    }
}