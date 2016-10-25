package postProcessing;

import engine.GraphicsConfig;
import engine.ModelLoadHandler;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import gaussianBlur.HorizontalBlur;
import gaussianBlur.VerticalBlur;
import models.RawModel;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur;
	private static HorizontalBlur hBlur2;
	private static VerticalBlur vBlur2;
	private static int blurScale=12;

	public static void init(ModelLoadHandler loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		hBlur = new HorizontalBlur(GraphicsConfig.WINDOW_WIDTH/blurScale,GraphicsConfig.WINDOW_HEIGHT/blurScale);
		vBlur = new VerticalBlur(GraphicsConfig.WINDOW_WIDTH/blurScale,GraphicsConfig.WINDOW_HEIGHT/blurScale);
		hBlur2 = new HorizontalBlur(GraphicsConfig.WINDOW_WIDTH/(blurScale/2),GraphicsConfig.WINDOW_HEIGHT/(blurScale/2));
		vBlur2 = new VerticalBlur(GraphicsConfig.WINDOW_WIDTH/(blurScale/2),GraphicsConfig.WINDOW_HEIGHT/(blurScale/2));
	}
	
	public static void doPostProcessing(int colourTexture){
		start();
		hBlur2.render(colourTexture);
		vBlur2.render(hBlur2.getOutputTexture());
		hBlur.render(vBlur2.getOutputTexture());
		vBlur.render(hBlur.getOutputTexture());
		contrastChanger.render(vBlur.getOutputTexture());
		end();
	}

	public static void doPostProcessingNoBlur(int colourTexture){
		start();
		contrastChanger.render(colourTexture);
		end();
	}
	
	public static void cleanUp(){
		contrastChanger.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
	}
	
	private static void start(){
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		glDisable(GL_DEPTH_TEST);
	}
	
	private static void end(){
		glEnable(GL_DEPTH_TEST);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}


}
