package Engine;

import Models.RawModel;
import Models.TexturedModel;

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

        glClearColor(1,0,0,1);
    }

    public void render(TexturedModel texturedModel){
        RawModel model = texturedModel.getModel();
        glBindVertexArray(model.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texturedModel.getTexture().getID());
        glDrawElements(GL_TRIANGLES, model.getVertexCount(),GL_UNSIGNED_INT,0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }
}
