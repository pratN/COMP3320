package Shaders;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;


public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/Shaders/particleVShader.glsl";
	private static final String FRAGMENT_FILE = "src/Shaders/particleFShader.glsl";

	private int location_modelViewMatrix;
	private int location_projectionMatrix;
	private int location_texOffset1;
	private int location_texOffset2;
	private int location_texCoordInfo;
	private int location_colourMode;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	public void getAllUniformLocations() {
		location_modelViewMatrix = super.getUniformLocation("modelViewMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_texOffset1 = super.getUniformLocation("texOffset1");
		location_texOffset2 = super.getUniformLocation("texOffset2");
		location_texCoordInfo = super.getUniformLocation("texCoordInfo");
		location_colourMode = super.getUniformLocation("colourMode");
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	public void loadModelViewMatrix(Matrix4f modelView) {
		super.loadMatrix(location_modelViewMatrix, modelView);
	}

	public void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

	public void loadTextureCoordInfo(Vector2f offset1, Vector2f offset2, float numRows, float blend){
		super.load2DVector(location_texOffset1, offset1);
		super.load2DVector(location_texOffset2, offset2);
		super.load2DVector(location_texCoordInfo, new Vector2f(numRows,blend));
	}

	public void loadColourMode(int colourMode){
		super.loadInt(location_colourMode, colourMode);
	}

}
