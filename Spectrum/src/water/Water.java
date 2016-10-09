

package water;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Vector4f;

import java.util.List;
import Entities.Entity;
import Entities.Light;
import Entities.Camera;
import Terrain.Terrain;
import Engine.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brendon on 28/09/2016.
 */


public class Water {

    private List<WaterTile> water;
    private WaterFrameBuffers fbos;
    private WaterShader waterShade;
    private WaterRenderer waterRend;

    GUIRenderer guiRenderer;// = new GUIRenderer(loader);


    public Water(List<WaterTile> water, ModelLoadHandler loader, MasterRenderHandler renderer) {
        this.water = water;
        fbos = new WaterFrameBuffers();
        waterShade = new WaterShader();
        waterRend = new WaterRenderer(loader, waterShade, renderer.getProjectionMatrix(), fbos);

        guiRenderer = new GUIRenderer(loader);


    }

    public void setWater(MasterRenderHandler renderer, Camera player, Terrain terrain, List<Entity> entities, List<Entity> normalMappedEntities, List<Light> lights  ) {

        GL11.glEnable(GL30.GL_CLIP_DISTANCE0); //to enable clipping for water reflection/refraction


        if (player.getPosition().y < water.get(0).getHeight()) {

            //render refraction texture
            fbos.bindReflectionFrameBuffer();
            float cameraDistance = 2 * (player.getPosition().y - water.get(0).getHeight()); //0 is for water height atm
            player.getPosition().y -= cameraDistance;
            player.setPitch(-player.getPitch());//invert pitch
            renderer.processTerrain(terrain);
            renderer.render(lights, player, new Vector4f(0, 1, 0, -water.get(0).getHeight())); //the vector is the clipping plane so for reflection only render everything above water height (at 0)
            entities.forEach(renderer::processEntity);
            normalMappedEntities.forEach(renderer::processNormalMappedEntity);
            //re-adjust camera
            player.getPosition().y += cameraDistance;
            player.setPitch(-player.getPitch());//invert pitch


            fbos.bindRefractionFrameBuffer();
            renderer.processTerrain(terrain);
            renderer.render(lights, player, new Vector4f(0, -1, 0, -water.get(0).getHeight())); //set clip plane for refraction here
            entities.forEach(renderer::processEntity);
            normalMappedEntities.forEach(renderer::processNormalMappedEntity);


            //***********set proper frame buffers for normal scene rendering**************
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0); //stop any clipping used for water
            fbos.unbindCurrentFrameBuffer();
            //renderer.enableCulling();

            glEnable(GL_CULL_FACE);
            glCullFace(GL_FRONT);

            waterRend.render(water, player, lights.get(0),true);
            renderer.enableCulling();




        /*
            List<GUITexture> guis = new ArrayList<>();

            float[]  positions ={-1,1,-1,-1,1,1,1,-1};
            RawModel quad = loader.loadToVAO(positions,2);
            glDrawArrays(GL_TRIANGLE_STRIP,0,quad.getVertexCount());





            GUITexture gui = new GUITexture(fbos.getUnderRefractionTexture(), new Vector2f(0f,0f), new Vector2f(1f,1f) );
            guis.add(gui);
            guiRenderer.render(guis);*/






        } else {

            //render reflection texture
            fbos.bindReflectionFrameBuffer();
            float cameraDistance = 2 * (player.getPosition().y - water.get(0).getHeight()); //0 is for water height atm
            player.getPosition().y -= cameraDistance;
            player.setPitch(-player.getPitch());//invert pitch
            renderer.processTerrain(terrain);
            renderer.render(lights, player, new Vector4f(0, 1, 0, -water.get(0).getHeight())); //the vector is the clipping plane so for reflection only render everything above water height (at 0)
            entities.forEach(renderer::processEntity);
            normalMappedEntities.forEach(renderer::processNormalMappedEntity);

            //re-adjust camera
            player.getPosition().y += cameraDistance;
            player.setPitch(-player.getPitch());//invert pitch


            //render refraction texture
            fbos.bindRefractionFrameBuffer();
            renderer.processTerrain(terrain);
            renderer.render(lights, player, new Vector4f(0, -1, 0, water.get(0).getHeight())); //set clip plane for refraction here
            entities.forEach(renderer::processEntity);
            normalMappedEntities.forEach(renderer::processNormalMappedEntity);

            //***********set proper frame buffers for normal scene rendering**************
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0); //stop any clipping used for water
            fbos.unbindCurrentFrameBuffer();
            waterRend.render(water, player, lights.get(0),false);


        }
    }


    public void cleanUp(){
        waterShade.cleanUp();
    }

}
