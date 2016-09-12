package Engine;

import Entities.Entity;
import Models.RawModel;
import Models.TexturedModel;
import Shaders.StaticShader;
import Textures.ModelTexture;
import org.lwjgl.opengl.GL;
import org.lwjglx.util.vector.Matrix4f;
import util.Maths;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;


public class EntityRenderHandler {
    private int WIDTH;
    private int HEIGHT;

    private StaticShader shader;

    public EntityRenderHandler(StaticShader shader, int WIDTH, int  HEIGHT, Matrix4f projectionMatrix){
        this.shader = shader;

        this.WIDTH=WIDTH;
        this.HEIGHT=HEIGHT;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    /**
     * Takes in a textured model and renders it to the screen
     * Raw mesh with associated texture data and UV mapping
     */

    public void render(Map<TexturedModel, List<Entity>> entities){
        for(TexturedModel model:entities.keySet()){
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for(Entity entity:batch){
                prepareInstance(entity);
                glDrawElements(GL_TRIANGLES, model.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);

            }
            unbindTexturedModel();
        }
    }

    public void prepareTexturedModel(TexturedModel model){
        RawModel raw = model.getModel();
        glBindVertexArray(raw.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        ModelTexture texture = model.getTexture();
        if(texture.isHasTransparency()){
            MasterRenderHandler.disableCulling();
        }
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(),texture.getReflectivity());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, model.getTexture().getID());
    }

    public void unbindTexturedModel(){
        MasterRenderHandler.enableCulling();
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    public void prepareInstance(Entity entity){
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }



}
