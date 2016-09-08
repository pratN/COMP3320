package Shaders;

import org.lwjglx.util.vector.Matrix4f;

/**
 * Created by Beau on 6/09/2016.
 */
public class StaticShader extends ShaderProgram{

    private static final String VERTEX_FILE = "src/Shaders/vertexShader.glsl";
    private static final String FRAG_FILE = "src/Shaders/fragmentShader.glsl";
    private int location_transformationMatrix;
    private int location_projectionMatrix;

    public StaticShader(){
        super(VERTEX_FILE,FRAG_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0,"position");
        super.bindAttribute(1,"texCoords");
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(location_projectionMatrix,projection);
    }
}
