package Shaders;

import Entities.Camera;
import Entities.Light;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import util.Maths;

public class StaticShader extends ShaderProgram{

    private static final String VERTEX_FILE = "src/Shaders/vertexShader.glsl";
    private static final String FRAG_FILE = "src/Shaders/fragmentShader.glsl";
    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition;
    private int location_lightColour;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_useFakeLighting;
    private int location_skyColour;
    private int location_numberOfRows;
    private int location_offset;

    public StaticShader(){
        super(VERTEX_FILE,FRAG_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColour = super.getUniformLocation("lightColour");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        location_skyColour = super.getUniformLocation("skyColour");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
    }

    public void loadSkyColour(float r, float g, float b){
        super.loadVector(location_skyColour,new Vector3f(r,g,b));
    }

    public void loadNumberOfRows(int numRows){
        super.loadFloat(location_numberOfRows,numRows);
    }

    public void loadOffset(float x, float y){
        super.load2DVector(location_offset,new Vector2f(x,y));
    }

    public void loadFakeLightingVariable(boolean useFake){
        super.loadBoolean(location_useFakeLighting,useFake);
    }

    public void loadShineVariables(float damper, float reflectivity){
        super.loadFloat(location_shineDamper,damper);
        super.loadFloat(location_reflectivity,reflectivity);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0,"position");
        super.bindAttribute(1,"texCoords");
        super.bindAttribute(2,"normal");
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadLight(Light light){
        super.loadVector(location_lightPosition, light.getPosition());
        super.loadVector(location_lightColour, light.getColour());

    }

    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(location_projectionMatrix,projection);
    }
}
