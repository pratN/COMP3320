package models;

/**
 * Created by Beau on 6/09/2016.
 */
public class RawModel {
    private int vaoID;
    private int vertexCount;

    /**
     *
     * @param vaoID
     * ID of the vao this mesh will be loaded to
     * @param vertexCount
     * Number of vertices this model will have
     */
    public RawModel(int vaoID, int vertexCount){
        this.vaoID=vaoID;
        this.vertexCount=vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
