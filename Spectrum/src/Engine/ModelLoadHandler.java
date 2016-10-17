package engine;

import models.RawModel;
import org.lwjglx.opengl.GLContext;
import textures.TextureData;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Beau on 6/09/2016.
 */
public class ModelLoadHandler {
    private static List<Integer> vaos = new ArrayList<>();
    private static List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();
    private static float MIPMAP_BIAS = GraphicsConfig.MIPMAP_BIAS;
    private static float AF_LEVEL = GraphicsConfig.AF_LEVEL;

    /**
     *
     * @param positions
     * The XYZ positions of each vertex
     * @param texCoords
     * The UV positions of the texture
     * @param indices
     * The values of the index buffer
     * @return
     * Returns a raw 3D mesh
     */
    public static RawModel loadToVAO(float[] positions, float[] texCoords,float [] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, texCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public static int loadToVAO(float[] positions, float[] texCoords) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, texCoords);
        unbindVAO();
        return vaoID;
    }

    public static RawModel loadToVAO(float[] positions, float[] texCoords,float [] normals, float[] tangents, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, texCoords);
        storeDataInAttributeList(2, 3, normals);
        storeDataInAttributeList(3, 3, tangents);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(float[] positions, int dimensions){
        int vaoID  = createVAO();
        this.storeDataInAttributeList(0,dimensions,positions);
        unbindVAO();
        return new RawModel(vaoID,positions.length/dimensions);
    }
    /**
     * Generates a vao
     * @return
     * returns the ID of the vao
     */
    private static int createVAO() {
        int vaoID = glGenVertexArrays();
        vaos.add(vaoID);
        glBindVertexArray(vaoID);
        return vaoID;
    }

    public int loadCubeMap(String[] textureFiles){
        int texID = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP,texID);
        for(int i = 0;i<textureFiles.length;i++){
            TextureData data = decodeTextureFile("assets/textures/"+textureFiles[i]+".png");
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X+i,0,GL_RGBA,data.getWidth(),data.getHeight(),0,GL_RGBA,GL_UNSIGNED_BYTE,data.getBuffer());
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        textures.add(texID);
        glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);


        return texID;
    }
    private TextureData decodeTextureFile(String fileName) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            FileInputStream in = new FileInputStream(fileName);
            PNGDecoder decoder = new PNGDecoder(in);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, Format.RGBA);
            buffer.flip();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Tried to load texture " + fileName + ", didn't work");
            System.exit(-1);
        }
        return new TextureData(buffer, width, height);
    }

    /**
     * Loads a texture from an image in assets folder
     * @param fileName
     * file name of the texture to load
     * @return
     * Returns the ID of the loaded texture
     */
    public int loadTexture(String fileName) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("assets/textures/" + fileName + ".png"));
            glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
            glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_LOD_BIAS, MIPMAP_BIAS);
            if(GL.createCapabilities().GL_EXT_texture_filter_anisotropic){
                float amount = Math.min(AF_LEVEL,glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                glTexParameterf(GL_TEXTURE_2D,EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
            }else{
                System.out.println("Not supported");
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();

        } catch(IOException e) {
            e.printStackTrace();
        }
        int textureID = texture.getTextureID();
        textures.add(textureID);
        return textureID;
    }

    /**
     * Binds the list passed in to an index buffer
     * @param indices
     * List of indices associated to the vertexes
     */
    private static void bindIndicesBuffer(int[] indices) {
        int vboID = glGenBuffers();
        vbos.add(vboID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     *Stores a given set of data into a vbo
     * @param attributeNumber
     * Index in attribute list that a vao is assigned to
     * @param coordSize
     * Size of the passed in coordinate system (XYZ/UV/...)
     * @param data
     * The list of data that is being passed in and bound to the buffer
     */
    private static void storeDataInAttributeList(int attributeNumber, int coordSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, coordSize, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private static void unbindVAO() {
        glBindVertexArray(0);
    }

    private static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static void setMipmapBias(float mipmapBias) {
        ModelLoadHandler.MIPMAP_BIAS = mipmapBias;
    }

    public static void setAfLevel(float afLevel) {
        AF_LEVEL = afLevel;
    }

    /**
     * For memory management
     */
    public void cleanUp() {
        for(int vao : vaos) {
            glDeleteVertexArrays(vao);
        }
        for(int vbo : vbos) {
            glDeleteBuffers(vbo);
        }
        for(int texture: textures){
            glDeleteTextures(texture);
        }
    }
}
