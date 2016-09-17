package Shaders;

import org.lwjglx.util.vector.Matrix4f;

/**
 * Created by Beau on 17/09/2016.
 */
public class GuiShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/Shaders/guiVertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/Shaders/guiFragmentShader.glsl";

    private int location_transformationMatrix;

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTransformation(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}





