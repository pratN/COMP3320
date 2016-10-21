

package water;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Vector4f;
import java.util.List;
import entities.Entity;
import entities.Light;
import entities.Camera;
import particles.ParticleHandler;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import terrain.Terrain;
import engine.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brendon on 28/09/2016.
 *
 * Class that handles all water creation and shading, just requires positions for a quad in the world where water will be
 */

public class Water {

    private List<WaterTile> water;
    private WaterFrameBuffers fbos;
    private Fbo PPFbo;
    private WaterShader waterShade;
    private WaterRenderer waterRend;

    public Water(List<WaterTile> water, ModelLoadHandler loader, MasterRenderHandler renderer, Fbo fbo) {
        this.water = water;
        fbos = new WaterFrameBuffers();
        waterShade = new WaterShader();
        waterRend = new WaterRenderer(loader, waterShade, renderer.getProjectionMatrix(), fbos);
        PPFbo = fbo;
    }

    public void setWater(MasterRenderHandler renderer, Camera player, Terrain terrain, List<Entity> entities, List<Entity> normalMappedEntities, List<Light> lights  ) {

        GL11.glEnable(GL30.GL_CLIP_DISTANCE0); //to enable clipping for water reflection/refraction

        if (player.getPosition().y < water.get(0).getHeight()) { //if player is underwater only textures for refraction
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
            renderer.render(lights, player, new Vector4f(0, -1, 0, 10000000)); //set clip plane for refraction here
            entities.forEach(renderer::processEntity);
            normalMappedEntities.forEach(renderer::processNormalMappedEntity);
            ParticleHandler.renderParticles(player);

            //***********set proper frame buffers for normal scene rendering**************
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0); //stop any clipping used for water
            fbos.unbindCurrentFrameBuffer();
            glEnable(GL_CULL_FACE);
            glCullFace(GL_FRONT);
            PPFbo.bindFrameBuffer();

            waterRend.render(water, player, lights.get(0),true);
            PPFbo.unbindFrameBuffer();

            renderer.enableCulling();
        }
        else
        {
            //render reflection texture
            fbos.bindReflectionFrameBuffer();
            float cameraDistance = 2 * (player.getPosition().y - water.get(0).getHeight()); //0 is for water height atm
            player.getPosition().y -= cameraDistance;
            player.setPitch(-player.getPitch());//invert pitch

            renderer.processTerrain(terrain);
            entities.forEach(renderer::processEntity);
            normalMappedEntities.forEach(renderer::processNormalMappedEntity);
            renderer.render(lights, player, new Vector4f(0, 1, 0, -water.get(0).getHeight()+1f)); //the vector is the clipping plane so for reflection only render everything above water height (at 0)
            ParticleHandler.renderParticles(player);

            //re-adjust camera
            player.getPosition().y += cameraDistance;
            player.setPitch(-player.getPitch());//invert pitch

            //render refraction texture
            fbos.bindRefractionFrameBuffer();
            renderer.processTerrain(terrain);
            entities.forEach(renderer::processEntity);
            normalMappedEntities.forEach(renderer::processNormalMappedEntity);
            renderer.render(lights, player, new Vector4f(0, -1, 0, water.get(0).getHeight()+1f)); //set clip plane for refraction here

            //***********set proper frame buffers for normal scene rendering**************
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0); //stop any clipping used for water
            fbos.unbindCurrentFrameBuffer();

            PPFbo.bindFrameBuffer();

            waterRend.render(water, player, lights.get(0),false);
            PPFbo.unbindFrameBuffer();
            //PostProcessing.doPostProcessing(PPFbo.getColourTexture());


        }
    }

    public void cleanUp(){
        waterShade.cleanUp();
    }

}