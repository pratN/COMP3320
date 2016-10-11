package water;


		import shaders.ShaderProgram;
		import util.Maths;







		import entities.Camera;
		import entities.Light;
		import org.lwjglx.util.vector.Matrix4f;



public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "src/water/waterVertex.glsl";
	private final static String FRAGMENT_FILE = "src/water/waterFragment.glsl";

	private float tileSize;

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_relfectTexture;
	private int location_refractTexture;
	private int location_dudvMap;
	private int location_rippleMove;
	private int location_camPos;
	private int location_normMap;
	private int location_lightColour;
	private int location_lightPos;
	private int location_tiling;
	private int location_depthMap;
	private int location_underWater;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_relfectTexture = getUniformLocation("reflectTexture");
		location_refractTexture = getUniformLocation("refractTexture");
		location_dudvMap = getUniformLocation("dudvMap");
		location_rippleMove = getUniformLocation("rippleMove");
		location_camPos = getUniformLocation("camPos");
		location_normMap = getUniformLocation("normMap");
		location_lightColour = getUniformLocation("lightColour");
		location_lightPos = getUniformLocation("lightPos");
		location_tiling = getUniformLocation("tiling");
		location_depthMap = getUniformLocation("depthMap");
		location_underWater = getUniformLocation("underWater");

	}

	public void connectTextureUnits(){
		super.loadInt(location_relfectTexture,0);
		super.loadInt(location_refractTexture,1);
		super.loadInt(location_dudvMap,2);
		super.loadInt(location_normMap,3);
		super.loadInt(location_depthMap,4);
	}

	public void loadRippleMove(float amount){
		super.loadFloat(location_rippleMove, amount);
	}

	public void loadUnderWater(float under){
		super.loadFloat(location_underWater, under);
	}


	public void loadTiling(float amount){
		super.loadFloat(location_tiling, amount);
	}

	public void loadLight(Light sun){
		super.loadVector(location_lightColour,sun.getColour());
		super.loadVector(location_lightPos,sun.getPosition());
	}


	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}

	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);

		super.loadVector(location_camPos,camera.getPosition());
	}



	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}

}
