package engine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.text.Format;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class WindowHandler {
    private static long window;
    private static Format.Field errorCallback;
    private static float lastFrameTime;
    private static float delta;
    private static float WINDOW_WIDTH;
    private static float WINDOW_HEIGHT;
    private static int MSAA_SAMPLE_AMOUNT = 8;

    /**
     * Set up and create the window display
     * @param WIDTH
     * Window width
     * @param HEIGHT
     * Window height
     * @param title
     * Program title
     */
    public static void createWindow(int WIDTH, int HEIGHT, String title) {
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        GLFWErrorCallback.createPrint(System.err).set();
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_SAMPLES, MSAA_SAMPLE_AMOUNT);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        window = glfwCreateWindow(WIDTH, HEIGHT, title, 0, 0);
        WINDOW_WIDTH = WIDTH;
        WINDOW_HEIGHT = HEIGHT;
        if (window == 0) {
            throw new RuntimeException("Failed to create window");
        }
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
        });
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Make the window visible
        glfwShowWindow(window);
        glfwSetWindowPos(
                window,
                (vidmode.width() - WIDTH) / 2,
                (vidmode.height() - HEIGHT) / 2
        );

        GL.createCapabilities();
        lastFrameTime = getCurrentTime();

    }


    public static void updateWindow() {
          //  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            glfwSwapBuffers(window); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        float currentFrameTime = getCurrentTime();
        delta = (currentFrameTime-lastFrameTime)/1000;
        lastFrameTime=currentFrameTime;
    }

    public static float getFrameTimeSeconds(){
        return delta;
    }

    public static void closeWindow() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    private static float getCurrentTime(){
        return (float) (glfwGetTime() * 1000);

    }

    public static void setMsaaSampleAmount(int msaaSampleAmount) {
        MSAA_SAMPLE_AMOUNT = msaaSampleAmount;
    }

    public static long getWindow(){
        return window;
    }

    public static boolean close(){
        return glfwWindowShouldClose(window);
    }

    public static float getWidth() {
        return WINDOW_WIDTH;
    }

    public static void setWidth(float windowWidth) {
        WINDOW_WIDTH = windowWidth;
    }

    public static float getHeight() {
        return WINDOW_HEIGHT;
    }

    public static void setHeight(float windowHeight) {
        WINDOW_HEIGHT = windowHeight;
    }
}
