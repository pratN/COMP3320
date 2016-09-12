package Engine;

import Entities.*;
import Models.*;
import Shaders.*;

import java.util.*;


/**
 * Created by Beau on 12/09/2016.
 */
public class MasterRenderHandler {

    private StaticShader shader = new StaticShader();
    private int WIDTH = 1280;
    private int HEIGHT = 720;
    private RenderHandler renderer = new RenderHandler(shader,WIDTH, HEIGHT);

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    public void render(Light sun, Camera camera){
        renderer.prepare();
        shader.start();
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        entities.clear();
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
    }
}
