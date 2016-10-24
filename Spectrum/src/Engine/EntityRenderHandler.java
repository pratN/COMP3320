package engine;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import org.lwjglx.util.vector.Matrix4f;
import util.Maths;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;


public class EntityRenderHandler {

    private StaticShader shader;

    public EntityRenderHandler(StaticShader shader, Matrix4f projectionMatrix){
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }


    /**
     * Draws the entity to screen in the proper location
     * @param entities
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

    /**
     * Prepares the textured model for rendering
     * @param model
     */
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
        shader.loadNumberOfRows(texture.getNumberOfRows());
        shader.loadShineVariables(texture.getShineDamper(),texture.getReflectivity());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, model.getTexture().getID());
    }

    /**
     * Unbinds arrays when done with rendering
     */
    public void unbindTexturedModel(){
        MasterRenderHandler.enableCulling();
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    /**
     * Prepares a single instance of an entity for rendering
     * @param entity
     */
    public void prepareInstance(Entity entity){
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadColorState(entity.getColorMode());
        shader.loadOffset(entity.getTextureXOffset(),entity.getTextureYOffset());
    }



}
