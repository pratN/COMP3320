package engine;

import ui.GUITexture;
import models.RawModel;
import shaders.GuiShader;
import org.lwjglx.util.vector.Matrix4f;
import util.Maths;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import java.util.List;

/**
 * Created by Beau on 17/09/2016.
 */
public class GUIRenderer {
    private final RawModel quad;
    private GuiShader shader;

    public GUIRenderer(ModelLoadHandler loader){
        float[]  positions ={-1,1,-1,-1,1,1,1,-1};
        quad = loader.loadToVAO(positions,2);
        shader =  new GuiShader();
    }

    public void render(List<GUITexture>  guis){
        shader.start();
        glBindVertexArray(quad.getVaoID());
        glEnableVertexAttribArray(0);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        for(GUITexture gui:guis){
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D,gui.getTexture());
            Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(),gui.getScale());
            shader.loadTransformation(matrix);
            glDrawArrays(GL_TRIANGLE_STRIP,0,quad.getVertexCount());
        }
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
    }
}
