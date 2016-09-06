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

        Loader loader = new Loader();
        Renderer renderer = new Renderer();

        float[] vertices = { -0.5f, 0.5f, 0f, -0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, 0.5f, 0f, -0.5f, 0.5f, 0f
        };

        RawModel model = loader.loadToVAO(vertices);
        System.out.println("Gets to here");
        while(!WindowHandler.close()){
            renderer.prepare();
            //game logic
            renderer.render(model);
            WindowHandler.updateWindow();
        }
        loader.cleanUp();
    }
}