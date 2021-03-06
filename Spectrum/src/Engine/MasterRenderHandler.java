package engine;

import entities.*;
import models.*;
import shaders.*;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import terrain.Terrain;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector4f;

import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;


/**
 * Created by Beau on 12/09/2016.
 */
public class MasterRenderHandler {

    private StaticShader shader = new StaticShader();
    public static final float FOV = GraphicsConfig.FOV;
    public static float NEAR_PLANE = 0.1f;
    public static float FAR_PLANE = GraphicsConfig.DRAW_DISTANCE;

    private static final float RED = 0.38f;
    private static final float GREEN = 0.514f;
    private static final float BLUE = 0.702f;

    private Matrix4f projectionMatrix;
    private EntityRenderHandler renderer;
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();
    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private Map<TexturedModel, List<Entity>> normalMappedEntities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();
    private SkyboxRenderer skyboxRenderer;
    private NormalMappingRenderer normalMappingRenderer;
    private ShadowMapMasterRenderer shadowMapRenderer;

    public MasterRenderHandler(ModelLoadHandler loader, Camera camera) {
        enableCulling();
        createProjectionMatrix();
        renderer = new EntityRenderHandler(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
        normalMappingRenderer = new NormalMappingRenderer(projectionMatrix, RED, GREEN, BLUE);
        shadowMapRenderer = new ShadowMapMasterRenderer(camera);
    }

    /**
     * Enables depth culling for 3D objects.
     * Culling gets disabled for rendering particle effects and guis/text,
     * so it needs to be re-enabled for models
     */
    public static void enableCulling() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }


    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public static void disableCulling() {
        glDisable(GL_CULL_FACE);
    }

    /**
     * Calls the corresponding functions for the other renderers and shaders
     * @param lights
     * @param camera
     * @param waterClipPlane
     */
    public void render(List<Light> lights, Camera camera, Vector4f waterClipPlane) {
        prepare();
        shader.start();
        shader.loadClipWaterPlane(waterClipPlane);
        shader.loadSkyColour(RED, GREEN, BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        normalMappingRenderer.render(normalMappedEntities, waterClipPlane, lights, camera);
        terrainShader.start();
        terrainShader.loadClipWaterPlane(waterClipPlane);
        terrainShader.loadSkyColour(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
        terrainShader.stop();
        skyboxRenderer.render(camera, RED, GREEN, BLUE);
        terrains.clear();
        entities.clear();
        normalMappedEntities.clear();
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    /**
     * Renders scene to the shadowmap
     * @param entityList
     * @param nmEntityList
     * @param sun
     */
    public void renderShadowMap(List<Entity> entityList, List<Entity> nmEntityList, Light sun) {
        for(Entity entity : entityList) {
            processEntity(entity);
        }
        for(Entity entity : nmEntityList) {
            processNormalMappedEntity(entity);
        }
        shadowMapRenderer.render(entities, normalMappedEntities, sun);
        entities.clear();
    }

    public int getShadowMapTexture() {
        return shadowMapRenderer.getShadowMap();
    }

    /**
     * Processes the entity for rendering, retrieving its model and texture and adding it to the hashMap of entities for rendering
     * @param entity
     */
    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    /**
     * As above, but for normal mapped entities. They're renderred with a different process so they need to be added into a different map
     * @param entity
     */
    public void processNormalMappedEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = normalMappedEntities.get(entityModel);
        if(batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            normalMappedEntities.put(entityModel, newBatch);
        }
    }

    //public void clip(Vector4f waterClipPlane){shader.loadClipWaterPlane(waterClipPlane);}

    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
        normalMappingRenderer.cleanUp();
        shadowMapRenderer.cleanUp();
    }

    /**
     * Pre-render steps
     */
    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(RED, GREEN, BLUE, 1);
        glActiveTexture(GL_TEXTURE5);
        glBindTexture(GL_TEXTURE_2D, getShadowMapTexture());
    }

    /**
     * Create the projection matrix for the scene to enable perspective view
     */
    private void createProjectionMatrix() {
        projectionMatrix = new Matrix4f();
        float aspectRatio = WindowHandler.getWidth() / WindowHandler.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public static float getRED() {
        return RED;
    }

    public static float getGREEN() {
        return GREEN;
    }

    public static float getBLUE() {
        return BLUE;
    }
}
