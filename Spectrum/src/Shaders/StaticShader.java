package Shaders;

/**
 * Created by Beau on 6/09/2016.
 */
public class StaticShader extends ShaderProgram{

    private static final String VERTEX_FILE = "src/Shaders/vertexShader.glsl";
    private static final String FRAG_FILE = "src/Shaders/fragmentShader.glsl";

    public StaticShader(){
        super(VERTEX_FILE,FRAG_FILE);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0,"position");
        super.bindAttribute(1,"texCoords");
    }
}
