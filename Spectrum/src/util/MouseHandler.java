package util;

/**
 * Created by Beau on 12/09/2016.
 */
import org.lwjgl.glfw.GLFWCursorPosCallback;

// Our MouseHandler class extends the abstract class
// abstract classes should never be instantiated so here
// we create a concrete that we can instantiate
public class MouseHandler extends GLFWCursorPosCallback {
    private static float x;
    private static float y;
    @Override
    public void invoke(long window, double xpos, double ypos) {
        // TODO Auto-generated method stub
        // this basically just prints out the X and Y coordinates
        // of our mouse whenever it is in our window
        this.x=(float)xpos;
        this.y=(float)ypos;
    }

    public static float getX() {
        return x;
    }

    public static float getY() {
        return y;
    }
}
