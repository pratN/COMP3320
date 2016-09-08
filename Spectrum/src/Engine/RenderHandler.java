package Engine;

import Entities.Entity;
import Models.RawModel;
import Models.TexturedModel;
import Shaders.StaticShader;
import org.lwjglx.util.vector.Matrix4f;
import util.Maths;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;


/**
 * Created by Beau on 6/09/2016.
 */
public class RenderHandler {

    public void prepare(){
        glClear(GL_COLOR_BUFFER_BIT);

        glClearColor(0,0,0,1);
    }

    /**
     * Takes in a textured model and renders it to the screen
     * Raw mesh with associated texture data and UV mapping
     */
    public void render(Entity entity, StaticShader shader){
        TexturedModel texturedModel = entity.getModel();
        RawModel model = texturedModel.getModel();
        glBindVertexArray(model.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        Matrix4f  transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),entity.getRotX(),
                entity.getRotY(),entity.getRotZ(),entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texturedModel.getTexture().getID());
        glDrawElements(GL_TRIANGLES, model.getVertexCount(),GL_UNSIGNED_INT,0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }
}
