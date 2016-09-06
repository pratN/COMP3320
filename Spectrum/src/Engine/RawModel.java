package Engine;

/**
 * Created by Beau on 6/09/2016.
 */
public class RawModel {
    private int vaoID;
    private int vertexCount;


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
