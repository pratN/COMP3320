package water;

import engine.ModelLoadHandler;
import engine.WindowHandler;
import entities.Camera;
import entities.Light;
import models.RawModel;
import org.lwjgl.opengl.GL13;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;
import java.util.*;

import util.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;


public class WaterRenderer {

	private static final float RIPPLE_AMOUNT = 0.06f;

	private RawModel quad;
	private WaterShader shader;
	private WaterFrameBuffers fbos;
	private int dudvTexture;
	private int normMap;
	private float rippleMove = 0;

	int i = 0;


	ModelLoadHandler loader;


	public WaterRenderer(ModelLoadHandler loader, WaterShader shader, Matrix4f projectionMatrix, WaterFrameBuffers fbos) {
		this.shader = shader;
		this.fbos = fbos;
		dudvTexture = loader.loadTexture("waterDUDV");
		normMap = loader.loadTexture("normalMap");
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		setUpVAO(loader);
		this.loader = loader;
	}

	public void render(List<WaterTile> water, Camera camera, Light sun, boolean under) {
		//prepareRender(camera, sun);
		float und = 1;
		if(under==true)
			und = -1;
			for (WaterTile tile : water) {
				prepareRender(camera, sun, tile.getTileSize(), und);
				Matrix4f modelMatrix = Maths.createTransformationMatrix(
						new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0,
						WaterTile.size);
				shader.loadModelMatrix(modelMatrix);
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
			}
			unbind();
		}

	
	private void prepareRender(Camera camera, Light sun, float tileSize, float under){
		shader.start();
		shader.loadUnderWater(under);
		shader.loadViewMatrix(camera);
		rippleMove += RIPPLE_AMOUNT*WindowHandler.getFrameTimeSeconds(); //move texture over time
		rippleMove %=1;//loop around
		shader.loadRippleMove(rippleMove);
        shader.loadTiling(tileSize);
		shader.loadLight(sun);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);//binding the reflec and refrac textures
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);//binding the reflec and refrac textures
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);//binding the dudv map
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);//binding the dudv map
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normMap);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);//binding the depth map
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	}
	
	private void unbind(){
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	private void setUpVAO(ModelLoadHandler loader) {
		// Just x and z vectex positions here, y is set to 0 in v.shader
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		quad = loader.loadToVAO(vertices, 2);
	}

}
