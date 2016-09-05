import org.lwjgl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import World.World;

import Engine.*;
public class Main {

    private static int WIDTH = 1280;
    private static int HEIGHT = 720;
    private static String title = "Spectrum";
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        try {
            init();
            loop();

        } finally {
            WindowHandler.closeWindow();
        }
    }

    private void init() {
        WindowHandler.createWindow(WIDTH,HEIGHT,title);
    }

    private void loop() {

        WindowHandler.updateWindow();
    }

    public static void main(String[] args) {
        new Main().run();
    }

}