package Engine;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;


/**
 * Created by Beau on 6/09/2016.
 */
public class Renderer {

    public void prepare(){
        glClearColor(1,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void render(RawModel model){
        glBindVertexArray(model.getVaoID());
        glEnableVertexAttribArray(0);
        glDrawArrays(GL_TRIANGLES,0,model.getVertexCount());
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }
}
