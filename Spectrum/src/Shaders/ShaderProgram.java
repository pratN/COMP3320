package Shaders;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjglx.util.vector.Matrix4f;
//import org.lwjglx.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;


/**
 * Created by Beau on 6/09/2016.
 */
public abstract class ShaderProgram {
    private int programID;
    private int vertShaderID;
    private int fragShaderID;

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public ShaderProgram(String vertexFile,String fragmentFile){
        vertShaderID = loadShader(vertexFile,GL_VERTEX_SHADER);
        fragShaderID =  loadShader(fragmentFile, GL_FRAGMENT_SHADER);
        programID = glCreateProgram();
        glAttachShader(programID,vertShaderID);
        glAttachShader(programID,fragShaderID);
        bindAttributes();
        glLinkProgram(programID);
        glValidateProgram(programID);
        getAllUniformLocations();
    }
    public void start(){

        glUseProgram(programID);
    }

    public void stop(){

        glUseProgram(0);
    }

    protected int getUniformLocation(String uniformName){
        return glGetUniformLocation(programID, uniformName);
    }

    protected abstract void getAllUniformLocations();



    /**
     * Memory Management
     */
    public void cleanUp(){
        stop();
        glDetachShader(programID,vertShaderID);
        glDetachShader(programID,fragShaderID);
        glDeleteShader(vertShaderID);
        glDeleteShader(fragShaderID);
        glDeleteProgram(programID);
    }

    protected void bindAttribute(int att, String variableName){
        glBindAttribLocation(programID,att,variableName);
    }

    protected abstract void bindAttributes();

    /**
     * Load a float from the uniform variables
     * @param location
     * @param value
     */
    protected void loadFloat(int location, float value){
        glUniform1f(location,value);
    }

    protected void loadVector(int location, Vector3f vector){
        glUniform3f(location,vector.x,vector.y,vector.z);
    }

    protected void loadBoolean(int location, boolean value){
        float toLoad = 0;
        if(value){
            toLoad = 1;
        }
        glUniform1f(location,toLoad);
    }

    protected void loadMatrix(int location, Matrix4f matrix){
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        glUniformMatrix4fv(location, false, matrixBuffer);
    }
    /**
     * Loads a shader into memory
     * @param file
     * The file the shader is stored in
     * @param type
     * The type of shader being passed in
     * @return
     * Return the id of the shader being passed in
     */
    public static int loadShader(String file, int type){
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();

        }catch(IOException  e){
            System.err.println("Could not read file");
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID,shaderSource);
        glCompileShader(shaderID);
        if(glGetShaderi(shaderID,GL_COMPILE_STATUS) == GL_FALSE){
            System.out.println(glGetShaderInfoLog(shaderID,500));
            System.err.println("Could not compile shader");
            System.exit(-1);
        }
        return shaderID;
    }

}

