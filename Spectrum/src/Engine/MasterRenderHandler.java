package Engine;

import Entities.*;
import Models.*;
import Shaders.*;
import Terrain.Terrain;
import org.lwjglx.util.vector.Matrix4f;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClearColor;


/**
 * Created by Beau on 12/09/2016.
 */
public class MasterRenderHandler {

    private StaticShader shader = new StaticShader();
    private int WIDTH = 1280;
    private int HEIGHT = 720;
    private static final float FOV = 70;
    private static float NEAR_PLANE = 0.1f;
    private static float FAR_PLANE = 1000;
    private Matrix4f projectionMatrix;
    private EntityRenderHandler renderer;
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();
    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    public MasterRenderHandler(){
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        createProjectionMatrix();
        renderer = new EntityRenderHandler(shader,WIDTH, HEIGHT, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }
    public void render(Light sun, Camera camera){
        prepare();
        shader.start();
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadLight(sun);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        terrains.clear();
        entities.clear();
    }

    public void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }
    public void processEntity(Entity entity){
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch!=null){
            batch.add(entity);
        }else{
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel,newBatch);
        }
    }
    public void cleanUp(){
        shader.cleanUp();
        terrainShader.cleanUp();
    }

    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        glClearColor(0, 0, 0, 1);
    }
    private void createProjectionMatrix(){
        float aspectRatio = (float) WIDTH / (float) HEIGHT;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }
}
