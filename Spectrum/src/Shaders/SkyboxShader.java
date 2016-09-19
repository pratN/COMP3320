package Shaders;

import org.lwjglx.util.vector.Matrix4f;
import Entities.Camera;

import util.Maths;

public class SkyboxShader extends ShaderProgram{

    private static final String VERTEX_FILE = "src/Shaders/skyboxVertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/Shaders/skyboxFragmentShader.glsl";

    private int location_projectionMatrix;
    private int location_viewMatrix;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}