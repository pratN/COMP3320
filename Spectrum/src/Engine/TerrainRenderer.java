package Engine;

import Entities.Entity;
import Models.RawModel;
import Models.TexturedModel;
import Shaders.TerrainShader;
import Terrain.Terrain;
import Textures.ModelTexture;
import org.lwjglx.util.vector.*;
import util.Maths;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * Created by Beau on 12/09/2016.
 */
public class TerrainRenderer {
    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix){
        this.shader=shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(List<Terrain> terrains){
        for(Terrain terrain:terrains){
            prepareTerrain((terrain));
            loadModelMatrix(terrain);
            glDrawElements(GL_TRIANGLES, terrain.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);

            unbindTexturedModel();
        }
    }

    public void prepareTerrain(Terrain terrain){
        RawModel raw = terrain.getModel();
        glBindVertexArray(raw.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        ModelTexture texture = terrain.getTexture();
        shader.loadShineVariables(texture.getShineDamper(),texture.getReflectivity());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getID());
    }

    public void unbindTexturedModel(){
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    public void loadModelMatrix(Terrain terrain){
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(),0,terrain.getZ()),0,0,0,1);
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
