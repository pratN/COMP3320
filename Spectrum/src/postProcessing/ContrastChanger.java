package postProcessing;

import shaders.ContrastShader;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Beau on 13/10/2016.
 */
public class ContrastChanger {
    private ImageRenderer renderer;
    private ContrastShader shader;

    public ContrastChanger(){
        shader = new ContrastShader();
        renderer = new ImageRenderer();
    }

    public void render(int texture){
        shader.start();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,texture);
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp(){
        renderer.cleanUp();
        shader.cleanUp();
    }

}
