package shaders;

import entities.Camera;
import entities.Light;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;
import util.Maths;

import java.util.List;

/**
 * Created by Beau on 12/09/2016.
 */
public class TerrainShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "/shaders/terrainVertexShader.glsl";
    private static final String FRAG_FILE = "/shaders/terrainFragmentShader.glsl";
    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition[];
    private int location_lightColour[];
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_skyColour;
    private int location_backgroundTexture;
    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendMap;
    private int location_attenuation[];
    private int location_waterPlane;
    private int location_toShadowMapSpace;
    private int location_shadowMap;
    private int location_shadowDistance;
    private int location_totalTexels;
    private int location_pcfCount;
    private int location_mapSize;


    public TerrainShader() {
        super(VERTEX_FILE, FRAG_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_skyColour = super.getUniformLocation("skyColour");
        location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
        location_waterPlane = super.getUniformLocation("waterPlane");
        location_shadowDistance = super.getUniformLocation("shadowDistance");
        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColour = new int[MAX_LIGHTS];
        location_attenuation = new int[MAX_LIGHTS];
        location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        location_shadowMap = super.getUniformLocation("shadowMap");
        location_mapSize = super.getUniformLocation("mapSize");
        location_totalTexels = super.getUniformLocation("totalTexels");
        location_pcfCount = super.getUniformLocation("pcfCount");
        for(int i=0;i<MAX_LIGHTS;i++){
            location_lightPosition[i] = super.getUniformLocation("lightPosition["+i+"]");
            location_lightColour[i] = super.getUniformLocation("lightColour["+i+"]");
            location_attenuation[i] = super.getUniformLocation("attenuation["+i+"]");
        }
    }

    public void connectTextureUnits(){
        super.loadInt(location_backgroundTexture,0);
        super.loadInt(location_rTexture,1);
        super.loadInt(location_gTexture,2);
        super.loadInt(location_bTexture,3);
        super.loadInt(location_blendMap,4);
        super.loadInt(location_shadowMap,5);
    }


    public void loadMapSize(float size){
        super.loadFloat(location_mapSize, size);
    }
    public void loadPCFCount(int count){
        super.loadInt(location_pcfCount, count);
        super.loadFloat(location_totalTexels, (float)((count * 2.0 + 1.0) * (count * 2.0 + 1.0)));
    }
    public void loadToShadowMapMatrix(Matrix4f matrix){
        super.loadMatrix(location_toShadowMapSpace, matrix);
    }

    public void loadSkyColour(float r, float g, float b) {
        super.loadVector(location_skyColour, new Vector3f(r, g, b));
    }

    public void loadClipWaterPlane(Vector4f plane){super.loadVector(location_waterPlane,plane);}

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "texCoords");
        super.bindAttribute(2, "normal");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadLights(List<Light> lights){
        for(int i=0;i<MAX_LIGHTS;i++){
            if(i<lights.size()){
                super.loadVector(location_lightPosition[i],lights.get(i).getPosition());
                super.loadVector(location_lightColour[i],lights.get(i).getColour());
                super.loadVector(location_attenuation[i],lights.get(i).getAttenuation());

            }
            else
            {
                super.loadVector(location_lightPosition[i],new Vector3f(0,0,0));
                super.loadVector(location_lightColour[i],new Vector3f(0,0,0));
                super.loadVector(location_attenuation[i],new Vector3f(1,0,0));

            }
        }
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadShadowDistance(float dist){
        super.loadFloat(location_shadowDistance, dist);
    }
    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }
}
